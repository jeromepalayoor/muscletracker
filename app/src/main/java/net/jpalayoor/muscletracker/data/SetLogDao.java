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
}