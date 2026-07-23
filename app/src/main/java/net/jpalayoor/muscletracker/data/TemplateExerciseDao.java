package net.jpalayoor.muscletracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TemplateExerciseDao {
    @Insert
    void insert(TemplateExercise template);

    @Delete
    void delete(TemplateExercise template);

    @Query("SELECT * FROM template_exercise WHERE templateId = :templateId ORDER BY exerciseOrder ASC")
    LiveData<List<TemplateExercise>> getAllLive(int templateId);

    @Query("SELECT template_exercise.id AS id, " +
            "template_exercise.exerciseId AS exerciseId, " +
            "exercises.name AS name, " +
            "template_exercise.exerciseOrder AS exerciseOrder " +
            "FROM template_exercise " +
            "JOIN exercises ON template_exercise.exerciseId = exercises.exerciseId " +
            "WHERE template_exercise.templateId = :templateId " +
            "ORDER BY template_exercise.exerciseOrder ASC")
    LiveData<List<TemplateExerciseWithName>> getExercisesWithNamesLive(int templateId);

    @Query("SELECT COUNT(*) FROM template_exercise WHERE templateId = :templateId")
    int countForTemplate(int templateId);
}