package net.jpalayoor.muscletracker.ui.exercises;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

public class ExercisesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExercisesViewModel viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);

        ExerciseAdapter adapter = new ExerciseAdapter(exercise -> {
            Bundle args = new Bundle();
            args.putString("exerciseId", exercise.exerciseId);
            Navigation.findNavController(view).navigate(R.id.action_exercises_to_detail, args);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerExercises);
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
    }
}