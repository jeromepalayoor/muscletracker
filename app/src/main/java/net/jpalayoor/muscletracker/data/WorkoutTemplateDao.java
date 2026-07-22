package net.jpalayoor.muscletracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutTemplateDao {
    @Insert
    long insert(WorkoutTemplate template);

    @Delete
    void delete(WorkoutTemplate template);

    @Query("SELECT * FROM workout_template ORDER BY name ASC")
    LiveData<List<WorkoutTemplate>> getAllLive();
}