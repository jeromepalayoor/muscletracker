package net.jpalayoor.muscletracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// creating the database
@Database(entities = {Exercise.class, WorkoutTemplate.class, TemplateExercise.class,
        WorkoutSession.class, SetLog.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract ExerciseDao exerciseDao();
    public abstract WorkoutTemplateDao workoutTemplateDao();
    public abstract TemplateExerciseDao templateExerciseDao();
    public abstract WorkoutSessionDao workoutSessionDao();
    public abstract SetLogDao setLogDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "muscle_tracker_db")
                    .fallbackToDestructiveMigration(true)
                    .build();
        }
        return instance;
    }
}