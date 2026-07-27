package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import net.jpalayoor.muscletracker.data.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveSessionViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


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
}
