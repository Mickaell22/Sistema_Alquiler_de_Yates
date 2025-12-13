package com.example.sistemadeyates.repositories;

import android.content.Context;
import android.util.Log;

import com.example.sistemadeyates.database.FirestoreManager;
import com.example.sistemadeyates.models.Cliente;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class ClienteRepository {
    private static final String TAG = "ClienteRepository";
    private final CollectionReference clientesCollection;

    public ClienteRepository(Context context) {
        this.clientesCollection = FirestoreManager.getInstance().getClientesCollection();
    }

    // Callback interfaces
    public interface ClienteCallback {
        void onSuccess(Cliente cliente);
        void onError(String error);
    }

    public interface ClientesCallback {
        void onSuccess(List<Cliente> clientes);
        void onError(String error);
    }

    /**
     * Get cliente by cedula (exact match)
     */
    public void getClienteByCedula(String cedula, ClienteCallback callback) {
        clientesCollection
                .whereEqualTo("cedula", cedula)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(null);
                    } else {
                        Cliente cliente = querySnapshot.getDocuments().get(0).toObject(Cliente.class);
                        callback.onSuccess(cliente);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting cliente by cedula", e);
                    callback.onError("Error al buscar cliente: " + e.getMessage());
                });
    }

    /**
     * Get cliente by correo (exact match)
     */
    public void getClienteByCorreo(String correo, ClienteCallback callback) {
        clientesCollection
                .whereEqualTo("correo", correo)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(null);
                    } else {
                        Cliente cliente = querySnapshot.getDocuments().get(0).toObject(Cliente.class);
                        callback.onSuccess(cliente);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting cliente by correo", e);
                    callback.onError("Error al buscar cliente por correo: " + e.getMessage());
                });
    }

    /**
     * Get cliente by numero de licencia (exact match)
     */
    public void getClienteByLicencia(String numeroLicencia, ClienteCallback callback) {
        clientesCollection
                .whereEqualTo("numero_licencia", numeroLicencia)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(null);
                    } else {
                        Cliente cliente = querySnapshot.getDocuments().get(0).toObject(Cliente.class);
                        callback.onSuccess(cliente);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting cliente by licencia", e);
                    callback.onError("Error al buscar cliente por licencia: " + e.getMessage());
                });
    }

    /**
     * Get cliente by ID
     */
    public void getClienteById(String clienteId, ClienteCallback callback) {
        clientesCollection
                .document(clienteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        callback.onSuccess(cliente);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting cliente by ID", e);
                    callback.onError("Error al buscar cliente: " + e.getMessage());
                });
    }

    /**
     * Insert new cliente
     */
    public void insertCliente(Cliente cliente, ClienteCallback callback) {
        // First check if cedula already exists
        getClienteByCedula(cliente.getCedula(), new ClienteCallback() {
            @Override
            public void onSuccess(Cliente existingCliente) {
                if (existingCliente != null) {
                    callback.onError("La cedula ya esta registrada");
                    return;
                }

                // Check if correo already exists
                getClienteByCorreo(cliente.getCorreo(), new ClienteCallback() {
                    @Override
                    public void onSuccess(Cliente existingEmailCliente) {
                        if (existingEmailCliente != null) {
                            callback.onError("El correo ya esta registrado");
                            return;
                        }

                        // Check if numero de licencia already exists
                        getClienteByLicencia(cliente.getNumeroLicencia(), new ClienteCallback() {
                            @Override
                            public void onSuccess(Cliente existingLicenciaCliente) {
                                if (existingLicenciaCliente != null) {
                                    callback.onError("El numero de licencia ya esta registrado");
                                    return;
                                }

                                // Create new cliente document
                                clientesCollection
                                        .add(cliente)
                                        .addOnSuccessListener(documentReference -> {
                                            cliente.setId(documentReference.getId());
                                            Log.d(TAG, "Cliente created with ID: " + cliente.getId());
                                            callback.onSuccess(cliente);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error inserting cliente", e);
                                            callback.onError("Error al crear cliente: " + e.getMessage());
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

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Update existing cliente
     */
    public void updateCliente(Cliente cliente, ClienteCallback callback) {
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            callback.onError("ID de cliente invalido");
            return;
        }

        cliente.setFechaModificacion(System.currentTimeMillis());

        clientesCollection
                .document(cliente.getId())
                .set(cliente)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cliente updated: " + cliente.getId());
                    callback.onSuccess(cliente);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating cliente", e);
                    callback.onError("Error al actualizar cliente: " + e.getMessage());
                });
    }

    /**
     * Delete cliente (soft delete - set estado = false)
     */
    public void deleteCliente(Cliente cliente, ClienteCallback callback) {
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            callback.onError("ID de cliente invalido");
            return;
        }

        cliente.setEstado(false);
        updateCliente(cliente, callback);
    }

    /**
     * Get all clientes
     */
    public void getAllClientes(ClientesCallback callback) {
        clientesCollection
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Cliente> clientes = querySnapshot.toObjects(Cliente.class);
                    callback.onSuccess(clientes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all clientes", e);
                    callback.onError("Error al obtener clientes: " + e.getMessage());
                });
    }

    /**
     * Get only active clientes
     */
    public void getActiveClientes(ClientesCallback callback) {
        clientesCollection
                .whereEqualTo("estado", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Cliente> clientes = querySnapshot.toObjects(Cliente.class);
                    callback.onSuccess(clientes);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting active clientes", e);
                    callback.onError("Error al obtener clientes activos: " + e.getMessage());
                });
    }

    /**
     * Search clientes by nombre, apellido, cedula or correo
     */
    public void searchClientes(String query, ClientesCallback callback) {
        getAllClientes(new ClientesCallback() {
            @Override
            public void onSuccess(List<Cliente> clientes) {
                List<Cliente> filteredClientes = new java.util.ArrayList<>();
                String lowerQuery = query.toLowerCase();

                for (Cliente cliente : clientes) {
                    if (cliente.getNombres().toLowerCase().contains(lowerQuery) ||
                        cliente.getApellidos().toLowerCase().contains(lowerQuery) ||
                        cliente.getCedula().toLowerCase().contains(lowerQuery) ||
                        cliente.getCorreo().toLowerCase().contains(lowerQuery)) {
                        filteredClientes.add(cliente);
                    }
                }

                callback.onSuccess(filteredClientes);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}
