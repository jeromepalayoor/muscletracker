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
    private String currentTrackingType = "weight";
    private SetLog currentPrevSet;
    private List<SetLog> currentSets = new ArrayList<>();
    private TextView textLogPrevious;
    private String savedUnit;

    private int elapsedSeconds;
    private boolean stopwatchRunning = false;
    private TextView textElapsedTime;
    private Button btnStartStopTimer;

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

    private final Runnable stopwatchRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds++;
            textElapsedTime.setText(formatElapsedTime(elapsedSeconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    private String formatElapsedTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    private void updatePreviousText() {
        String previous;
        if (currentPrevSet == null) {
            previous = "Previous: -";
        } else if ("reps".equals(currentTrackingType)) {
            previous = "Previous: " + currentPrevSet.reps + " reps";
        } else if ("time".equals(currentTrackingType)) {
            int dur = currentPrevSet.durationSeconds != null ? currentPrevSet.durationSeconds : 0;
            previous = "Previous: " + formatElapsedTime(dur);
        } else {
            String weightFormatted = savedUnit.equals("kg")
                    ? getString(R.string.weight_kg_format, currentPrevSet.weight)
                    : getString(R.string.weight_lb_format, currentPrevSet.weight * 2.2);
            previous = "Previous: " + weightFormatted + " · " + currentPrevSet.reps + " reps";
        }
        textLogPrevious.setText(previous);
    }

    private void renderLoggedSets(LinearLayout loggedSetsContainer, String savedUnit) {
        loggedSetsContainer.removeAllViews();
        for (SetLog log : currentSets) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_logged_set, loggedSetsContainer, false);
            ((TextView) row.findViewById(R.id.textSetNumber)).setText(getString(R.string.set_number_format, log.setNumber + 1));

            TextView textSetWeight = row.findViewById(R.id.textSetWeight);
            TextView textSetReps = row.findViewById(R.id.textSetReps);

            if ("reps".equals(currentTrackingType)) {
                textSetWeight.setVisibility(View.GONE);
                textSetReps.setText(getString(R.string.reps_format, log.reps));
            } else if ("time".equals(currentTrackingType)) {
                textSetWeight.setVisibility(View.GONE);
                int dur = log.durationSeconds != null ? log.durationSeconds : 0;
                textSetReps.setText(formatElapsedTime(dur));
            } else {
                textSetWeight.setVisibility(View.VISIBLE);
                if (savedUnit.equals("kg")) {
                    textSetWeight.setText(getString(R.string.weight_kg_format, log.weight));
                } else {
                    textSetWeight.setText(getString(R.string.weight_lb_format, log.weight * 2.2));
                }
                textSetReps.setText(getString(R.string.reps_format, log.reps));
            }
            loggedSetsContainer.addView(row);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseLogViewModel viewModel = new ViewModelProvider(this).get(ExerciseLogViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        savedUnit = prefs.getString("weight_unit", "kg");
        int savedRest = prefs.getInt("rest_timer_seconds", 90);

        String exerciseId = getArguments() != null ? getArguments().getString("exerciseId") : null;
        int sessionId = getArguments() != null ? getArguments().getInt("sessionId") : -1;

        TextView textLogExerciseName = view.findViewById(R.id.textLogExerciseName);
        textLogPrevious = view.findViewById(R.id.textLogPrevious);
        TextView textSuggestedWeight = view.findViewById(R.id.textSuggestedWeight);
        TextView textSuggestedReps = view.findViewById(R.id.textSuggestedReps);
        TextView textSuggestedDuration = view.findViewById(R.id.textSuggestedDuration);
        Button btnLogSet = view.findViewById(R.id.btnLogSet);
        Button btnLogReps = view.findViewById(R.id.btnLogReps);
        EditText editWeight = view.findViewById(R.id.editWeight);
        EditText editReps = view.findViewById(R.id.editReps);
        EditText editRepsOnly = view.findViewById(R.id.editRepsOnly);
        Button btnFinishExercise = view.findViewById(R.id.btnFinishExercise);
        TextView textSkipTimer = view.findViewById(R.id.textSkipTimer);
        TextView undoText = view.findViewById(R.id.undoText);
        LinearLayout loggedSetsContainer = view.findViewById(R.id.loggedSetsContainer);
        LinearLayout groupWeight = view.findViewById(R.id.groupWeight);
        LinearLayout groupReps = view.findViewById(R.id.groupReps);
        LinearLayout groupTime = view.findViewById(R.id.groupTime);
        textElapsedTime = view.findViewById(R.id.textElapsedTime);
        btnStartStopTimer = view.findViewById(R.id.btnStartStopTimer);
        rowRestTimer = view.findViewById(R.id.rowRestTimer);
        textRestTimer = view.findViewById(R.id.textRestTimer);

        viewModel.loadExerciseData(exerciseId);
        viewModel.getExerciseName().observe(getViewLifecycleOwner(), textLogExerciseName::setText);

        viewModel.getTrackingType().observe(getViewLifecycleOwner(), type -> {
            currentTrackingType = type != null ? type : "weight";
            groupWeight.setVisibility("weight".equals(currentTrackingType) ? View.VISIBLE : View.GONE);
            groupReps.setVisibility("reps".equals(currentTrackingType) ? View.VISIBLE : View.GONE);
            groupTime.setVisibility("time".equals(currentTrackingType) ? View.VISIBLE : View.GONE);
            updatePreviousText();
            renderLoggedSets(loggedSetsContainer, savedUnit);
        });

        viewModel.getPreviousSet().observe(getViewLifecycleOwner(), prev -> {
            currentPrevSet = prev;
            updatePreviousText();
        });

        rowRestTimer.setVisibility(View.GONE);

        editWeight.setHint(savedUnit.equals("kg") ? getString(R.string.weight_kg_text) : getString(R.string.weight_lb_text));

        viewModel.getLoggedSets(sessionId, exerciseId).observe(getViewLifecycleOwner(), sets -> {
            currentSets = sets;
            renderLoggedSets(loggedSetsContainer, savedUnit);
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

        viewModel.getSuggestedReps().observe(getViewLifecycleOwner(), reps ->
                textSuggestedReps.setText(reps != null ? "Suggested: " + reps + " reps" : "Suggested: -"));

        viewModel.getSuggestedDuration().observe(getViewLifecycleOwner(), duration ->
                textSuggestedDuration.setText(duration != null ? "Suggested: " + formatElapsedTime(duration) : "Suggested: -"));

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
            viewModel.logSet(sessionId, exerciseId, weight, reps, 0);
            editWeight.setText("");
            editReps.setText("");
            startRestTimer(savedRest);
        });

        btnLogReps.setOnClickListener(v -> {
            String repsText = editRepsOnly.getText().toString();
            if (repsText.isEmpty()) return;
            int reps = Integer.parseInt(repsText);
            if (reps <= 0) return;
            viewModel.logSet(sessionId, exerciseId, 0f, reps, 0);
            editRepsOnly.setText("");
            startRestTimer(savedRest);
        });

        btnStartStopTimer.setOnClickListener(v -> {
            if (!stopwatchRunning) {
                stopwatchRunning = true;
                elapsedSeconds = 0;
                textElapsedTime.setText(formatElapsedTime(elapsedSeconds));
                btnStartStopTimer.setText(getString(R.string.stop_text));
                timerHandler.postDelayed(stopwatchRunnable, 1000);
            } else {
                stopwatchRunning = false;
                timerHandler.removeCallbacks(stopwatchRunnable);
                btnStartStopTimer.setText(getString(R.string.start_text));
                viewModel.logSet(sessionId, exerciseId, 0f, 0, elapsedSeconds);
                textElapsedTime.setText(getString(R.string.zero_seconds_text));
                startRestTimer(savedRest);
            }
        });

        textSkipTimer.setOnClickListener(v -> {
            timerHandler.removeCallbacks(timerRunnable);
            rowRestTimer.setVisibility(View.GONE);
        });

        btnFinishExercise.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        undoText.setOnClickListener(v -> {
            if (!currentSets.isEmpty()) {
                SetLog last = currentSets.get(currentSets.size() - 1);
                viewModel.deleteSet(last.id);
            }
        });
    }

    private void startRestTimer(int seconds) {
        secondsRemaining = seconds;
        rowRestTimer.setVisibility(View.VISIBLE);
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.removeCallbacks(stopwatchRunnable);
    }
}