package net.jpalayoor.muscletracker.ui.exercises;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.jpalayoor.muscletracker.R;

import java.io.IOException;
import java.io.InputStream;

public class ExerciseDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExerciseDetailViewModel viewModel = new ViewModelProvider(this).get(ExerciseDetailViewModel.class);

        TextView textName = view.findViewById(R.id.detailExerciseName);
        TextView textInstructions = view.findViewById(R.id.detailInstructions);
        ImageView imageView = view.findViewById(R.id.detailImage);

        viewModel.getExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise == null) return;
            textName.setText(exercise.name);
            textInstructions.setText(exercise.instructions.replace("|", "\n\n"));
            loadImageFromAssets(imageView, exercise.image1);
        });

        String exerciseId = getArguments() != null ? getArguments().getString("exerciseId") : null;
        if (exerciseId != null) {
            viewModel.loadExercise(exerciseId);
        }
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
}