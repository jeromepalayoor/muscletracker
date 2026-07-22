package net.jpalayoor.muscletracker.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.workouts_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_add_template) {
                    TextInputLayout input = (TextInputLayout) LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_template, null);
                    TextInputEditText editText = input.findViewById(R.id.editTemplateName);
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("New template")
                            .setView(input)
                            .setPositiveButton("Create", (dialog, which) -> {
                                String name = editText.getText().toString();
                                if (!name.isEmpty()) {
                                    viewModel.createTemplate(name);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

        TemplateAdapter adapter = new TemplateAdapter(template -> {
            // click handling — wired up in the next wave
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTemplates);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getAllTemplates().observe(getViewLifecycleOwner(), adapter::setTemplates);
    }
}