package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.SetLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseLogViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> exerciseName = new MutableLiveData<>();
    private final MutableLiveData<String> prev = new MutableLiveData<>();

    public ExerciseLogViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<SetLog>> getLoggedSets(int sessionId, String exerciseId) {
        return db.setLogDao().getForSessionAndExerciseLive(sessionId, exerciseId);
    }

    public MutableLiveData<String> getExerciseName() {
        return exerciseName;
    }

    public MutableLiveData<String> getPreviousSetText() {
        return prev;
    }

    public void loadExerciseData(String exerciseId) {
        executor.execute(() -> {
            exerciseName.postValue(db.exerciseDao().getById(exerciseId).name);
            SetLog log = db.setLogDao().getMostRecentForExercise(exerciseId);
            if (log != null) {
                prev.postValue("Previous: " + log.weight + "kg x " + log.reps);
            }
            else {
                prev.postValue("Previous: -");
            }
        });
    }

    public void logSet(int sessionId, String exerciseId, float weight, int reps) {
        executor.execute(() -> {
            SetLog log = new SetLog();
            log.sessionId = sessionId;
            log.exerciseId = exerciseId;
            log.weight = weight;
            log.reps = reps;
            log.setNumber = db.setLogDao().countForExerciseInSession(sessionId, exerciseId);
            log.timestamp = System.currentTimeMillis();
            db.setLogDao().insert(log);
        });
    }
}
