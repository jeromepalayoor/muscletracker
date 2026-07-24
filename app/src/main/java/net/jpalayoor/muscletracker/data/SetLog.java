package net.jpalayoor.muscletracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "set_log")
public class SetLog {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int sessionId;
    public String exerciseId;
    public float weight;
    public int reps;
    public int setNumber;
    public long timestamp;
}