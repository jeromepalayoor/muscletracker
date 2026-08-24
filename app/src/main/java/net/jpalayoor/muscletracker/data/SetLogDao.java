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

    @Query("DELETE FROM set_log WHERE id = :id")
    void deleteById(int id);

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
            "exercises.trackingType AS trackingType, " +
            "exercises.fitCategoryValue AS fitCategoryValue, " +
            "exercises.fitSubtypeValue AS fitSubtypeValue, " +
            "set_log.durationSeconds AS durationSeconds, " +
            "((CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END) " +
            "= (SELECT MAX(CASE exercises.trackingType WHEN 'reps' THEN sl3.reps WHEN 'time' THEN sl3.durationSeconds ELSE sl3.weight END) " +
            "FROM set_log sl3 WHERE sl3.exerciseId = set_log.exerciseId AND sl3.sessionId = set_log.sessionId) " +
            "AND (CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END) " +
            "> COALESCE((SELECT MAX(CASE exercises.trackingType WHEN 'reps' THEN sl2.reps WHEN 'time' THEN sl2.durationSeconds ELSE sl2.weight END) " +
            "FROM set_log sl2 WHERE sl2.exerciseId = set_log.exerciseId AND sl2.timestamp < set_log.timestamp), 0) " +
            "AND set_log.id = (SELECT MIN(sl4.id) FROM set_log sl4 " +
            "WHERE sl4.exerciseId = set_log.exerciseId AND sl4.sessionId = set_log.sessionId " +
            "AND (CASE exercises.trackingType WHEN 'reps' THEN sl4.reps WHEN 'time' THEN sl4.durationSeconds ELSE sl4.weight END) " +
            "= (CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END))" +
            ") AS isPR " +
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
            "exercises.trackingType AS trackingType, " +
            "exercises.fitCategoryValue AS fitCategoryValue, " +
            "exercises.fitSubtypeValue AS fitSubtypeValue, " +
            "set_log.durationSeconds AS durationSeconds, " +
            "((CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END) " +
            "= (SELECT MAX(CASE exercises.trackingType WHEN 'reps' THEN sl3.reps WHEN 'time' THEN sl3.durationSeconds ELSE sl3.weight END) " +
            "FROM set_log sl3 WHERE sl3.exerciseId = set_log.exerciseId AND sl3.sessionId = set_log.sessionId) " +
            "AND (CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END) " +
            "> COALESCE((SELECT MAX(CASE exercises.trackingType WHEN 'reps' THEN sl2.reps WHEN 'time' THEN sl2.durationSeconds ELSE sl2.weight END) " +
            "FROM set_log sl2 WHERE sl2.exerciseId = set_log.exerciseId AND sl2.timestamp < set_log.timestamp), 0) " +
            "AND set_log.id = (SELECT MIN(sl4.id) FROM set_log sl4 " +
            "WHERE sl4.exerciseId = set_log.exerciseId AND sl4.sessionId = set_log.sessionId " +
            "AND (CASE exercises.trackingType WHEN 'reps' THEN sl4.reps WHEN 'time' THEN sl4.durationSeconds ELSE sl4.weight END) " +
            "= (CASE exercises.trackingType WHEN 'reps' THEN set_log.reps WHEN 'time' THEN set_log.durationSeconds ELSE set_log.weight END))" +
            ") AS isPR " +
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

    @Query("SELECT MAX(one_rm) FROM (" +
            "SELECT weight * (" +
            "CASE WHEN reps < 10 THEN 36.0/(37-reps)" +
            "ELSE 1+reps/30.0 END) AS one_rm " +
            "FROM set_log " +
            "WHERE exerciseId = :exerciseId " +
            "ORDER BY timestamp DESC LIMIT 10)")
    Float getRecentEstimatedOneRepMax(String exerciseId);

    @Query("DELETE FROM set_log")
    void deleteAll();

    @Query("SELECT * FROM set_log")
    List<SetLog> getAll();

    @Query("SELECT MAX(reps) FROM (" +
            "SELECT reps FROM set_log " +
            "WHERE exerciseId = :exerciseId " +
            "ORDER BY timestamp DESC LIMIT 10)")
    Integer getRecentMaxReps(String exerciseId);

    @Query("SELECT MAX(durationSeconds) FROM (" +
            "SELECT durationSeconds FROM set_log " +
            "WHERE exerciseId = :exerciseId " +
            "ORDER BY timestamp DESC LIMIT 10)")
    Integer getRecentMaxDuration(String exerciseId);

    @Query("SELECT MAX(reps) FROM set_log WHERE exerciseId = :exerciseId")
    Integer getMaxReps(String exerciseId);

    @Query("SELECT MAX(durationSeconds) FROM set_log WHERE exerciseId = :exerciseId")
    Integer getMaxDuration(String exerciseId);
}