package net.jpalayoor.muscletracker.ui.record;

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

public class ExerciseLogViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> exerciseName = new MutableLiveData<>();
    private final MutableLiveData<SetLog> prev = new MutableLiveData<>();
    private final MutableLiveData<Double> suggestedWeight = new MutableLiveData<>();
    private final MutableLiveData<Integer> suggestedReps = new MutableLiveData<>();
    private final MutableLiveData<Integer> suggestedDuration = new MutableLiveData<>();
    private final MutableLiveData<String> trackingType = new MutableLiveData<>();

    public ExerciseLogViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<SetLog>> getLoggedSets(int sessionId, String exerciseId) {
        return db.setLogDao().getForSessionAndExerciseLive(sessionId, exerciseId);
    }

    public LiveData<String> getExerciseName() {
        return exerciseName;
    }

    public LiveData<SetLog> getPreviousSet() {
        return prev;
    }

    public LiveData<String> getTrackingType() {
        return trackingType;
    }

    public LiveData<Integer> getSuggestedReps() {
        return suggestedReps;
    }

    public LiveData<Integer> getSuggestedDuration() {
        return suggestedDuration;
    }

    public void loadExerciseData(String exerciseId) {
        executor.execute(() -> {
            Exercise exercise = db.exerciseDao().getById(exerciseId);
            exerciseName.postValue(exercise.name);
            trackingType.postValue(exercise.trackingType);
            SetLog log = db.setLogDao().getMostRecentForExercise(exerciseId);
            prev.postValue(log);
            Float recentOneRM = db.setLogDao().getRecentEstimatedOneRepMax(exerciseId);
            suggestedWeight.postValue(recentOneRM != null ? Math.round(recentOneRM * 0.85 / 5.0) * 5.0 : null);
            suggestedReps.postValue(db.setLogDao().getRecentMaxReps(exerciseId));
            suggestedDuration.postValue(db.setLogDao().getRecentMaxDuration(exerciseId));
        });
    }

    public void logSet(int sessionId, String exerciseId, float weight, int reps, long durationSeconds) {
        executor.execute(() -> {
            SetLog log = new SetLog();
            log.sessionId = sessionId;
            log.exerciseId = exerciseId;
            log.weight = weight;
            log.reps = reps;
            log.setNumber = db.setLogDao().countForExerciseInSession(sessionId, exerciseId);
            log.timestamp = System.currentTimeMillis();
            log.durationSeconds = durationSeconds > 0 ? (int) durationSeconds : null;
            db.setLogDao().insert(log);
            Float recentOneRM = db.setLogDao().getRecentEstimatedOneRepMax(exerciseId);
            if (recentOneRM != null) {
                suggestedWeight.postValue(Math.round(recentOneRM * 0.85 / 5.0) * 5.0);
            }
            else {
                suggestedWeight.postValue(null);
            }
            suggestedReps.postValue(db.setLogDao().getRecentMaxReps(exerciseId));
            suggestedDuration.postValue(db.setLogDao().getRecentMaxDuration(exerciseId));
        });
    }

    public void deleteSet(int id) {
        executor.execute(() -> {
            db.setLogDao().deleteById(id);
        });
    }

    public LiveData<Double> getSuggestedWeight() {
        return suggestedWeight;
    }
}
