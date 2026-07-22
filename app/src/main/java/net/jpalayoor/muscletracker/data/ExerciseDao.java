package net.jpalayoor.muscletracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Insert
    void insert(Exercise exercise);

    @Insert
    void insertAll(List<Exercise> exercises);

    @Query("SELECT * FROM exercises ORDER BY exerciseId ASC")
    List<Exercise> getAll();

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    LiveData<List<Exercise>> getAllLive();

    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId LIMIT 1")
    Exercise getById(String exerciseId);

    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    List<Exercise> searchByName(String query);
}