package com.example.sistemadeyates.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sistemadeyates.models.ActivityLog;

import java.util.List;

@Dao
public interface ActivityLogDao {
    @Insert
    long insert(ActivityLog log);

    @Delete
    void delete(ActivityLog log);

    @Query("SELECT * FROM activity_logs WHERE id = :logId")
    ActivityLog getById(int logId);

    @Query("SELECT * FROM activity_logs WHERE user_id = :userId ORDER BY timestamp DESC")
    List<ActivityLog> getLogsByUserId(int userId);

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT :limit")
    List<ActivityLog> getRecentLogs(int limit);

    @Query("SELECT * FROM activity_logs WHERE accion = :action ORDER BY timestamp DESC")
    List<ActivityLog> getLogsByAction(String action);

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    List<ActivityLog> getAllLogs();

    @Query("DELETE FROM activity_logs WHERE timestamp < :timestamp")
    void deleteOldLogs(long timestamp);

    @Query("DELETE FROM activity_logs")
    void deleteAll();
}
