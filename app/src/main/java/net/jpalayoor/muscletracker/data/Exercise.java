package net.jpalayoor.muscletracker.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// creating exercises table
@Entity(tableName = "exercises")
public class Exercise {
    @PrimaryKey @NonNull
    public String exerciseId;
    public String name;
    public String muscleGroupsPrimary;
    public String muscleGroupsSecondary;
    public String specificMusclesPrimary;
    public String specificMusclesSecondary;
    public String anatomicalRegionsPrimary;
    public String anatomicalRegionsSecondary;
    public String splitCategory;
    public String upperLower;
    public String force;
    public String mechanic;
    public String equipment;
    public String level;
    public String exerciseType;
    public String instructions;
    public String image1;
    public String image2;
}