package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;
import net.jpalayoor.muscletracker.data.WorkoutSession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveSessionViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<TemplateExerciseWithName>> sessionExercises = new MutableLiveData<>();

    public LiveSessionViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public void cancelSession(int sessionId) {
        executor.execute(() -> {
            db.setLogDao().deleteAllForSession(sessionId);
            db.workoutSessionDao().deleteById(sessionId);
        });
    }

    public LiveData<List<TemplateExerciseWithName>> getSessionExercises() {
        return sessionExercises;
    }

    public void loadExercisesForSession(int sessionId) {
        executor.execute(() -> {
            WorkoutSession session = db.workoutSessionDao().getById(sessionId);
            if (session != null) {
                List<TemplateExerciseWithName> exercises = db.templateExerciseDao().getExercisesWithNames(session.templateId);
                sessionExercises.postValue(exercises);
            }
        });
    }
}
