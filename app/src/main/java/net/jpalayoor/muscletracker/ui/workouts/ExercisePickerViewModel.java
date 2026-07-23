package net.jpalayoor.muscletracker.ui.workouts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.jpalayoor.muscletracker.data.AppDatabase;
import net.jpalayoor.muscletracker.data.Exercise;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExercisePickerViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<Exercise>> searchResults = new MutableLiveData<>();

    public ExercisePickerViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<Exercise>> getSearchResults() {
        return searchResults;
    }

    public void search(String query) {
        executor.execute(() -> {
            List<Exercise> results = db.exerciseDao().searchByName(query);
            searchResults.postValue(results);
        });
    }
}