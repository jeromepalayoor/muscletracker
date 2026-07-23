package net.jpalayoor.muscletracker.ui.workouts;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

import java.util.Objects;

public class ExercisePickerFragment extends Fragment {
    private int templateId;
    private ExercisePickerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExercisePickerViewModel viewModel = new ViewModelProvider(this).get(ExercisePickerViewModel.class);
        TemplateDetailViewModel detailViewModel = new ViewModelProvider(this).get(TemplateDetailViewModel.class);

        adapter = new ExercisePickerAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerExercisePicker);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), adapter::setExercises);
        viewModel.search("");

        EditText editSearch = view.findViewById(R.id.editSearchExercises);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        templateId = getArguments() != null ? getArguments().getInt("templateId") : -1;
        String templateName = getArguments() != null ? getArguments().getString("templateName") : "";
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(templateName + " Exercises");

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.exercise_picker_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_add_selected) {
                    detailViewModel.addExercisesToTemplate(templateId, adapter.getSelectedIds());
                    Navigation.findNavController(view).navigateUp();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());
    }
}
