package net.jpalayoor.muscletracker.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

public class TemplateDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TemplateDetailViewModel viewModel = new ViewModelProvider(this).get(TemplateDetailViewModel.class);

        TemplateExerciseAdapter adapter = new TemplateExerciseAdapter(templateExercise -> {
            // click handling — later wave, reusing ExerciseDetailFragment
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTemplateExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        int templateId = getArguments() != null ? getArguments().getInt("templateId") : -1;
        if (templateId != -1) {
            viewModel.getExercisesForTemplate(templateId).observe(getViewLifecycleOwner(), adapter::setItems);
        }
    }
}
