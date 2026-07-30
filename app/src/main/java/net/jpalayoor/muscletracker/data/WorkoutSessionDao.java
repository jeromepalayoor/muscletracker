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

    @Query("SELECT * FROM workout_session WHERE endTime IS NOT NULL ORDER BY startTime DESC")
    LiveData<List<WorkoutSession>> getAllSessionsLive();

    @Query("SELECT * FROM workout_session WHERE id = :id LIMIT 1")
    WorkoutSession getById(int id);

    @Update
    void update(WorkoutSession session);

    @Query("DELETE FROM workout_session WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM workout_session WHERE endTime IS NULL LIMIT 1")
    WorkoutSession getInProgressSession();

    @Query("UPDATE workout_session SET endTime = :endTime WHERE id = :id")
    void endSession(int id, long endTime);

    @Query("SELECT * FROM workout_session WHERE endTime IS NOT NULL ORDER BY startTime DESC LIMIT 1")
    WorkoutSession getMostRecentSession();

    @Query("SELECT COUNT(*) FROM workout_session WHERE endTime IS NOT NULL AND startTime >= :cutoffTime")
    int countSessionsSince(long cutoffTime);

    @Query("SELECT * FROM workout_session WHERE endTime IS NOT NULL AND startTime >= :monthStart AND startTime < :monthEnd ORDER BY startTime ASC")
    List<WorkoutSession> getSessionsInRange(long monthStart, long monthEnd);
}