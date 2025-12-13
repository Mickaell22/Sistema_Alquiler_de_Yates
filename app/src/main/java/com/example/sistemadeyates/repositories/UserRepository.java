package com.example.sistemadeyates.repositories;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.database.FirestoreManager;
import com.example.sistemadeyates.models.User;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final CollectionReference usersCollection;

    public UserRepository(Context context) {
        this.usersCollection = FirestoreManager.getInstance().getUsersCollection();
    }

    // Callback interfaces
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    /**
     * Get user by username (exact match)
     */
    public void getUserByUsername(String username, UserCallback callback) {
        usersCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(null);
                    } else {
                        User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                        callback.onSuccess(user);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user by username", e);
                    callback.onError("Error al buscar usuario: " + e.getMessage());
                });
    }

    /**
     * Get user by email (exact match)
     */
    public void getUserByEmail(String email, UserCallback callback) {
        usersCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(null);
                    } else {
                        User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                        callback.onSuccess(user);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user by email", e);
                    callback.onError("Error al buscar usuario por email: " + e.getMessage());
                });
    }

    /**
     * Get user by ID
     */
    public void getUserById(String userId, UserCallback callback) {
        usersCollection
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user by ID", e);
                    callback.onError("Error al buscar usuario: " + e.getMessage());
                });
    }

    /**
     * Insert new user
     */
    public void insertUser(User user, UserCallback callback) {
        // First check if username already exists
        getUserByUsername(user.getUsername(), new UserCallback() {
            @Override
            public void onSuccess(User existingUser) {
                if (existingUser != null) {
                    callback.onError("El nombre de usuario ya existe");
                    return;
                }

                // Check if email already exists
                getUserByEmail(user.getEmail(), new UserCallback() {
                    @Override
                    public void onSuccess(User existingEmailUser) {
                        if (existingEmailUser != null) {
                            callback.onError("El email ya esta registrado");
                            return;
                        }

                        // Create new user document
                        usersCollection
                                .add(user)
                                .addOnSuccessListener(documentReference -> {
                                    user.setId(documentReference.getId());
                                    Log.d(TAG, "User created with ID: " + user.getId());
                                    callback.onSuccess(user);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error inserting user", e);
                                    callback.onError("Error al crear usuario: " + e.getMessage());
                                });
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Update existing user
     */
    public void updateUser(User user, UserCallback callback) {
        if (user.getId() == null || user.getId().isEmpty()) {
            callback.onError("ID de usuario invalido");
            return;
        }

        usersCollection
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated: " + user.getId());
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user", e);
                    callback.onError("Error al actualizar usuario: " + e.getMessage());
                });
    }

    /**
     * Delete user (soft delete - set activo = false)
     */
    public void deleteUser(User user, UserCallback callback) {
        if (user.getId() == null || user.getId().isEmpty()) {
            callback.onError("ID de usuario invalido");
            return;
        }

        user.setActivo(false);
        updateUser(user, callback);
    }

    /**
     * Get all users
     */
    public void getAllUsers(UsersCallback callback) {
        usersCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = querySnapshot.toObjects(User.class);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all users", e);
                    callback.onError("Error al obtener usuarios: " + e.getMessage());
                });
    }

    /**
     * Get only active users
     */
    public void getActiveUsers(UsersCallback callback) {
        usersCollection
                .whereEqualTo("activo", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = querySnapshot.toObjects(User.class);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting active users", e);
                    callback.onError("Error al obtener usuarios activos: " + e.getMessage());
                });
    }

    /**
     * Get users by role
     */
    public void getUsersByRol(String rol, UsersCallback callback) {
        usersCollection
                .whereEqualTo("rol", rol)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = querySnapshot.toObjects(User.class);
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting users by rol", e);
                    callback.onError("Error al obtener usuarios por rol: " + e.getMessage());
                });
    }
}
