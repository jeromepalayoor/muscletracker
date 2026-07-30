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

    @Query("DELETE FROM workout_template WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT workout_template.* FROM workout_template " +
            "LEFT JOIN workout_session ON workout_template.id = workout_session.templateId " +
            "GROUP BY workout_template.id " +
            "ORDER BY MAX(workout_session.startTime) ASC " +
            "LIMIT 1")
    WorkoutTemplate getSuggestedTemplate();
}