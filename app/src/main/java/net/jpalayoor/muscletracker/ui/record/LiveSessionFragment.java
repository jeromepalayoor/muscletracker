package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.ui.workouts.TemplateExerciseAdapter;

public class LiveSessionFragment extends Fragment {
    private int sessionId;
    private TemplateExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LiveSessionViewModel viewModel = new ViewModelProvider(this).get(LiveSessionViewModel.class);

        sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        adapter = new TemplateExerciseAdapter(templateExercise -> {
            Bundle args = new Bundle();
            args.putInt("sessionId", sessionId);
            args.putString("exerciseId", templateExercise.exerciseId);
            Navigation.findNavController(view).navigate(R.id.action_live_session_to_exercise, args);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerLiveSessionExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSessionExercises().observe(getViewLifecycleOwner(), adapter::setItems);
        viewModel.loadExercisesForSession(sessionId);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Cancel workout?")
                        .setMessage("This workout won't be saved.")
                        .setPositiveButton("Yes, cancel", (dialog, which) -> {
                            viewModel.cancelSession(sessionId);
                            NavController nc = Navigation.findNavController(requireView());
                            NavOptions options = new NavOptions.Builder()
                                    .setPopUpTo(nc.getGraph().getStartDestinationId(), false)
                                    .build();
                            nc.navigate(R.id.navigation_record, null, options);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}