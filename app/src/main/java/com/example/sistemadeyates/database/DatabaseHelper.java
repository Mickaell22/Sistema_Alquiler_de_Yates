package com.example.sistemadeyates.database;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.utils.EncryptionUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance;
    private final ExecutorService executorService;
    private final AppDatabase database;

    private DatabaseHelper(Context context) {
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void initializeDefaultUsers(Runnable onComplete) {
        executorService.execute(() -> {
            try {
                int userCount = database.userDao().getAllUsers().size();
                if (userCount == 0) {
                    Log.d(TAG, "Initializing default users...");

                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setEmail("admin@yates.com");
                    admin.setPassword(EncryptionUtils.hashPassword("admin123"));
                    admin.setRol("ADMIN");
                    admin.setActivo(true);
                    database.userDao().insert(admin);

                    User empleado = new User();
                    empleado.setUsername("empleado1");
                    empleado.setEmail("empleado1@yates.com");
                    empleado.setPassword(EncryptionUtils.hashPassword("emp123"));
                    empleado.setRol("EMPLEADO");
                    empleado.setActivo(true);
                    database.userDao().insert(empleado);

                    Log.d(TAG, "Default users created successfully");
                }

                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing default users", e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
        AppDatabase.destroyInstance();
    }
}
