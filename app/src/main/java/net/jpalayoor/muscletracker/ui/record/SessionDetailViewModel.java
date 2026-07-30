package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.SetLogWithName;
import net.jpalayoor.muscletracker.data.WorkoutSession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionDetailViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<WorkoutSession> session = new MutableLiveData<>();
    private final MutableLiveData<List<SetLogWithName>> sets = new MutableLiveData<>();

    public SessionDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<WorkoutSession> getSession() {
        return session;
    }

    public LiveData<List<SetLogWithName>> getSets() {
        return sets;
    }

    public void loadSession(int sessionId) {
        executor.execute(() -> {
            session.postValue(db.workoutSessionDao().getById(sessionId));
            sets.postValue(db.setLogDao().getSetLogWithNameByTime(sessionId));
        });
    }

    public float getTotalVolume(List<SetLogWithName> sets) {
        float total = 0;
        for (SetLogWithName s : sets) {
            total += s.weight * s.reps;
        }
        return total;
    }

    public int getTotalReps(List<SetLogWithName> sets) {
        int total = 0;
        for (SetLogWithName s : sets) {
            total += s.reps;
        }
        return total;
    }

    public void deleteById(int sessionId) {
        executor.execute(() -> {
            db.setLogDao().deleteAllForSession(sessionId);
            db.workoutSessionDao().deleteById(sessionId);
        });
    }
}