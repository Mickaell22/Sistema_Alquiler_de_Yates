package com.example.sistemadeyates.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.adapters.ClienteAdapter;
import com.example.sistemadeyates.models.Cliente;
import com.example.sistemadeyates.repositories.ClienteRepository;
import com.example.sistemadeyates.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClientesActivity extends AppCompatActivity implements ClienteAdapter.OnClienteClickListener {

    private RecyclerView recyclerView;
    private ClienteAdapter adapter;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialToolbar toolbar;
    private ClienteRepository clienteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        clienteRepository = new ClienteRepository(this);
        loadClientes();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewClientes);
        fabAdd = findViewById(R.id.fabAddCliente);
        etSearch = findViewById(R.id.etSearchClients);
        progressBar = findViewById(R.id.progressBarClients);
        emptyState = findViewById(R.id.emptyStateClientes);
        toolbar = findViewById(R.id.toolbarClients);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ClienteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddClienteDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadClientes() {
        showLoading(true);
        clienteRepository.getActiveClientes(new ClienteRepository.ClientesCallback() {
            @Override
            public void onSuccess(List<Cliente> clientes) {
                runOnUiThread(() -> {
                    showLoading(false);
                    adapter.setClientes(clientes);
                    updateEmptyState(clientes.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ClientesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        // Click en el ítem completo -> VER DETALLES
        showClienteDetailsDialog(cliente);
    }

    @Override
    public void onClienteMenuClick(Cliente cliente, View view) {
        // Click en los 3 puntos -> MOSTRAR MENÚ POPUP CON 4 OPCIONES
        showClientePopupMenu(cliente, view);
    }

    private void showClientePopupMenu(Cliente cliente, View view) {
        PopupMenu popup = new PopupMenu(this, view);

        // Cargar menú desde XML
        popup.getMenuInflater().inflate(R.menu.menu_cliente_item, popup.getMenu());

        // Cambiar texto del botón según estado
        MenuItem toggleItem = popup.getMenu().findItem(R.id.action_toggle_status);
        if (toggleItem != null) {
            String statusText = cliente.isEstado() ? "Dar de baja" : "Reactivar";
            toggleItem.setTitle(statusText);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_edit) {
                // Editar
                showEditClienteDialog(cliente);
                return true;
            } else if (itemId == R.id.action_view_details) {
                // Ver detalles
                showClienteDetailsDialog(cliente);
                return true;
            } else if (itemId == R.id.action_toggle_status) {
                // Dar de baja/Reactivar
                toggleClienteStatus(cliente);
                return true;
            } else if (itemId == R.id.action_delete) {
                // Eliminar
                showDeleteConfirmation(cliente);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showClienteDetailsDialog(Cliente cliente) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cliente_detalles, null);

        TextView tvNombres = dialogView.findViewById(R.id.tvNombres);
        TextView tvApellidos = dialogView.findViewById(R.id.tvApellidos);
        TextView tvCedula = dialogView.findViewById(R.id.tvCedula);
        TextView tvTelefono = dialogView.findViewById(R.id.tvTelefono);
        TextView tvCorreo = dialogView.findViewById(R.id.tvCorreo);
        TextView tvLicencia = dialogView.findViewById(R.id.tvLicencia);
        TextView tvEstado = dialogView.findViewById(R.id.tvEstado);
        TextView tvFechaCreacion = dialogView.findViewById(R.id.tvFechaCreacion);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaCreacion = sdf.format(new Date(cliente.getFechaCreacion()));

        tvNombres.setText(cliente.getNombres());
        tvApellidos.setText(cliente.getApellidos());
        tvCedula.setText(cliente.getCedula());
        tvTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado");
        tvCorreo.setText(cliente.getCorreo());
        tvLicencia.setText(cliente.getNumeroLicencia() != null ? cliente.getNumeroLicencia() : "No registrada");
        tvEstado.setText(cliente.isEstado() ? "Activo" : "Inactivo");
        tvFechaCreacion.setText(fechaCreacion);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Detalles del Cliente")
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAddClienteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_client, null);

        TextInputLayout tilNombres = dialogView.findViewById(R.id.tilNombres);
        TextInputLayout tilApellidos = dialogView.findViewById(R.id.tilApellidos);
        TextInputLayout tilCedula = dialogView.findViewById(R.id.tilCedula);
        TextInputLayout tilTelefono = dialogView.findViewById(R.id.tilTelefono);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        TextInputLayout tilLicencia = dialogView.findViewById(R.id.tilLicencia);

        TextInputEditText etNombres = dialogView.findViewById(R.id.etNombres);
        TextInputEditText etApellidos = dialogView.findViewById(R.id.etApellidos);
        TextInputEditText etCedula = dialogView.findViewById(R.id.etCedula);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etLicencia = dialogView.findViewById(R.id.etLicencia);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnGuardarCliente);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelarCliente);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String cedula = etCedula.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String correo = etEmail.getText().toString().trim();
            String licencia = etLicencia.getText().toString().trim();

            // Limpiar todos los errores primero
            tilNombres.setError(null);
            tilApellidos.setError(null);
            tilCedula.setError(null);
            tilTelefono.setError(null);
            tilEmail.setError(null);
            tilLicencia.setError(null);

            // Validaciones individuales con mensajes específicos
            boolean isValid = true;

            // Validar nombres
            if (!ValidationUtils.validateNotEmpty(nombres)) {
                tilNombres.setError("Los nombres son obligatorios");
                isValid = false;
            } else if (!ValidationUtils.validateNombre(nombres)) {
                tilNombres.setError(ValidationUtils.getNombreError());
                isValid = false;
            }

            // Validar apellidos
            if (!ValidationUtils.validateNotEmpty(apellidos)) {
                tilApellidos.setError("Los apellidos son obligatorios");
                isValid = false;
            } else if (!ValidationUtils.validateNombre(apellidos)) {
                tilApellidos.setError(ValidationUtils.getNombreError());
                isValid = false;
            }

            // Validar cédula
            if (!ValidationUtils.validateCedula(cedula)) {
                tilCedula.setError(ValidationUtils.getCedulaError());
                isValid = false;
            }

            // Validar teléfono (opcional)
            if (!telefono.isEmpty() && !ValidationUtils.validateTelefono(telefono)) {
                tilTelefono.setError(ValidationUtils.getTelefonoError());
                isValid = false;
            }

            // Validar email
            if (!ValidationUtils.validateEmail(correo)) {
                tilEmail.setError(ValidationUtils.getEmailError(correo));
                isValid = false;
            }

            // Validar licencia (opcional)
            if (!licencia.isEmpty() && !ValidationUtils.validateLicencia(licencia)) {
                tilLicencia.setError(ValidationUtils.getLicenciaError());
                isValid = false;
            }

            if (!isValid) {
                return; // Detener si hay errores
            }

            // Crear cliente
            Cliente newCliente = new Cliente(
                    correo,
                    cedula,
                    telefono.isEmpty() ? null : telefono,
                    nombres,
                    apellidos,
                    licencia.isEmpty() ? null : licencia
            );

            clienteRepository.insertCliente(newCliente, new ClienteRepository.ClienteCallback() {
                @Override
                public void onSuccess(Cliente cliente) {
                    runOnUiThread(() -> {
                        Toast.makeText(ClientesActivity.this,
                                "Cliente creado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadClientes();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(ClientesActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showEditClienteDialog(Cliente cliente) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_client, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvTituloCliente);
        tvTitle.setText("Editar Cliente");

        TextInputLayout tilNombres = dialogView.findViewById(R.id.tilNombres);
        TextInputLayout tilApellidos = dialogView.findViewById(R.id.tilApellidos);
        TextInputLayout tilCedula = dialogView.findViewById(R.id.tilCedula);
        TextInputLayout tilTelefono = dialogView.findViewById(R.id.tilTelefono);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        TextInputLayout tilLicencia = dialogView.findViewById(R.id.tilLicencia);

        TextInputEditText etNombres = dialogView.findViewById(R.id.etNombres);
        TextInputEditText etApellidos = dialogView.findViewById(R.id.etApellidos);
        TextInputEditText etCedula = dialogView.findViewById(R.id.etCedula);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etLicencia = dialogView.findViewById(R.id.etLicencia);

        // Rellenar datos
        etNombres.setText(cliente.getNombres());
        etApellidos.setText(cliente.getApellidos());
        etCedula.setText(cliente.getCedula());
        etTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        etEmail.setText(cliente.getCorreo());
        etLicencia.setText(cliente.getNumeroLicencia() != null ? cliente.getNumeroLicencia() : "");

        // Cédula no editable en edición
        etCedula.setEnabled(false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnGuardarCliente);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelarCliente);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String correo = etEmail.getText().toString().trim();
            String licencia = etLicencia.getText().toString().trim();

            // Limpiar todos los errores primero
            tilNombres.setError(null);
            tilApellidos.setError(null);
            tilTelefono.setError(null);
            tilEmail.setError(null);
            tilLicencia.setError(null);

            // Validaciones individuales con mensajes específicos
            boolean isValid = true;

            // Validar nombres
            if (!ValidationUtils.validateNotEmpty(nombres)) {
                tilNombres.setError("Los nombres son obligatorios");
                isValid = false;
            } else if (!ValidationUtils.validateNombre(nombres)) {
                tilNombres.setError(ValidationUtils.getNombreError());
                isValid = false;
            }

            // Validar apellidos
            if (!ValidationUtils.validateNotEmpty(apellidos)) {
                tilApellidos.setError("Los apellidos son obligatorios");
                isValid = false;
            } else if (!ValidationUtils.validateNombre(apellidos)) {
                tilApellidos.setError(ValidationUtils.getNombreError());
                isValid = false;
            }

            // Validar teléfono (opcional)
            if (!telefono.isEmpty() && !ValidationUtils.validateTelefono(telefono)) {
                tilTelefono.setError(ValidationUtils.getTelefonoError());
                isValid = false;
            }

            // Validar email
            if (!ValidationUtils.validateEmail(correo)) {
                tilEmail.setError(ValidationUtils.getEmailError(correo));
                isValid = false;
            }

            // Validar licencia (opcional)
            if (!licencia.isEmpty() && !ValidationUtils.validateLicencia(licencia)) {
                tilLicencia.setError(ValidationUtils.getLicenciaError());
                isValid = false;
            }

            if (!isValid) {
                return; // Detener si hay errores
            }

            // Actualizar cliente
            cliente.setNombres(nombres);
            cliente.setApellidos(apellidos);
            cliente.setTelefono(telefono.isEmpty() ? null : telefono);
            cliente.setCorreo(correo);
            cliente.setNumeroLicencia(licencia.isEmpty() ? null : licencia);

            clienteRepository.updateCliente(cliente, new ClienteRepository.ClienteCallback() {
                @Override
                public void onSuccess(Cliente updatedCliente) {
                    runOnUiThread(() -> {
                        Toast.makeText(ClientesActivity.this,
                                "Cliente actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadClientes();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(ClientesActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void toggleClienteStatus(Cliente cliente) {
        boolean nuevoEstado = !cliente.isEstado();
        String accion = nuevoEstado ? "Reactivar" : "Dar de baja";

        new MaterialAlertDialogBuilder(this)
                .setTitle(accion + " Cliente")
                .setMessage("¿Está seguro que desea " + accion.toLowerCase() + " a " +
                        cliente.getNombreCompleto() + "?")
                .setPositiveButton(accion, (dialog, which) -> {
                    cliente.setEstado(nuevoEstado);
                    clienteRepository.updateCliente(cliente, new ClienteRepository.ClienteCallback() {
                        @Override
                        public void onSuccess(Cliente updatedCliente) {
                            runOnUiThread(() -> {
                                String resultado = nuevoEstado ? "reactivado" : "dado de baja";
                                Toast.makeText(ClientesActivity.this,
                                        "Cliente " + resultado + " correctamente",
                                        Toast.LENGTH_SHORT).show();
                                loadClientes();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(ClientesActivity.this,
                                        "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteConfirmation(Cliente cliente) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar Cliente")
                .setMessage("¿Está seguro que desea eliminar permanentemente a " +
                        cliente.getNombreCompleto() + "?\n\n" +
                        "Esta acción marcará al cliente como inactivo.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    clienteRepository.deleteCliente(cliente, new ClienteRepository.ClienteCallback() {
                        @Override
                        public void onSuccess(Cliente deletedCliente) {
                            runOnUiThread(() -> {
                                Toast.makeText(ClientesActivity.this,
                                        "Cliente eliminado (marcado como inactivo)",
                                        Toast.LENGTH_SHORT).show();
                                loadClientes();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(ClientesActivity.this,
                                        "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}