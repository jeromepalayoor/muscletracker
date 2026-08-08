package net.jpalayoor.muscletracker.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private String pendingCsv;

    private final ActivityResultLauncher<String> createDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
                if (uri != null && pendingCsv != null) {
                    writeCsvToUri(uri, pendingCsv);
                }
            });

    private final ActivityResultLauncher<String[]> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    readCsvFromUri(uri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        MaterialButtonToggleGroup unitsToggle = view.findViewById(R.id.unitsToggle);
        MaterialButtonToggleGroup sortToggle = view.findViewById(R.id.sortToggle);
        Spinner restTimerSpinner = view.findViewById(R.id.restTimerSpinner);

        TextView btnExportData = view.findViewById(R.id.btnExportData);
        TextView btnLoadBackup = view.findViewById(R.id.btnLoadBackup);
        TextView btnDeleteLogs = view.findViewById(R.id.btnDeleteLogs);
        TextView btnResetData = view.findViewById(R.id.btnResetData);

        TextView btnHowToUse = view.findViewById(R.id.btnHowToUse);
        TextView btnAboutApp = view.findViewById(R.id.btnAboutApp);
        TextView btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);


        String savedUnit = prefs.getString("weight_unit", "kg");
        unitsToggle.check(savedUnit.equals("lb") ? R.id.btnLb : R.id.btnKg);
        unitsToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String unit = checkedId == R.id.btnLb ? "lb" : "kg";
                prefs.edit().putString("weight_unit", unit).apply();
            }
        });

        String savedSort = prefs.getString("sort_order", "time");
        sortToggle.check(savedSort.equals("name") ? R.id.btnName : R.id.btnTime);
        sortToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String sort = checkedId == R.id.btnName ? "name" : "time";
                prefs.edit().putString("sort_order", sort).apply();
            }
        });

        Integer[] restOptions = {30, 60, 90, 120, 180, 240, 300};
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, restOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restTimerSpinner.setAdapter(spinnerAdapter);

        int savedRest = prefs.getInt("rest_timer_seconds", 90);
        restTimerSpinner.setSelection(java.util.Arrays.asList(restOptions).indexOf(savedRest));

        restTimerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("rest_timer_seconds", restOptions[position]).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        viewModel.getExportCsv().observe(getViewLifecycleOwner(), csv -> {
            pendingCsv = csv;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
            String filename = "muscle_tracker_backup_" + sdf.format(new Date()) + ".csv";
            createDocumentLauncher.launch(filename);
        });
        btnExportData.setOnClickListener(v -> viewModel.buildExportCsv());

        viewModel.getImportResult().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show());

        btnLoadBackup.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Load backup?")
                .setMessage("This adds the backup's templates and history to what's already on this device — it won't replace or remove anything currently here.")
                .setPositiveButton("Choose file", (dialog, which) ->
                        openDocumentLauncher.launch(new String[]{"text/*", "text/csv", "text/comma-separated-values"}))
                .setNegativeButton("Cancel", null)
                .show());

        btnDeleteLogs.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete all workout logs?")
                .setMessage("This deletes every recorded session and set. Your templates will stay intact. This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.resetLogsOnly())
                .setNegativeButton("Cancel", null)
                .show());

        btnResetData.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reset all data?")
                .setMessage("This permanently deletes everything — every template, every exercise you've added to them, and your entire workout history. There is no way to recover this once it's done.")
                .setPositiveButton("Reset Everything", (dialog, which) -> viewModel.resetEverything())
                .setNegativeButton("Cancel", null)
                .show());

        btnAboutApp.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_settings_to_about));
        btnPrivacyPolicy.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_settings_to_privacy));
        btnHowToUse.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_settings_to_use));
    }

    private void writeCsvToUri(Uri uri, String csv) {
        try (OutputStream os = requireContext().getContentResolver().openOutputStream(uri)) {
            if (os != null) {
                os.write(csv.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(requireContext(), "Backup saved", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Failed to save backup", Toast.LENGTH_SHORT).show();
        }
    }

    private void readCsvFromUri(Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            if (is == null) return;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                byteStream.write(buffer, 0, length);
            }
            String content = byteStream.toString("UTF-8");
            viewModel.importCsv(content);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Failed to read backup", Toast.LENGTH_SHORT).show();
        }
    }
}