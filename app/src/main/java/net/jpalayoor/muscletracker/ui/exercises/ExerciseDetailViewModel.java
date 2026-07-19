package net.jpalayoor.muscletracker.ui.exercises;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.Exercise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseDetailViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MutableLiveData<Exercise> exercise = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExerciseDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }

    public void loadExercise(String exerciseId) {
        executor.execute(() -> exercise.postValue(db.exerciseDao().getById(exerciseId)));
    }
}