package net.jpalayoor.muscletracker.ui.workouts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;
import net.jpalayoor.muscletracker.data.WorkoutTemplateWithCount;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutsViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final LiveData<List<WorkoutTemplateWithCount>> allTemplates;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WorkoutsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allTemplates = db.workoutTemplateDao().getAllTemplatesWithCount();
    }

    public LiveData<List<WorkoutTemplateWithCount>> getAllTemplates() {
        return allTemplates;
    }

    public void createTemplate(String name) {
        executor.execute(() -> {
            WorkoutTemplate template = new WorkoutTemplate();
            template.name = name;
            db.workoutTemplateDao().insert(template);
        });
    }

    public void renameTemplate(int templateId, String newName) {
        executor.execute(() -> db.workoutTemplateDao().updateName(templateId, newName));
    }

    public void deleteTemplate(int templateId) {
        executor.execute(() -> {
            db.templateExerciseDao().deleteAllForTemplate(templateId);
            db.workoutTemplateDao().deleteById(templateId);
        });
    }
}
