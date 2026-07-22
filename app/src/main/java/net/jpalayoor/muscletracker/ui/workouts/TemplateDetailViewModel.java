package net.jpalayoor.muscletracker.ui.workouts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.TemplateExerciseWithName;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemplateDetailViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TemplateDetailViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<TemplateExerciseWithName>> getExercisesForTemplate(int templateId) {
        return db.templateExerciseDao().getExercisesWithNamesLive(templateId);
    }
}