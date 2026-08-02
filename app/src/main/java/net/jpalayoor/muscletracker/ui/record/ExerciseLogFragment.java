package net.jpalayoor.muscletracker.ui.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;

public class ExerciseLogFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_exercise_log, container, false);
    }

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int secondsRemaining;
    private TextView textRestTimer;
    private LinearLayout rowRestTimer;
    private Double suggestedWeight;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (secondsRemaining <= 0) {
                rowRestTimer.setVisibility(View.GONE);
                return;
            }
            textRestTimer.setText("Rest: " + secondsRemaining + "s");
            secondsRemaining--;
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseLogViewModel viewModel = new ViewModelProvider(this).get(ExerciseLogViewModel.class);

        String exerciseId = getArguments() != null ? getArguments().getString("exerciseId") : null;
        int sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        TextView textLogExerciseName = view.findViewById(R.id.textLogExerciseName);
        TextView textLogPrevious = view.findViewById(R.id.textLogPrevious);
        TextView textSuggestedWeight = view.findViewById(R.id.textSuggestedWeight);
        Button btnLogSet = view.findViewById(R.id.btnLogSet);
        EditText editWeight = view.findViewById(R.id.editWeight);
        EditText editReps = view.findViewById(R.id.editReps);
        Button btnFinishExercise = view.findViewById(R.id.btnFinishExercise);
        TextView textSkipTimer = view.findViewById(R.id.textSkipTimer);
        rowRestTimer = view.findViewById(R.id.rowRestTimer);
        textRestTimer = view.findViewById(R.id.textRestTimer);

        viewModel.loadExerciseData(exerciseId);
        viewModel.getExerciseName().observe(getViewLifecycleOwner(), textLogExerciseName::setText);
        viewModel.getPreviousSetText().observe(getViewLifecycleOwner(), textLogPrevious::setText);

        LoggedSetAdapter adapter = new LoggedSetAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerLoggedSets);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        rowRestTimer.setVisibility(View.GONE);

        viewModel.getLoggedSets(sessionId, exerciseId).observe(getViewLifecycleOwner(), adapter::setItems);
        viewModel.getSuggestedWeight().observe(getViewLifecycleOwner(), weight -> {
                textSuggestedWeight.setText(weight != null ? "Suggested: " + weight + "kg" : "Suggested: -");
                suggestedWeight = weight;
        });

        textSuggestedWeight.setOnClickListener(v -> {
            if (suggestedWeight != null) {
                editWeight.setText(String.valueOf(suggestedWeight));
            }
        });

        btnLogSet.setOnClickListener(v -> {
            String weightText = editWeight.getText().toString();
            String repsText = editReps.getText().toString();
            if (weightText.isEmpty() || repsText.isEmpty()) return;
            float weight = Float.parseFloat(weightText);
            int reps = Integer.parseInt(repsText);
            if (weight <= 0.0 || reps <= 0) return;
            viewModel.logSet(sessionId, exerciseId, weight, reps);
            editWeight.setText("");
            editReps.setText("");
            secondsRemaining = 90;
            rowRestTimer.setVisibility(View.VISIBLE);
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.post(timerRunnable);
        });

        textSkipTimer.setOnClickListener(v -> {
            timerHandler.removeCallbacks(timerRunnable);
            rowRestTimer.setVisibility(View.GONE);
        });

        btnFinishExercise.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
