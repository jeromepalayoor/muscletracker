package net.jpalayoor.muscletracker.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.WorkoutSession;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> daysSinceLastText = new MutableLiveData<>();
    private final MutableLiveData<Integer> sessionsThisWeek = new MutableLiveData<>();
    private final MutableLiveData<Integer> sessionsThisMonth = new MutableLiveData<>();
    private MutableLiveData<String> suggested = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<String> getDaysSinceLastText() {
        return daysSinceLastText;
    }

    public LiveData<Integer> getSessionsThisWeek() {
        return sessionsThisWeek;
    }

    public LiveData<Integer> getSessionsThisMonth() {
        return sessionsThisMonth;
    }

    public LiveData<String> getSuggested() {
        return suggested;
    }

    public void loadStats() {
        executor.execute(() -> {
            WorkoutSession last = db.workoutSessionDao().getMostRecentSession();
            if (last != null) {
                long daysSince = ((System.currentTimeMillis() - last.startTime) / 1000 / 60 / 60 / 24);
                daysSinceLastText.postValue(daysSince + " day" + (daysSince == 1 ? "" : "s") + " since last");
            } else {
                daysSinceLastText.postValue("No workouts yet");
            }

            long weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            sessionsThisWeek.postValue(db.workoutSessionDao().countSessionsSince(weekAgo));

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            sessionsThisMonth.postValue(db.workoutSessionDao().countSessionsSince(cal.getTimeInMillis()));

            WorkoutTemplate template = db.workoutTemplateDao().getSuggestedTemplate();
            suggested.postValue(template != null ? template.name : "Create a template to get started");
        });
    }
}