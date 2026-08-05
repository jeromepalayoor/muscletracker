package net.jpalayoor.muscletracker.ui.record;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.WorkoutSession;
import net.jpalayoor.muscletracker.data.WorkoutTemplateWithCount;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartWorkoutViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Long> newSessionId = new MutableLiveData<>();
    private final LiveData<List<WorkoutTemplateWithCount>> allTemplates;

    public StartWorkoutViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        allTemplates = db.workoutTemplateDao().getAllTemplatesWithCount();
    }

    public LiveData<List<WorkoutTemplateWithCount>> getAllTemplates() {
        return allTemplates;
    }

    public LiveData<Long> getNewSessionId() {
        return newSessionId;
    }

    public void createSession(WorkoutTemplateWithCount template) {
        executor.execute(() -> {
            WorkoutSession session = new WorkoutSession();
            session.templateId = template.id;
            session.templateName = template.name;
            session.startTime = System.currentTimeMillis();
            long id = db.workoutSessionDao().insert(session);
            newSessionId.postValue(id);
        });
    }
}
