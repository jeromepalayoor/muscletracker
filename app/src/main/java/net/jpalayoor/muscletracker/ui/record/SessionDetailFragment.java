package net.jpalayoor.muscletracker.ui.record;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLogWithName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SessionDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
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
                textDetailSetReps.setText(getString(R.string.reps_format, setLog.reps));
                if (savedUnit.equals("kg")) {
                    textDetailSetWeight.setText(getString(R.string.weight_kg_format, setLog.weight));
                } else {
                    textDetailSetWeight.setText(getString(R.string.weight_lb_format, setLog.weight * 2.2));
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
                    // future feature
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }
}