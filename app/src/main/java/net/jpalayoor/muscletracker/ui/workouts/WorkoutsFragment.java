package net.jpalayoor.muscletracker.ui.workouts;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

public class WorkoutsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutsViewModel viewModel = new ViewModelProvider(this).get(WorkoutsViewModel.class);

        Button btnAddTemplate = view.findViewById(R.id.btnAddTemplate);
        btnAddTemplate.setOnClickListener(v -> {
            EditText input = new EditText(requireContext());
            new AlertDialog.Builder(requireContext())
                    .setTitle("New template")
                    .setView(input)
                    .setPositiveButton("Create", (dialog, which) -> {
                        String name = input.getText().toString();
                        if (!name.isEmpty()) {
                            viewModel.createTemplate(name);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        TemplateAdapter adapter = new TemplateAdapter(template -> {
            // click handling — wired up in the next wave
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTemplates);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllTemplates().observe(getViewLifecycleOwner(), adapter::setTemplates);
    }
}