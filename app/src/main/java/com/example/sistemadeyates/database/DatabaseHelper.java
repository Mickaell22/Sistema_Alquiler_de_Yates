package com.example.sistemadeyates.database;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.repositories.UserRepository;
import com.example.sistemadeyates.utils.EncryptionUtils;

/**
 * Helper class for Firestore database initialization
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static DatabaseHelper instance;
    private final UserRepository userRepository;

    private DatabaseHelper(Context context) {
        this.userRepository = new UserRepository(context);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Initialize default users in Firestore
     * Only creates users if none exist
     */
    public void initializeDefaultUsers(Runnable onComplete) {
        // Check if users already exist
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(java.util.List<User> users) {
                if (users == null || users.isEmpty()) {
                    Log.d(TAG, "No users found. Initializing default users...");
                    createDefaultUsers(onComplete);
                } else {
                    Log.d(TAG, "Users already exist. Skipping initialization.");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking existing users: " + error);
                // Still try to create default users
                createDefaultUsers(onComplete);
            }
        });
    }

    private void createDefaultUsers(Runnable onComplete) {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@yates.com");
        admin.setPassword(EncryptionUtils.hashPassword("admin123"));
        admin.setRol("ADMIN");
        admin.setActivo(true);

        userRepository.insertUser(admin, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Admin user created successfully with ID: " + user.getId());

                // Create employee user
                User empleado = new User();
                empleado.setUsername("empleado1");
                empleado.setEmail("empleado1@yates.com");
                empleado.setPassword(EncryptionUtils.hashPassword("emp123"));
                empleado.setRol("EMPLEADO");
                empleado.setActivo(true);

                userRepository.insertUser(empleado, new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "Employee user created successfully with ID: " + user.getId());
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error creating employee user: " + error);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error creating admin user: " + error);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    /**
     * No shutdown needed for Firestore (unlike Room)
     */
    public void shutdown() {
        Log.d(TAG, "DatabaseHelper shutdown (no-op for Firestore)");
    }
}
