package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.ui.workouts.TemplateAdapter;

public class StartWorkoutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StartWorkoutViewModel viewModel = new ViewModelProvider(this).get(StartWorkoutViewModel.class);

        viewModel.getNewSessionId().observe(getViewLifecycleOwner(), sessionId -> {
            if (sessionId != null) {
                Bundle args = new Bundle();
                args.putInt("sessionId", sessionId.intValue());
                Navigation.findNavController(view).navigate(R.id.action_start_workout_to_live_session, args);
            }
        });

        TemplateAdapter adapter = new TemplateAdapter(viewModel::createSession);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerStartWorkoutTemplates);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        TextView noTemplates = view.findViewById(R.id.noTemplates);

        viewModel.getAllTemplates().observe(getViewLifecycleOwner(), templates -> {
            adapter.setTemplates(templates);
            if (templates.isEmpty()) {
                noTemplates.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noTemplates.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}
