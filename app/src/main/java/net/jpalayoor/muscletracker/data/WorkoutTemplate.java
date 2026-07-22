package net.jpalayoor.muscletracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_template")
public class WorkoutTemplate {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
}