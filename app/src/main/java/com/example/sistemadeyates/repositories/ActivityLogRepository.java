package com.example.sistemadeyates.repositories;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.database.FirestoreManager;
import com.example.sistemadeyates.models.ActivityLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ActivityLogRepository {
    private static final String TAG = "ActivityLogRepository";
    private final CollectionReference logsCollection;

    public ActivityLogRepository(Context context) {
        this.logsCollection = FirestoreManager.getInstance().getActivityLogsCollection();
    }

    // Callback interfaces
    public interface LogCallback {
        void onSuccess(ActivityLog log);
        void onError(String error);
    }

    public interface LogsCallback {
        void onSuccess(List<ActivityLog> logs);
        void onError(String error);
    }

    /**
     * Insert new activity log
     */
    public void insertLog(ActivityLog log, LogCallback callback) {
        logsCollection
                .add(log)
                .addOnSuccessListener(documentReference -> {
                    log.setId(documentReference.getId());
                    Log.d(TAG, "Activity log created with ID: " + log.getId());
                    if (callback != null) {
                        callback.onSuccess(log);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error inserting activity log", e);
                    if (callback != null) {
                        callback.onError("Error al crear log: " + e.getMessage());
                    }
                });
    }

    /**
     * Get logs by user ID
     */
    public void getLogsByUserId(String userId, LogsCallback callback) {
        logsCollection
                .whereEqualTo("user_id", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ActivityLog> logs = querySnapshot.toObjects(ActivityLog.class);
                    callback.onSuccess(logs);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting logs by user ID", e);
                    callback.onError("Error al obtener logs: " + e.getMessage());
                });
    }

    /**
     * Get recent logs (limited)
     */
    public void getRecentLogs(int limit, LogsCallback callback) {
        logsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ActivityLog> logs = querySnapshot.toObjects(ActivityLog.class);
                    callback.onSuccess(logs);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting recent logs", e);
                    callback.onError("Error al obtener logs recientes: " + e.getMessage());
                });
    }

    /**
     * Get all logs
     */
    public void getAllLogs(LogsCallback callback) {
        logsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ActivityLog> logs = querySnapshot.toObjects(ActivityLog.class);
                    callback.onSuccess(logs);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all logs", e);
                    callback.onError("Error al obtener todos los logs: " + e.getMessage());
                });
    }

    /**
     * Get logs by action type
     */
    public void getLogsByAction(String action, LogsCallback callback) {
        logsCollection
                .whereEqualTo("accion", action)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ActivityLog> logs = querySnapshot.toObjects(ActivityLog.class);
                    callback.onSuccess(logs);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting logs by action", e);
                    callback.onError("Error al obtener logs por accion: " + e.getMessage());
                });
    }

    /**
     * Delete old logs (before specified timestamp)
     */
    public void deleteOldLogs(long timestamp, LogCallback callback) {
        logsCollection
                .whereLessThan("timestamp", timestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        if (callback != null) {
                            callback.onSuccess(null);
                        }
                        return;
                    }

                    // Delete in batch
                    com.google.firebase.firestore.WriteBatch batch =
                            FirestoreManager.getInstance().getDb().batch();

                    querySnapshot.getDocuments().forEach(doc -> {
                        batch.delete(doc.getReference());
                    });

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Deleted " + querySnapshot.size() + " old logs");
                                if (callback != null) {
                                    callback.onSuccess(null);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting old logs", e);
                                if (callback != null) {
                                    callback.onError("Error al eliminar logs antiguos: " + e.getMessage());
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error querying old logs", e);
                    if (callback != null) {
                        callback.onError("Error al buscar logs antiguos: " + e.getMessage());
                    }
                });
    }
}
