package net.jpalayoor.muscletracker.ui.record;

import android.content.Context;
import android.content.SharedPreferences;
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

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.SetLog;

import java.util.ArrayList;
import java.util.List;

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
    private List<SetLog> currentLoggedSets = new ArrayList<>();

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (secondsRemaining <= 0) {
                rowRestTimer.setVisibility(View.GONE);
                return;
            }
            String timerText = "Rest: " + secondsRemaining + "s";
            textRestTimer.setText(timerText);
            secondsRemaining--;
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseLogViewModel viewModel = new ViewModelProvider(this).get(ExerciseLogViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedUnit = prefs.getString("weight_unit", "kg");
        int savedRest = prefs.getInt("rest_timer_seconds", 90);

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
        TextView undoText = view.findViewById(R.id.undoText);
        LinearLayout loggedSetsContainer = view.findViewById(R.id.loggedSetsContainer);
        rowRestTimer = view.findViewById(R.id.rowRestTimer);
        textRestTimer = view.findViewById(R.id.textRestTimer);

        viewModel.loadExerciseData(exerciseId);
        viewModel.getExerciseName().observe(getViewLifecycleOwner(), textLogExerciseName::setText);
        viewModel.getPreviousSet().observe(getViewLifecycleOwner(), prev -> {
            String previous;
            if (prev != null) {
                if (savedUnit.equals("kg")) {
                    previous = "Previous: " + getString(R.string.weight_kg_format, prev.weight) + " · " + prev.reps + " reps";
                } else {
                    previous = "Previous: " + getString(R.string.weight_lb_format, prev.weight * 2.2) + " · " + prev.reps + " reps";
                }
            } else {
                previous = "Previous: -";
            }
            textLogPrevious.setText(previous);
        });

        rowRestTimer.setVisibility(View.GONE);

        editWeight.setHint(savedUnit.equals("kg") ? getString(R.string.weight_kg_text) : getString(R.string.weight_lb_text));

        viewModel.getLoggedSets(sessionId, exerciseId).observe(getViewLifecycleOwner(), sets -> {
            loggedSetsContainer.removeAllViews();
            currentLoggedSets = sets;
            for (SetLog log : sets) {
                View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_logged_set, loggedSetsContainer, false);
                ((TextView) row.findViewById(R.id.textSetNumber)).setText(getString(R.string.set_number_format, log.setNumber + 1));
                if (savedUnit.equals("kg")) {
                    ((TextView) row.findViewById(R.id.textSetWeight)).setText(getString(R.string.weight_kg_format, log.weight));
                } else {
                    ((TextView) row.findViewById(R.id.textSetWeight)).setText(getString(R.string.weight_lb_format, log.weight * 2.2));
                }
                ((TextView) row.findViewById(R.id.textSetReps)).setText(getString(R.string.reps_format, log.reps));
                loggedSetsContainer.addView(row);
            }
        });

        viewModel.getSuggestedWeight().observe(getViewLifecycleOwner(), weight -> {
            if (weight != null) {
                double displayWeight = savedUnit.equals("kg") ? weight : weight * 2.2;
                String formatted = savedUnit.equals("kg")
                        ? getString(R.string.weight_kg_format, displayWeight)
                        : getString(R.string.weight_lb_format, displayWeight);
                String suggested = "Suggested: " + formatted;
                textSuggestedWeight.setText(suggested);
                suggestedWeight = displayWeight;
            } else {
                String suggested = "Suggested: -";
                textSuggestedWeight.setText(suggested);
                suggestedWeight = null;
            }
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
            weight = savedUnit.equals("kg") ? weight : (float) (weight / 2.2);
            viewModel.logSet(sessionId, exerciseId, weight, reps);
            editWeight.setText("");
            editReps.setText("");
            secondsRemaining = savedRest;
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

        undoText.setOnClickListener(v -> {
            if (!currentLoggedSets.isEmpty()) {
                SetLog last = currentLoggedSets.get(currentLoggedSets.size() - 1);
                viewModel.deleteSet(last.id);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
    }
}