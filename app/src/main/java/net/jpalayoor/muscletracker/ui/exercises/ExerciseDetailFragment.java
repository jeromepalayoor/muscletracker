package net.jpalayoor.muscletracker.ui.exercises;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jpalayoor.muscletracker.R;
import net.jpalayoor.muscletracker.data.Exercise;

import java.io.IOException;
import java.io.InputStream;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseDetailViewModel viewModel = new ViewModelProvider(this).get(ExerciseDetailViewModel.class);

        imageView = view.findViewById(R.id.detailImage);
        TextView textName = view.findViewById(R.id.detailExerciseName);

        LinearLayout statsSection = view.findViewById(R.id.statsSection);
        LinearLayout statsAccordion = view.findViewById(R.id.StatsAccordion);
        ImageView statsArrow = view.findViewById(R.id.detailStatsArrow);
        TextView maxWeight = view.findViewById(R.id.detailMaxWeight);
        TextView maxVolume = view.findViewById(R.id.detailMaxVolume);
        TextView oneRM = view.findViewById(R.id.detailOneRM);

        LinearLayout instructionsSection = view.findViewById(R.id.instructionsSection);
        LinearLayout instructionsAccordion = view.findViewById(R.id.instructionsAccordion);
        ImageView instructionsArrow = view.findViewById(R.id.detailInstructionsArrow);
        TextView instructions = view.findViewById(R.id.detailInstructions);

        LinearLayout pastSetsSection = view.findViewById(R.id.pastSetsSection);
        LinearLayout pastSetsAccordion = view.findViewById(R.id.pastSetsAccordion);
        ImageView pastSetsArrow = view.findViewById(R.id.pastSetsArrow);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerPastSets);

        PastSetAdapter adapter = new PastSetAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        String exerciseId = getArguments() != null ? getArguments().getString("exerciseId") : null;
        if (exerciseId != null) {
            viewModel.loadExercise(exerciseId);
        }

        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise == null) return;
            this.exercise = exercise;
            textName.setText(exercise.name);
            instructions.setText(exercise.instructions.replace("|", "\n\n"));
            loadImageFromAssets(imageView, exercise.image1);
            handler.removeCallbacks(toggleRunnable);
            handler.postDelayed(toggleRunnable, 0);
        });

        viewModel.getMaxWeight().observe(getViewLifecycleOwner(), weight -> {
            maxWeight.setText("Max Weight: -");
            if (weight != null && weight > 0.0f) {
                maxWeight.setText("Max Weight: " + String.format(Locale.US, "%.2f", weight) + "kg");
            }
        });

        viewModel.getMaxVolume().observe(getViewLifecycleOwner(), volume -> {
            maxVolume.setText("Max Volume: -");
            if (volume != null && volume > 0.0f) {
                maxVolume.setText("Max Volume: " + String.format(Locale.US, "%.2f", volume) + "kg");
            }
        });

        viewModel.getOneRM().observe(getViewLifecycleOwner(), weight -> {
            oneRM.setText("Estimated One Rep Max: -");
            if (weight != null && weight > 0.0f) {
                oneRM.setText("Estimated One Rep Max: " + String.format(Locale.US, "%.2f", weight) + "kg");
            }
        });

        viewModel.getPastSets().observe(getViewLifecycleOwner(), adapter::setItems);

        maxWeight.setVisibility(View.GONE);
        maxVolume.setVisibility(View.GONE);
        oneRM.setVisibility(View.GONE);
        statsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(statsSection, new ChangeBounds());
            maxWeight.setVisibility(maxWeight.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            maxVolume.setVisibility(maxVolume.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            oneRM.setVisibility(oneRM.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            statsArrow.animate().rotation(statsArrow.getRotation() == 0 ? 180 : 0).setDuration(500).start();
        });

        instructions.setVisibility(View.GONE);
        instructionsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(instructionsSection, new ChangeBounds());
            instructions.setVisibility(instructions.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            instructionsArrow.animate().rotation(instructionsArrow.getRotation() == 0 ? 180 : 0).setDuration(500).start();
        });

        recyclerView.setVisibility(View.GONE);
        pastSetsAccordion.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(pastSetsSection, new ChangeBounds());
            recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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