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

    @Query("SELECT * FROM exercises" +
            " WHERE name LIKE '%' || :query || '%'" +
            " OR muscleGroupsPrimary LIKE '%' || :query || '%'" +
            " OR muscleGroupsSecondary LIKE '%' || :query || '%'" +
            " OR specificMusclesPrimary LIKE '%' || :query || '%'" +
            " OR specificMusclesSecondary LIKE '%' || :query || '%'" +
            " OR anatomicalRegionsPrimary LIKE '%' || :query || '%'" +
            " OR anatomicalRegionsSecondary LIKE '%' || :query || '%'" +
            " OR splitCategory LIKE '%' || :query || '%'" +
            " OR upperLower LIKE '%' || :query || '%'" +
            " OR force LIKE '%' || :query || '%'" +
            " OR mechanic LIKE '%' || :query || '%'" +
            " OR equipment LIKE '%' || :query || '%'" +
            " OR level LIKE '%' || :query || '%'" +
            " OR exerciseType LIKE '%' || :query || '%'" +
            " ORDER BY CASE" +
            " WHEN name LIKE '%' || :query || '%' THEN 1" +
            " WHEN muscleGroupsPrimary LIKE '%' || :query || '%' THEN 2" +
            " WHEN muscleGroupsSecondary LIKE '%' || :query || '%' THEN 3" +
            " WHEN specificMusclesPrimary LIKE '%' || :query || '%' THEN 4" +
            " WHEN specificMusclesSecondary LIKE '%' || :query || '%' THEN 5" +
            " WHEN anatomicalRegionsPrimary LIKE '%' || :query || '%' THEN 6" +
            " WHEN anatomicalRegionsSecondary LIKE '%' || :query || '%' THEN 7" +
            " WHEN splitCategory LIKE '%' || :query || '%' THEN 8" +
            " WHEN upperLower LIKE '%' || :query || '%' THEN 9" +
            " WHEN force LIKE '%' || :query || '%' THEN 10" +
            " WHEN mechanic LIKE '%' || :query || '%' THEN 11" +
            " WHEN equipment LIKE '%' || :query || '%' THEN 12" +
            " WHEN level LIKE '%' || :query || '%' THEN 13" +
            " WHEN exerciseType LIKE '%' || :query || '%' THEN 14" +
            " ELSE 15 END ASC, name ASC")
    List<Exercise> searchRanked(String query);
}