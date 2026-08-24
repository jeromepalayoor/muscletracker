package net.jpalayoor.muscletracker.ui.record;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLogWithName;
import net.jpalayoor.muscletracker.data.WorkoutSession;
import net.jpalayoor.muscletracker.util.FitFileGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SessionDetailFragment extends Fragment {
    private List<SetLogWithName> currentSets;
    private WorkoutSession currentSession;
    private File pendingFitFile;
    private String pendingFitName;

    private final ActivityResultLauncher<String> requestStoragePermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted && pendingFitFile != null) {
                    saveFitToDownloads(pendingFitFile, pendingFitName);
                } else {
                    Toast.makeText(requireContext(), "Storage permission needed to save the file", Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionDetailViewModel viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedUnit = prefs.getString("weight_unit", "kg");
        String savedOrder = prefs.getString("sort_order", "time");

        TextView textSessionDetailName = view.findViewById(R.id.textSessionDetailName);
        TextView textSessionDetailDate = view.findViewById(R.id.textSessionDetailDate);
        TextView textSessionDetailDuration = view.findViewById(R.id.textSessionDetailDuration);
        TextView textSessionDetailVolume = view.findViewById(R.id.textSessionDetailVolume);
        TextView textSessionDetailSets = view.findViewById(R.id.textSessionDetailSets);
        TextView textSessionDetailReps = view.findViewById(R.id.textSessionDetailReps);
        LinearLayout setsContainer = view.findViewById(R.id.setsContainer);

        int sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        viewModel.loadSession(sessionId, savedOrder);
        viewModel.getSession().observe(getViewLifecycleOwner(), ws -> {
            currentSession = ws;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy, hh:mm aa", Locale.getDefault());
            String date = "Date: " + sdf.format(new Date(ws.startTime));
            String duration = "Duration: ";
            if (ws.endTime != null) {
                long minutes = ((ws.endTime - ws.startTime) / 1000) / 60;
                if (minutes >= 60) {
                    duration += minutes / 60;
                    duration += "h ";
                    minutes %= 60;
                }
                duration += minutes + "min";
            } else {
                duration += "-";
            }
            String name = ws.templateName + " Session";
            textSessionDetailName.setText(name);
            textSessionDetailDate.setText(date);
            textSessionDetailDuration.setText(duration);
        });

        viewModel.getSets().observe(getViewLifecycleOwner(), logSets -> {
            currentSets = logSets;
            int reps = viewModel.getTotalReps(logSets);
            float volume = viewModel.getTotalVolume(logSets);
            textSessionDetailReps.setText(String.valueOf(reps));
            textSessionDetailSets.setText(String.valueOf(logSets.size()));
            if (savedUnit.equals("kg")) {
                textSessionDetailVolume.setText(getString(R.string.weight_kg_format, volume));
            } else {
                textSessionDetailVolume.setText(getString(R.string.weight_lb_format, volume * 2.2));
            }

            setsContainer.removeAllViews();
            for (int i = 0; i < logSets.size(); i++) {
                SetLogWithName setLog = logSets.get(i);
                View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_detail_set, setsContainer, false);

                TextView textDetailSetExercise = row.findViewById(R.id.textDetailSetExercise);
                TextView textDetailSetNumber = row.findViewById(R.id.textDetailSetNumber);
                TextView textDetailSetWeight = row.findViewById(R.id.textDetailSetWeight);
                TextView textDetailSetReps = row.findViewById(R.id.textDetailSetReps);
                TextView textDetailPR = row.findViewById(R.id.textDetailPR);

                textDetailSetExercise.setText(setLog.name);
                textDetailSetNumber.setText(getString(R.string.set_number_format, setLog.setNumber + 1));

                if ("reps".equals(setLog.trackingType)) {
                    textDetailSetWeight.setVisibility(View.GONE);
                    textDetailSetReps.setText(getString(R.string.reps_format, setLog.reps));
                } else if ("time".equals(setLog.trackingType)) {
                    textDetailSetWeight.setVisibility(View.GONE);
                    int dur = setLog.durationSeconds != null ? setLog.durationSeconds : 0;
                    textDetailSetReps.setText(formatDuration(dur));
                } else {
                    textDetailSetWeight.setVisibility(View.VISIBLE);
                    if (savedUnit.equals("kg")) {
                        textDetailSetWeight.setText(getString(R.string.weight_kg_format, setLog.weight));
                    } else {
                        textDetailSetWeight.setText(getString(R.string.weight_lb_format, setLog.weight * 2.2));
                    }
                    textDetailSetReps.setText(getString(R.string.reps_format, setLog.reps));
                }

                if (i > 0 && Objects.equals(logSets.get(i - 1).name, setLog.name)) {
                    textDetailSetExercise.setVisibility(View.GONE);
                } else {
                    textDetailSetExercise.setVisibility(View.VISIBLE);
                }

                textDetailPR.setVisibility(setLog.isPR ? View.VISIBLE : View.GONE);

                setsContainer.addView(row);
            }
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.past_session_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_session) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete session?")
                            .setMessage("This cannot be undone.")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                viewModel.deleteById(sessionId);
                                Navigation.findNavController(view).popBackStack();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
                else if (menuItem.getItemId() == R.id.action_share_session) {
                    if (currentSession != null && currentSession.endTime != null && currentSets != null) {
                        try {
                            File tempFile = new File(requireContext().getCacheDir(), "workout.fit");
                            FitFileGenerator.generate(tempFile, currentSession.startTime, currentSession.endTime, currentSets);
                            saveFitToDownloads(tempFile, "workout_" + currentSession.id + ".fit");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.strava.com/upload/select"));
                            intent.setPackage("com.android.chrome");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                intent.setPackage(null);
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e2) {
                                    Toast.makeText(requireContext(), "No browser found to open Strava upload", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "FIT generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }

    private void saveFitToDownloads(File sourceFile, String displayName) {
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, displayName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = requireContext().getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(requireContext(), "Could not create file in Downloads", Toast.LENGTH_LONG).show();
                return;
            }
            try (OutputStream os = requireContext().getContentResolver().openOutputStream(uri);
                 FileInputStream fis = new FileInputStream(sourceFile)) {
                if (os == null) {
                    Toast.makeText(requireContext(), "Could not open file for writing", Toast.LENGTH_LONG).show();
                    return;
                }
                copyStream(fis, os);
                Toast.makeText(requireContext(), "Saved to Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Failed to save file", Toast.LENGTH_LONG).show();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                pendingFitFile = sourceFile;
                pendingFitName = displayName;
                requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File destFile = new File(downloadsDir, displayName);
            try (FileOutputStream fos = new FileOutputStream(destFile);
                 FileInputStream fis = new FileInputStream(sourceFile)) {
                copyStream(fis, fos);
                Toast.makeText(requireContext(), "Saved to Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Failed to save file", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
    }
}