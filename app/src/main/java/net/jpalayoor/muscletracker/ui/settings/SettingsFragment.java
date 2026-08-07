package net.jpalayoor.muscletracker.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;

import net.jpalayoor.muscletracker.R;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        MaterialButtonToggleGroup unitsToggle = view.findViewById(R.id.unitsToggle);
        MaterialButtonToggleGroup sortToggle = view.findViewById(R.id.sortToggle);
        Spinner restTimerSpinner = view.findViewById(R.id.restTimerSpinner);

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
    }
}