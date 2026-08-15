package net.jpalayoor.muscletracker.ui.exercises;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.Exercise;
import net.jpalayoor.muscletracker.data.SetLog;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExerciseDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean showingFirstImage = true;
    private ImageView imageView;
    private Exercise exercise;
    private boolean isNoSets = false;
    private String currentTrackingType = "weight";

    private final Runnable toggleRunnable = new Runnable() {
        @Override
        public void run() {
            if (showingFirstImage) {
                loadImageFromAssets(imageView, exercise.image1);
                showingFirstImage = false;
            }
            else {
                loadImageFromAssets(imageView, exercise.image2);
                showingFirstImage = true;
            }
            handler.postDelayed(this, 800);
        }
    };

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseDetailViewModel viewModel = new ViewModelProvider(this).get(ExerciseDetailViewModel.class);
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedUnit = prefs.getString("weight_unit", "kg");

        imageView = view.findViewById(R.id.detailImage);
        TextView textName = view.findViewById(R.id.detailExerciseName);

        LinearLayout statsSection = view.findViewById(R.id.statsSection);
        LinearLayout statsAccordion = view.findViewById(R.id.StatsAccordion);
        ImageView statsArrow = view.findViewById(R.id.detailStatsArrow);
        LinearLayout statsWeightGroup = view.findViewById(R.id.statsWeightGroup);
        TextView maxWeight = view.findViewById(R.id.detailMaxWeight);
        TextView maxVolume = view.findViewById(R.id.detailMaxVolume);
        TextView oneRM = view.findViewById(R.id.detailOneRM);
        TextView maxReps = view.findViewById(R.id.detailMaxReps);
        TextView maxDuration = view.findViewById(R.id.detailMaxDuration);

        LinearLayout instructionsSection = view.findViewById(R.id.instructionsSection);
        LinearLayout instructionsAccordion = view.findViewById(R.id.instructionsAccordion);
        ImageView instructionsArrow = view.findViewById(R.id.detailInstructionsArrow);
        TextView instructions = view.findViewById(R.id.detailInstructions);

        LinearLayout pastSetsSection = view.findViewById(R.id.pastSetsSection);
        LinearLayout pastSetsAccordion = view.findViewById(R.id.pastSetsAccordion);
        ImageView pastSetsArrow = view.findViewById(R.id.pastSetsArrow);
        TextView noPastSetsText = view.findViewById(R.id.noPastSetsText);
        LinearLayout pastSetsContainer = view.findViewById(R.id.pastSetsContainer);

        viewModel.getPastSets().observe(getViewLifecycleOwner(), sets -> {
            pastSetsContainer.removeAllViews();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            for (SetLog log : sets) {
                View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_past_set, pastSetsContainer, false);
                ((TextView) row.findViewById(R.id.textDetailSetDate)).setText(dateFormat.format(new Date(log.timestamp)));
                ((TextView) row.findViewById(R.id.textDetailSetTime)).setText(timeFormat.format(new Date(log.timestamp)));

                TextView textDetailSetWeight = row.findViewById(R.id.textDetailSetWeight);
                TextView textDetailSetReps = row.findViewById(R.id.textDetailSetReps);

                if ("reps".equals(currentTrackingType)) {
                    textDetailSetWeight.setVisibility(View.GONE);
                    textDetailSetReps.setText(getString(R.string.reps_format, log.reps));
                } else if ("time".equals(currentTrackingType)) {
                    textDetailSetWeight.setVisibility(View.GONE);
                    int dur = log.durationSeconds != null ? log.durationSeconds : 0;
                    textDetailSetReps.setText(formatDuration(dur));
                } else {
                    textDetailSetWeight.setVisibility(View.VISIBLE);
                    if (savedUnit.equals("kg")) {
                        textDetailSetWeight.setText(getString(R.string.weight_kg_format, log.weight));
                    } else {
                        textDetailSetWeight.setText(getString(R.string.weight_lb_format, log.weight * 2.2));
                    }
                    textDetailSetReps.setText(getString(R.string.reps_format, log.reps));
                }

                pastSetsContainer.addView(row);
            }

            isNoSets = sets.isEmpty();
        });

        String exerciseId = getArguments() != null ? getArguments().getString("exerciseId") : null;
        if (exerciseId != null) {
            viewModel.loadExercise(exerciseId);
        }

        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise == null) return;
            this.exercise = exercise;
            currentTrackingType = exercise.trackingType != null ? exercise.trackingType : "weight";
            textName.setText(exercise.name);
            instructions.setText(exercise.instructions.replace("|", "\n\n"));
            loadImageFromAssets(imageView, exercise.image1);
            handler.removeCallbacks(toggleRunnable);
            handler.postDelayed(toggleRunnable, 0);
        });

        viewModel.getMaxWeight().observe(getViewLifecycleOwner(), weight -> {
            String formatted = "Max Weight: -";
            if (weight != null && weight > 0.0f) {
                if (savedUnit.equals("kg")) {
                    formatted = "Max Weight: " + String.format(Locale.US, "%.2f", weight) + "kg";
                } else {
                    formatted = "Max Weight: " + String.format(Locale.US, "%.2f", weight * 2.2) + "lb";
                }
            }
            maxWeight.setText(formatted);
        });

        viewModel.getMaxVolume().observe(getViewLifecycleOwner(), volume -> {
            String formatted = "Max Volume: -";
            if (volume != null && volume > 0.0f) {
                if (savedUnit.equals("kg")) {
                    formatted = "Max Volume: " + String.format(Locale.US, "%.2f", volume) + "kg";
                } else {
                    formatted = "Max Volume: " + String.format(Locale.US, "%.2f", volume * 2.2) + "lb";
                }
            }
            maxVolume.setText(formatted);
        });

        viewModel.getOneRM().observe(getViewLifecycleOwner(), weight -> {
            String formatted = "Estimated One Rep Max: -";
            if (weight != null && weight > 0.0f) {
                if (savedUnit.equals("kg")) {
                    formatted = "Estimated One Rep Max: " + String.format(Locale.US, "%.2f", weight) + "kg";
                } else {
                    formatted = "Estimated One Rep Max: " + String.format(Locale.US, "%.2f", weight * 2.2) + "lb";
                }
            }
            oneRM.setText(formatted);
        });

        viewModel.getMaxReps().observe(getViewLifecycleOwner(), reps ->
                maxReps.setText(reps != null ? "Max Reps: " + reps : "Max Reps: -"));

        viewModel.getMaxDuration().observe(getViewLifecycleOwner(), duration ->
                maxDuration.setText(duration != null ? "Longest Hold: " + formatDuration(duration) : "Longest Hold: -"));

        statsWeightGroup.setVisibility(View.GONE);
        maxReps.setVisibility(View.GONE);
        maxDuration.setVisibility(View.GONE);
        statsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(statsSection, new ChangeBounds());
            if ("reps".equals(currentTrackingType)) {
                maxReps.setVisibility(maxReps.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else if ("time".equals(currentTrackingType)) {
                maxDuration.setVisibility(maxDuration.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else {
                statsWeightGroup.setVisibility(statsWeightGroup.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
            statsArrow.animate().rotation(statsArrow.getRotation() == 0 ? 180 : 0).setDuration(500).start();
        });

        instructions.setVisibility(View.GONE);
        instructionsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(instructionsSection, new ChangeBounds());
            instructions.setVisibility(instructions.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            instructionsArrow.animate().rotation(instructionsArrow.getRotation() == 0 ? 180 : 0).setDuration(500).start();
        });

        pastSetsContainer.setVisibility(View.GONE);
        noPastSetsText.setVisibility(View.GONE);
        pastSetsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(pastSetsSection, new ChangeBounds());
            if (isNoSets) {
                noPastSetsText.setVisibility(noPastSetsText.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else {
                noPastSetsText.setVisibility(View.GONE);
            }
            pastSetsContainer.setVisibility(pastSetsContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            pastSetsArrow.animate().rotation(pastSetsArrow.getRotation() == 0 ? 180 : 0).setDuration(500).start();
        });
    }

    private void loadImageFromAssets(ImageView imageView, String relativePath) {
        if (relativePath == null) return;
        try {
            AssetManager assetManager = requireContext().getAssets();
            InputStream is = assetManager.open("exercise_images/" + relativePath);
            imageView.setImageBitmap(BitmapFactory.decodeStream(is));
            is.close();
        } catch (IOException e) {
            Log.e("ExerciseDetail", "Could not load image: " + relativePath, e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(toggleRunnable);
    }
}