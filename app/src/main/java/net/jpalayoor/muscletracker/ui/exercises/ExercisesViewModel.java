package net.jpalayoor.muscletracker.ui.exercises;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.Exercise;

import java.util.List;

public class ExercisesViewModel extends AndroidViewModel {

    private final LiveData<List<Exercise>> allExercises;

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        allExercises = AppDatabase.getInstance(application).exerciseDao().getAllLive();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return allExercises;
    }
}