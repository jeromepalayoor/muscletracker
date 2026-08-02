package net.jpalayoor.muscletracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SetLogDao {
    @Insert
    void insert(SetLog log);

    @Query("SELECT * FROM set_log WHERE sessionId = :sessionId ORDER BY timestamp ASC, setNumber ASC")
    LiveData<List<SetLog>> getAllForSessionLive(int sessionId);

    @Query("SELECT * FROM set_log WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT 1")
    SetLog getMostRecentForExercise(String exerciseId);

    @Query("DELETE FROM set_log WHERE sessionId = :sessionId")
    void deleteAllForSession(int sessionId);

    @Query("SELECT COUNT(*) FROM set_log WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    int countForExerciseInSession(int sessionId, String exerciseId);

    @Query("SELECT * FROM set_log WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber ASC")
    LiveData<List<SetLog>> getForSessionAndExerciseLive(int sessionId, String exerciseId);

    @Query("SELECT set_log.id AS id, " +
            "set_log.exerciseId AS exerciseId, " +
            "exercises.name AS name, " +
            "set_log.weight AS weight, " +
            "set_log.reps AS reps, " +
            "set_log.setNumber AS setNumber, " +
            "set_log.timestamp AS timestamp " +
            "FROM set_log " +
            "JOIN exercises ON set_log.exerciseId = exercises.exerciseId " +
            "WHERE set_log.sessionId = :sessionId " +
            "ORDER BY set_log.exerciseId ASC, set_log.setNumber ASC")
    List<SetLogWithName> getSetLogWithNameByExercise(int sessionId);

    @Query("SELECT set_log.id AS id, " +
            "set_log.exerciseId AS exerciseId, " +
            "exercises.name AS name, " +
            "set_log.weight AS weight, " +
            "set_log.reps AS reps, " +
            "set_log.setNumber AS setNumber, " +
            "set_log.timestamp AS timestamp " +
            "FROM set_log " +
            "JOIN exercises ON set_log.exerciseId = exercises.exerciseId " +
            "WHERE set_log.sessionId = :sessionId " +
            "ORDER BY set_log.timestamp ASC")
    List<SetLogWithName> getSetLogWithNameByTime(int sessionId);

    @Query("SELECT MAX(weight) FROM set_log WHERE exerciseId = :exerciseId")
    Float getMaxWeight(String exerciseId);

    @Query("SELECT MAX(weight * reps) FROM set_log WHERE exerciseId = :exerciseId")
    Float getMaxVolume(String exerciseId);

    @Query("SELECT MAX(" +
            "CASE WHEN reps < 10 THEN weight * (36.0 / (37 - reps)) " +
            "ELSE weight * (1 + reps / 30.0) END" +
            ") FROM set_log WHERE exerciseId = :exerciseId")
    Float getBestEstimatedOneRepMax(String exerciseId);

    @Query("SELECT * FROM set_log WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    List<SetLog> getAllForExercise(String exerciseId);
}