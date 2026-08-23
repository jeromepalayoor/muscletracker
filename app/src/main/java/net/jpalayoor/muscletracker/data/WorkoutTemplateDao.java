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
            "AND workout_session.endTime IS NOT NULL " +
            "GROUP BY workout_template.id " +
            "ORDER BY MAX(workout_session.startTime) ASC " +
            "LIMIT 1")
    WorkoutTemplate getSuggestedTemplate();

    @Query("SELECT workout_template.id AS id, " +
            "workout_template.name AS name, " +
            "COUNT(template_exercise.id) AS count " +
            "FROM workout_template " +
            "LEFT JOIN template_exercise ON template_exercise.templateId = workout_template.id " +
            "GROUP BY workout_template.id " +
            "ORDER BY workout_template.name ASC")
    LiveData<List<WorkoutTemplateWithCount>> getAllTemplatesWithCount();

    @Query("DELETE FROM workout_template")
    void deleteAll();

    @Query("SELECT * FROM workout_template")
    List<WorkoutTemplate> getAll();

    @Query("UPDATE workout_template SET name = :name WHERE id = :id")
    void updateName(int id, String name);
}