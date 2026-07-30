package net.jpalayoor.muscletracker.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.CalendarDay;
import net.jpalayoor.muscletracker.data.WorkoutSession;
import net.jpalayoor.muscletracker.data.WorkoutTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<String> daysSinceLastText = new MutableLiveData<>();
    private final MutableLiveData<Integer> sessionsThisWeek = new MutableLiveData<>();
    private final MutableLiveData<Integer> sessionsThisMonth = new MutableLiveData<>();
    private final MutableLiveData<String> suggested = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, List<Integer>>> sessionsByDay = new MutableLiveData<>();

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

    public LiveData<Map<Integer, List<Integer>>> getSessionsByDay() {
        return sessionsByDay;
    }

    public void groupSessionsByDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        long start = cal.getTimeInMillis();
        cal.set(Calendar.MONTH, month + 1);
        long end = cal.getTimeInMillis();

        executor.execute(() -> {
            List<WorkoutSession> sessions = db.workoutSessionDao().getSessionsInRange(start, end);
            Map<Integer, List<Integer>> grouped = new HashMap<>();

            for (WorkoutSession session : sessions) {
                cal.setTimeInMillis(session.startTime);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                if (!grouped.containsKey(day)) {
                    grouped.put(day, new ArrayList<>());
                }
                grouped.get(day).add(session.id);
            }

            sessionsByDay.postValue(grouped);
        });
    }

    public List<CalendarDay> buildCalendarDays(int year, int month, Map<Integer, List<Integer>> sessionsByDay) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);


        List<CalendarDay> calendarDays = new ArrayList<>();
        for (int i = 0; i < startDayOfWeek-1; i++) {
            CalendarDay day = new CalendarDay();
            day.isBlank = true;
            calendarDays.add(day);
        }

        for (int i = 1; i <= daysInMonth; i++) {
            CalendarDay day = new CalendarDay();
            day.isBlank = false;
            day.day = i;
            if (sessionsByDay.containsKey(day.day)) {
                day.sessionIds = sessionsByDay.get(day.day);
            }
            else {
                day.sessionIds = new ArrayList<>();
            }
            calendarDays.add(day);
        }

        return calendarDays;
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