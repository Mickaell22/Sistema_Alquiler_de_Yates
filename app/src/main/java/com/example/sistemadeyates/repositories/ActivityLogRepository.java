package com.example.sistemadeyates.repositories;

import android.content.Context;

import com.example.sistemadeyates.database.AppDatabase;
import com.example.sistemadeyates.database.dao.ActivityLogDao;
import com.example.sistemadeyates.models.ActivityLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityLogRepository {
    private final ActivityLogDao activityLogDao;
    private final ExecutorService executorService;

    public ActivityLogRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.activityLogDao = database.activityLogDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface LogCallback {
        void onSuccess(ActivityLog log);
        void onError(String error);
    }

    public interface LogsCallback {
        void onSuccess(List<ActivityLog> logs);
        void onError(String error);
    }

    public void insertLog(ActivityLog log, LogCallback callback) {
        executorService.execute(() -> {
            try {
                long id = activityLogDao.insert(log);
                log.setId((int) id);
                if (callback != null) {
                    callback.onSuccess(log);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getLogsByUserId(int userId, LogsCallback callback) {
        executorService.execute(() -> {
            try {
                List<ActivityLog> logs = activityLogDao.getLogsByUserId(userId);
                callback.onSuccess(logs);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getRecentLogs(int limit, LogsCallback callback) {
        executorService.execute(() -> {
            try {
                List<ActivityLog> logs = activityLogDao.getRecentLogs(limit);
                callback.onSuccess(logs);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void getAllLogs(LogsCallback callback) {
        executorService.execute(() -> {
            try {
                List<ActivityLog> logs = activityLogDao.getAllLogs();
                callback.onSuccess(logs);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
}
