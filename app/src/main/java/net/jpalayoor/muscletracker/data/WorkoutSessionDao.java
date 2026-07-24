package net.jpalayoor.muscletracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkoutSessionDao {
    @Insert
    long insert(WorkoutSession session);

    @Delete
    void delete(WorkoutSession template);

    @Query("SELECT * FROM workout_session ORDER BY startTime DESC")
    LiveData<List<WorkoutSession>> getAllSessionsLive();

    @Query("SELECT * FROM workout_session WHERE id = :id LIMIT 1")
    WorkoutSession getById(int id);

    @Update
    void update(WorkoutSession session);
}