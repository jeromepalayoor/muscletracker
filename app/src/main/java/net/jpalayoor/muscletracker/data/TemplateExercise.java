package net.jpalayoor.muscletracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "template_exercise")
public class TemplateExercise {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int templateId;
    public String exerciseId;
    public int exerciseOrder;
}