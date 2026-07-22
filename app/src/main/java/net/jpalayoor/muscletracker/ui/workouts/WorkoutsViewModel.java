package net.jpalayoor.muscletracker.ui.workouts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutsViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<WorkoutTemplate>> allTemplates;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WorkoutsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allTemplates = db.workoutTemplateDao().getAllLive();
    }

    public LiveData<List<WorkoutTemplate>> getAllTemplates() {
        return allTemplates;
    }

    public void createTemplate(String name) {
        executor.execute(() -> {
            WorkoutTemplate template = new WorkoutTemplate();
            template.name = name;
            db.workoutTemplateDao().insert(template);
        });
    }
}
