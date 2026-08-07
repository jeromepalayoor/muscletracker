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
    private final MutableLiveData<List<String>> alreadyAddedIds = new MutableLiveData<>();

    public ExercisePickerViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<Exercise>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<String>> getAlreadyAddedIds() {
        return alreadyAddedIds;
    }

    public void search(String query, int templateId) {
        executor.execute(() -> {
            List<Exercise> results = db.exerciseDao().searchRanked(query);
            searchResults.postValue(results);
            List<String> added = db.templateExerciseDao().getExerciseIdsForTemplate(templateId);
            alreadyAddedIds.postValue(added);
        });
    }
}