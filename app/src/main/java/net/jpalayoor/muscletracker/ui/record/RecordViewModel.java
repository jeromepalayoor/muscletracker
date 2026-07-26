package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.WorkoutSession;

import java.util.List;

public class RecordViewModel extends AndroidViewModel {
    private final LiveData<List<WorkoutSession>> allSessions;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        allSessions = AppDatabase.getInstance(application).workoutSessionDao().getAllSessionsLive();
    }

    public LiveData<List<WorkoutSession>> getAllSessions() {
        return allSessions;
    }
}