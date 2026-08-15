package net.jpalayoor.muscletracker.ui.exercises;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.Exercise;
import net.jpalayoor.muscletracker.data.SetLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseDetailViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MutableLiveData<Exercise> exercise = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Float> maxWeight = new MutableLiveData<>();
    private final MutableLiveData<Float> maxVolume = new MutableLiveData<>();
    private final MutableLiveData<Float> oneRM = new MutableLiveData<>();
    private final MutableLiveData<Integer> maxReps = new MutableLiveData<>();
    private final MutableLiveData<Integer> maxDuration = new MutableLiveData<>();
    private final MutableLiveData<List<SetLog>> pastSets = new MutableLiveData<>();

    public ExerciseDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }

    public void loadExercise(String exerciseId) {
        executor.execute(() -> {
            exercise.postValue(db.exerciseDao().getById(exerciseId));
            maxWeight.postValue(db.setLogDao().getMaxWeight(exerciseId));
            maxVolume.postValue(db.setLogDao().getMaxVolume(exerciseId));
            oneRM.postValue(db.setLogDao().getBestEstimatedOneRepMax(exerciseId));
            maxReps.postValue(db.setLogDao().getMaxReps(exerciseId));
            maxDuration.postValue(db.setLogDao().getMaxDuration(exerciseId));
            pastSets.postValue(db.setLogDao().getAllForExercise(exerciseId));
        });
    }

    public LiveData<Float> getMaxWeight() {
        return maxWeight;
    }

    public LiveData<Float> getMaxVolume() {
        return maxVolume;
    }

    public LiveData<Float> getOneRM() {
        return oneRM;
    }

    public LiveData<Integer> getMaxReps() {
        return maxReps;
    }

    public LiveData<Integer> getMaxDuration() {
        return maxDuration;
    }

    public LiveData<List<SetLog>> getPastSets() {
        return pastSets;
    }
}