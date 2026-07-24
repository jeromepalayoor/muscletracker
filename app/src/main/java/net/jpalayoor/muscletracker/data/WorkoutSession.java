package net.jpalayoor.muscletracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_session")
public class WorkoutSession {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int templateId;
    public String templateName;
    public long startTime;
    public Long endTime;
}