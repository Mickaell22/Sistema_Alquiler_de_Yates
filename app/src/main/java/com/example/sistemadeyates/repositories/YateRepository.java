package com.example.sistemadeyates.repositories;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.database.FirestoreManager;
import com.example.sistemadeyates.models.Yate;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class YateRepository {
    private static final String TAG = "YateRepository";
    private final CollectionReference yatesCollection;

    public YateRepository(Context context) {
        this.yatesCollection = FirestoreManager.getInstance().getYatesCollection();
    }

    // Callback interfaces
    public interface YateCallback {
        void onSuccess(Yate yate);
        void onError(String error);
    }

    public interface YatesCallback {
        void onSuccess(List<Yate> yates);
        void onError(String error);
    }

    /**
     * Get yate by ID
     */
    public void getYateById(String yateId, YateCallback callback) {
        yatesCollection
                .document(yateId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Yate yate = documentSnapshot.toObject(Yate.class);
                        callback.onSuccess(yate);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting yate by ID", e);
                    callback.onError("Error al buscar yate: " + e.getMessage());
                });
    }

    /**
     * Insert new yate
     */
    public void insertYate(Yate yate, YateCallback callback) {
        yatesCollection
                .add(yate)
                .addOnSuccessListener(documentReference -> {
                    yate.setId(documentReference.getId());
                    Log.d(TAG, "Yate created with ID: " + yate.getId());
                    callback.onSuccess(yate);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error inserting yate", e);
                    callback.onError("Error al crear yate: " + e.getMessage());
                });
    }

    /**
     * Update existing yate
     */
    public void updateYate(Yate yate, YateCallback callback) {
        if (yate.getId() == null || yate.getId().isEmpty()) {
            callback.onError("ID de yate invalido");
            return;
        }

        yatesCollection
                .document(yate.getId())
                .set(yate)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Yate updated: " + yate.getId());
                    callback.onSuccess(yate);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating yate", e);
                    callback.onError("Error al actualizar yate: " + e.getMessage());
                });
    }

    /**
     * Delete yate (soft delete - set disponible = false)
     */
    public void deleteYate(Yate yate, YateCallback callback) {
        if (yate.getId() == null || yate.getId().isEmpty()) {
            callback.onError("ID de yate invalido");
            return;
        }

        yate.setDisponible(false);
        updateYate(yate, callback);
    }

    /**
     * Get all yates
     */
    public void getAllYates(YatesCallback callback) {
        yatesCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Yate> yates = querySnapshot.toObjects(Yate.class);
                    callback.onSuccess(yates);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all yates", e);
                    callback.onError("Error al obtener yates: " + e.getMessage());
                });
    }

    /**
     * Get only available yates
     */
    public void getAvailableYates(YatesCallback callback) {
        yatesCollection
                .whereEqualTo("disponible", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Yate> yates = querySnapshot.toObjects(Yate.class);
                    callback.onSuccess(yates);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting available yates", e);
                    callback.onError("Error al obtener yates disponibles: " + e.getMessage());
                });
    }
}
