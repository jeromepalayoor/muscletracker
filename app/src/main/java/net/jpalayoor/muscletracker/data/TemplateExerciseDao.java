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
}