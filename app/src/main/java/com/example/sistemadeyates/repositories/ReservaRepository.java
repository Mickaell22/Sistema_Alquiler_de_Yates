package com.example.sistemadeyates.repositories;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.database.FirestoreManager;
import com.example.sistemadeyates.models.Reserva;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ReservaRepository {
    private static final String TAG = "ReservaRepository";
    private final CollectionReference reservasCollection;

    public ReservaRepository(Context context) {
        this.reservasCollection = FirestoreManager.getInstance().getReservasCollection();
    }

    // Callback interfaces
    public interface ReservaCallback {
        void onSuccess(Reserva reserva);
        void onError(String error);
    }

    public interface ReservasCallback {
        void onSuccess(List<Reserva> reservas);
        void onError(String error);
    }

    /**
     * Insert new reserva
     */
    public void insertReserva(Reserva reserva, ReservaCallback callback) {
        reservasCollection
                .add(reserva)
                .addOnSuccessListener(documentReference -> {
                    reserva.setId(documentReference.getId());
                    Log.d(TAG, "Reserva created with ID: " + reserva.getId());
                    callback.onSuccess(reserva);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error inserting reserva", e);
                    callback.onError("Error al crear reserva: " + e.getMessage());
                });
    }

    /**
     * Update existing reserva (with audit trail)
     */
    public void updateReserva(Reserva reserva, String modificadoPor, ReservaCallback callback) {
        if (reserva.getId() == null || reserva.getId().isEmpty()) {
            callback.onError("ID de reserva invalido");
            return;
        }

        // Update audit fields
        reserva.setFechaModificacion(System.currentTimeMillis());
        reserva.setModificadoPor(modificadoPor);

        reservasCollection
                .document(reserva.getId())
                .set(reserva)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Reserva updated: " + reserva.getId());
                    callback.onSuccess(reserva);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating reserva", e);
                    callback.onError("Error al actualizar reserva: " + e.getMessage());
                });
    }

    /**
     * Cancel reserva
     */
    public void cancelReserva(String reservaId, String canceladoPor, ReservaCallback callback) {
        reservasCollection
                .document(reservaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onError("Reserva no encontrada");
                        return;
                    }

                    Reserva reserva = documentSnapshot.toObject(Reserva.class);
                    if (reserva == null) {
                        callback.onError("Error al leer reserva");
                        return;
                    }

                    reserva.setEstado(Reserva.ESTADO_CANCELADA);
                    reserva.setCanceladoPor(canceladoPor);
                    reserva.setFechaModificacion(System.currentTimeMillis());
                    reserva.setModificadoPor(canceladoPor);

                    updateReserva(reserva, canceladoPor, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting reserva for cancellation", e);
                    callback.onError("Error al cancelar reserva: " + e.getMessage());
                });
    }

    /**
     * Get reserva by ID
     */
    public void getReservaById(String reservaId, ReservaCallback callback) {
        reservasCollection
                .document(reservaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Reserva reserva = documentSnapshot.toObject(Reserva.class);
                        callback.onSuccess(reserva);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting reserva by ID", e);
                    callback.onError("Error al buscar reserva: " + e.getMessage());
                });
    }

    /**
     * Get all reservas
     * NOTE: orderBy requires a Firestore index. Removed temporarily to allow functionality.
     * To add ordering back, create a composite index in Firestore Console for:
     * Collection: reservas, Fields: fecha_creacion (Descending)
     */
    public void getAllReservas(ReservasCallback callback) {
        Log.d(TAG, "getAllReservas() called");
        reservasCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reserva> reservas = querySnapshot.toObjects(Reserva.class);
                    Log.d(TAG, "getAllReservas - Found " + reservas.size() + " reservas from Firestore");
                    for (Reserva r : reservas) {
                        Log.d(TAG, "  Reserva: ID=" + r.getId() +
                                ", clienteId=" + r.getClienteId() +
                                ", yateId=" + r.getYateId() +
                                ", estado=" + r.getEstado() +
                                ", precioTotal=" + r.getPrecioTotal());
                    }

                    // Sort by fecha_creacion in memory (since we can't use orderBy without index)
                    reservas.sort((r1, r2) -> Long.compare(r2.getFechaCreacion(), r1.getFechaCreacion()));

                    callback.onSuccess(reservas);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all reservas", e);
                    callback.onError("Error al obtener reservas: " + e.getMessage());
                });
    }

    /**
     * Get reservas by cliente ID
     */
    public void getReservasByCliente(String clienteId, ReservasCallback callback) {
        reservasCollection
                .whereEqualTo("cliente_id", clienteId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reserva> reservas = querySnapshot.toObjects(Reserva.class);
                    // Sort in memory
                    reservas.sort((r1, r2) -> Long.compare(r2.getFechaInicio(), r1.getFechaInicio()));
                    callback.onSuccess(reservas);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting reservas by cliente", e);
                    callback.onError("Error al obtener reservas del cliente: " + e.getMessage());
                });
    }

    /**
     * Get reservas by yate ID
     */
    public void getReservasByYate(String yateId, ReservasCallback callback) {
        reservasCollection
                .whereEqualTo("yate_id", yateId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reserva> reservas = querySnapshot.toObjects(Reserva.class);
                    // Sort in memory
                    reservas.sort((r1, r2) -> Long.compare(r2.getFechaInicio(), r1.getFechaInicio()));
                    callback.onSuccess(reservas);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting reservas by yate", e);
                    callback.onError("Error al obtener reservas del yate: " + e.getMessage());
                });
    }

    /**
     * Get reservas by estado
     */
    public void getReservasByEstado(String estado, ReservasCallback callback) {
        reservasCollection
                .whereEqualTo("estado", estado)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reserva> reservas = querySnapshot.toObjects(Reserva.class);
                    // Sort in memory
                    reservas.sort((r1, r2) -> Long.compare(r2.getFechaInicio(), r1.getFechaInicio()));
                    callback.onSuccess(reservas);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting reservas by estado", e);
                    callback.onError("Error al obtener reservas por estado: " + e.getMessage());
                });
    }

    /**
     * Delete reserva (hard delete)
     */
    public void deleteReserva(String reservaId, ReservaCallback callback) {
        reservasCollection
                .document(reservaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Reserva deleted: " + reservaId);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting reserva", e);
                    callback.onError("Error al eliminar reserva: " + e.getMessage());
                });
    }
}
