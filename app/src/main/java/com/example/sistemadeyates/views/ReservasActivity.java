package com.example.sistemadeyates.views;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.adapters.ReservaAdapter;
import com.example.sistemadeyates.models.Cliente;
import com.example.sistemadeyates.models.Reserva;
import com.example.sistemadeyates.models.Yate;
import com.example.sistemadeyates.repositories.ClienteRepository;
import com.example.sistemadeyates.repositories.ReservaRepository;
import com.example.sistemadeyates.repositories.YateRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReservasActivity extends AppCompatActivity implements ReservaAdapter.OnReservaClickListener {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_USER_ID = "user_id";

    private RecyclerView recyclerView;
    private ReservaAdapter adapter;
    private FloatingActionButton fabAdd;
    private AutoCompleteTextView actvFilterEstado;
    private MaterialButton btnClearFilters;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialToolbar toolbar;

    private ReservaRepository reservaRepository;
    private ClienteRepository clienteRepository;
    private YateRepository yateRepository;

    private List<Cliente> clientesList = new ArrayList<>();
    private List<Yate> yatesList = new ArrayList<>();
    private Map<String, Cliente> clientesMap = new HashMap<>();
    private Map<String, Yate> yatesMap = new HashMap<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        // Get current user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, "unknown");

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        reservaRepository = new ReservaRepository(this);
        clienteRepository = new ClienteRepository(this);
        yateRepository = new YateRepository(this);

        loadData();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewReservas);
        fabAdd = findViewById(R.id.fabAddReserva);
        actvFilterEstado = findViewById(R.id.actvFilterEstado);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ReservaAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddReservaDialog());

        // Setup filtro de estado
        String[] estados = {"Todos", "Pendiente", "Confirmada", "Cancelada"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        actvFilterEstado.setAdapter(estadoAdapter);
        actvFilterEstado.setText(estados[0], false);

        actvFilterEstado.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEstado = (String) parent.getItemAtPosition(position);
            adapter.filterByEstado(selectedEstado);
        });

        btnClearFilters.setOnClickListener(v -> {
            actvFilterEstado.setText("Todos", false);
            adapter.filterByEstado("Todos");
        });
    }

    private void loadData() {
        showLoading(true);

        // Load clientes first
        clienteRepository.getAllClientes(new ClienteRepository.ClientesCallback() {
            @Override
            public void onSuccess(List<Cliente> clientes) {
                clientesList = clientes;
                clientesMap.clear();
                for (Cliente cliente : clientes) {
                    clientesMap.put(cliente.getId(), cliente);
                }

                // Then load yates
                loadYates();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ReservasActivity.this, "Error al cargar clientes: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadYates() {
        yateRepository.getAllYates(new YateRepository.YatesCallback() {
            @Override
            public void onSuccess(List<Yate> yates) {
                yatesList = yates;
                yatesMap.clear();
                for (Yate yate : yates) {
                    yatesMap.put(yate.getId(), yate);
                }

                // Finally load reservas
                loadReservas();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ReservasActivity.this, "Error al cargar yates: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadReservas() {
        android.util.Log.d("ReservasActivity", "loadReservas() called");
        reservaRepository.getAllReservas(new ReservaRepository.ReservasCallback() {
            @Override
            public void onSuccess(List<Reserva> reservas) {
                runOnUiThread(() -> {
                    android.util.Log.d("ReservasActivity", "Reservas cargadas: " + reservas.size() + " reservas");
                    for (Reserva r : reservas) {
                        android.util.Log.d("ReservasActivity", "  Reserva ID=" + r.getId() +
                                ", clienteId=" + r.getClienteId() +
                                ", yateId=" + r.getYateId() +
                                ", estado=" + r.getEstado());
                    }
                    showLoading(false);
                    adapter.setReservas(reservas);
                    updateEmptyState(reservas.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    android.util.Log.e("ReservasActivity", "Error al cargar reservas: " + error);
                    showLoading(false);
                    Toast.makeText(ReservasActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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
    public void onReservaClick(Reserva reserva) {
        showEditReservaDialog(reserva);
    }

    @Override
    public void onReservaMenuClick(Reserva reserva, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_reserva_item, popup.getMenu());

        // Disable cancel option if already cancelled
        if (reserva.getEstado().equals(Reserva.ESTADO_CANCELADA)) {
            popup.getMenu().findItem(R.id.action_cancel).setEnabled(false);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                showEditReservaDialog(reserva);
                return true;
            } else if (itemId == R.id.action_cancel) {
                showCancelConfirmation(reserva);
                return true;
            } else if (itemId == R.id.action_delete) {
                showDeleteConfirmation(reserva);
                return true;
            }
            return false;
        });

        popup.show();
    }

    @Override
    public String getClienteNombre(String clienteId) {
        Cliente cliente = clientesMap.get(clienteId);
        return cliente != null ? cliente.getNombreCompleto() : null;
    }

    @Override
    public String getYateNombre(String yateId) {
        Yate yate = yatesMap.get(yateId);
        return yate != null ? yate.getDisplayName() : null;
    }

    private void showAddReservaDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reserva, null);

        TextInputLayout tilCliente = dialogView.findViewById(R.id.tilCliente);
        TextInputLayout tilYate = dialogView.findViewById(R.id.tilYate);
        TextInputLayout tilFechaInicio = dialogView.findViewById(R.id.tilFechaInicio);
        TextInputLayout tilFechaFin = dialogView.findViewById(R.id.tilFechaFin);
        TextInputLayout tilPrecioTotal = dialogView.findViewById(R.id.tilPrecioTotal);
        TextInputLayout tilEstado = dialogView.findViewById(R.id.tilEstado);

        AutoCompleteTextView actvCliente = dialogView.findViewById(R.id.actvCliente);
        AutoCompleteTextView actvYate = dialogView.findViewById(R.id.actvYate);
        TextInputEditText etFechaInicio = dialogView.findViewById(R.id.etFechaInicio);
        TextInputEditText etFechaFin = dialogView.findViewById(R.id.etFechaFin);
        TextInputEditText etPrecioTotal = dialogView.findViewById(R.id.etPrecioTotal);

        tilEstado.setVisibility(View.GONE); // Hide estado in creation

        // Setup clientes dropdown
        List<String> clienteNames = new ArrayList<>();
        for (Cliente cliente : clientesList) {
            clienteNames.add(cliente.getNombreCompleto());
        }
        ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, clienteNames);
        actvCliente.setAdapter(clienteAdapter);

        // Setup yates dropdown
        List<String> yateNames = new ArrayList<>();
        for (Yate yate : yatesList) {
            android.util.Log.d("ReservasActivity", "Yate en dropdown: ID=" + yate.getId() +
                    ", marca=" + yate.getMarca() +
                    ", modelo=" + yate.getModelo() +
                    ", anio=" + yate.getAnio() +
                    ", capacidad=" + yate.getCapacidad() +
                    ", precioDia=" + yate.getPrecioDia() +
                    ", displayName=" + yate.getDisplayName());
            yateNames.add(yate.getDisplayName());
        }
        android.util.Log.d("ReservasActivity", "Total yates en dropdown: " + yateNames.size());
        ArrayAdapter<String> yateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yateNames);
        actvYate.setAdapter(yateAdapter);

        // Date pickers
        Calendar calendar = Calendar.getInstance();
        final long[] fechaInicioMillis = {0};
        final long[] fechaFinMillis = {0};

        etFechaInicio.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaInicioMillis[0] = calendar.getTimeInMillis();
                        etFechaInicio.setText(dateFormat.format(calendar.getTime()));
                        calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        etFechaFin.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaFinMillis[0] = calendar.getTimeInMillis();
                        etFechaFin.setText(dateFormat.format(calendar.getTime()));
                        calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Auto-calculate price when yate changes
        actvYate.setOnItemClickListener((parent, view, position, id) -> {
            calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String clienteNombre = actvCliente.getText().toString().trim();
            String yateNombre = actvYate.getText().toString().trim();
            String fechaInicio = etFechaInicio.getText().toString().trim();
            String fechaFin = etFechaFin.getText().toString().trim();

            // Validaciones
            if (clienteNombre.isEmpty()) {
                Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show();
                return;
            }

            if (yateNombre.isEmpty()) {
                Toast.makeText(this, "Seleccione un yate", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fechaInicio.isEmpty()) {
                Toast.makeText(this, "Seleccione fecha de inicio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fechaFin.isEmpty()) {
                Toast.makeText(this, "Seleccione fecha de fin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fechaFinMillis[0] <= fechaInicioMillis[0]) {
                Toast.makeText(this, "La fecha de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get cliente and yate IDs
            String clienteId = getClienteIdByNombre(clienteNombre);
            String yateId = getYateIdByNombre(yateNombre);

            if (clienteId == null || yateId == null) {
                Toast.makeText(this, "Error: Cliente o Yate no encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate days and price
            long days = (fechaFinMillis[0] - fechaInicioMillis[0]) / (1000 * 60 * 60 * 24);
            Yate selectedYate = yatesMap.get(yateId);
            double precioTotal = days * selectedYate.getPrecioDia();

            // Create reserva
            Reserva newReserva = new Reserva(clienteId, yateId, fechaInicioMillis[0], fechaFinMillis[0], precioTotal, currentUserId);

            android.util.Log.d("ReservasActivity", "Creando reserva: clienteId=" + clienteId +
                    ", yateId=" + yateId +
                    ", fechaInicio=" + fechaInicioMillis[0] +
                    ", fechaFin=" + fechaFinMillis[0] +
                    ", precioTotal=" + precioTotal);

            reservaRepository.insertReserva(newReserva, new ReservaRepository.ReservaCallback() {
                @Override
                public void onSuccess(Reserva reserva) {
                    runOnUiThread(() -> {
                        android.util.Log.d("ReservasActivity", "Reserva creada con exito: ID=" + reserva.getId());
                        Toast.makeText(ReservasActivity.this, "Reserva creada exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadReservas();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        android.util.Log.e("ReservasActivity", "Error al crear reserva: " + error);
                        Toast.makeText(ReservasActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showEditReservaDialog(Reserva reserva) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reserva, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputLayout tilCliente = dialogView.findViewById(R.id.tilCliente);
        TextInputLayout tilYate = dialogView.findViewById(R.id.tilYate);
        TextInputLayout tilFechaInicio = dialogView.findViewById(R.id.tilFechaInicio);
        TextInputLayout tilFechaFin = dialogView.findViewById(R.id.tilFechaFin);
        TextInputLayout tilPrecioTotal = dialogView.findViewById(R.id.tilPrecioTotal);
        TextInputLayout tilEstado = dialogView.findViewById(R.id.tilEstado);

        AutoCompleteTextView actvCliente = dialogView.findViewById(R.id.actvCliente);
        AutoCompleteTextView actvYate = dialogView.findViewById(R.id.actvYate);
        AutoCompleteTextView actvEstado = dialogView.findViewById(R.id.actvEstado);
        TextInputEditText etFechaInicio = dialogView.findViewById(R.id.etFechaInicio);
        TextInputEditText etFechaFin = dialogView.findViewById(R.id.etFechaFin);
        TextInputEditText etPrecioTotal = dialogView.findViewById(R.id.etPrecioTotal);

        tvTitle.setText("Editar Reserva");
        tilEstado.setVisibility(View.VISIBLE);

        // Setup estados dropdown
        String[] estados = {Reserva.ESTADO_PENDIENTE, Reserva.ESTADO_CONFIRMADA, Reserva.ESTADO_CANCELADA};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, estados);
        actvEstado.setAdapter(estadoAdapter);
        actvEstado.setText(reserva.getEstado(), false);

        // Setup clientes dropdown
        List<String> clienteNames = new ArrayList<>();
        for (Cliente cliente : clientesList) {
            clienteNames.add(cliente.getNombreCompleto());
        }
        ArrayAdapter<String> clienteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, clienteNames);
        actvCliente.setAdapter(clienteAdapter);

        // Setup yates dropdown
        List<String> yateNames = new ArrayList<>();
        for (Yate yate : yatesList) {
            android.util.Log.d("ReservasActivity", "Yate en dropdown: ID=" + yate.getId() +
                    ", marca=" + yate.getMarca() +
                    ", modelo=" + yate.getModelo() +
                    ", anio=" + yate.getAnio() +
                    ", capacidad=" + yate.getCapacidad() +
                    ", precioDia=" + yate.getPrecioDia() +
                    ", displayName=" + yate.getDisplayName());
            yateNames.add(yate.getDisplayName());
        }
        android.util.Log.d("ReservasActivity", "Total yates en dropdown: " + yateNames.size());
        ArrayAdapter<String> yateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yateNames);
        actvYate.setAdapter(yateAdapter);

        // Fill current values
        Cliente currentCliente = clientesMap.get(reserva.getClienteId());
        Yate currentYate = yatesMap.get(reserva.getYateId());

        if (currentCliente != null) {
            actvCliente.setText(currentCliente.getNombreCompleto(), false);
        }
        if (currentYate != null) {
            actvYate.setText(currentYate.getDisplayName(), false);
        }

        etFechaInicio.setText(dateFormat.format(new Date(reserva.getFechaInicio())));
        etFechaFin.setText(dateFormat.format(new Date(reserva.getFechaFin())));
        etPrecioTotal.setText(String.valueOf(reserva.getPrecioTotal()));

        final long[] fechaInicioMillis = {reserva.getFechaInicio()};
        final long[] fechaFinMillis = {reserva.getFechaFin()};

        // Date pickers
        Calendar calendar = Calendar.getInstance();

        etFechaInicio.setOnClickListener(v -> {
            calendar.setTimeInMillis(fechaInicioMillis[0]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaInicioMillis[0] = calendar.getTimeInMillis();
                        etFechaInicio.setText(dateFormat.format(calendar.getTime()));
                        calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        etFechaFin.setOnClickListener(v -> {
            calendar.setTimeInMillis(fechaFinMillis[0]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        fechaFinMillis[0] = calendar.getTimeInMillis();
                        etFechaFin.setText(dateFormat.format(calendar.getTime()));
                        calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Auto-calculate price when yate changes
        actvYate.setOnItemClickListener((parent, view, position, id) -> {
            calculatePrice(actvYate, etFechaInicio, etFechaFin, etPrecioTotal);
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String clienteNombre = actvCliente.getText().toString().trim();
            String yateNombre = actvYate.getText().toString().trim();
            String estado = actvEstado.getText().toString().trim();
            String fechaInicio = etFechaInicio.getText().toString().trim();
            String fechaFin = etFechaFin.getText().toString().trim();

            // Validaciones
            if (clienteNombre.isEmpty() || yateNombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fechaFinMillis[0] <= fechaInicioMillis[0]) {
                Toast.makeText(this, "La fecha de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get IDs
            String clienteId = getClienteIdByNombre(clienteNombre);
            String yateId = getYateIdByNombre(yateNombre);

            if (clienteId == null || yateId == null) {
                Toast.makeText(this, "Error: Cliente o Yate no encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate price
            long days = (fechaFinMillis[0] - fechaInicioMillis[0]) / (1000 * 60 * 60 * 24);
            Yate selectedYate = yatesMap.get(yateId);
            double precioTotal = days * selectedYate.getPrecioDia();

            // Update reserva
            reserva.setClienteId(clienteId);
            reserva.setYateId(yateId);
            reserva.setFechaInicio(fechaInicioMillis[0]);
            reserva.setFechaFin(fechaFinMillis[0]);
            reserva.setPrecioTotal(precioTotal);
            reserva.setEstado(estado);

            reservaRepository.updateReserva(reserva, currentUserId, new ReservaRepository.ReservaCallback() {
                @Override
                public void onSuccess(Reserva reserva) {
                    runOnUiThread(() -> {
                        Toast.makeText(ReservasActivity.this, "Reserva actualizada exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadReservas();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(ReservasActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showCancelConfirmation(Reserva reserva) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancelar Reserva")
                .setMessage("¿Está seguro que desea cancelar esta reserva?")
                .setPositiveButton("Cancelar Reserva", (dialog, which) -> {
                    reservaRepository.cancelReserva(reserva.getId(), currentUserId, new ReservaRepository.ReservaCallback() {
                        @Override
                        public void onSuccess(Reserva reserva) {
                            runOnUiThread(() -> {
                                Toast.makeText(ReservasActivity.this, "Reserva cancelada", Toast.LENGTH_SHORT).show();
                                loadReservas();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(ReservasActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDeleteConfirmation(Reserva reserva) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar Reserva")
                .setMessage("¿Está seguro que desea eliminar esta reserva? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    reservaRepository.deleteReserva(reserva.getId(), new ReservaRepository.ReservaCallback() {
                        @Override
                        public void onSuccess(Reserva reserva) {
                            runOnUiThread(() -> {
                                Toast.makeText(ReservasActivity.this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
                                loadReservas();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(ReservasActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void calculatePrice(AutoCompleteTextView actvYate, TextInputEditText etFechaInicio,
                                 TextInputEditText etFechaFin, TextInputEditText etPrecioTotal) {
        String yateNombre = actvYate.getText().toString().trim();
        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();

        if (yateNombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            return;
        }

        String yateId = getYateIdByNombre(yateNombre);
        if (yateId == null) {
            return;
        }

        Yate yate = yatesMap.get(yateId);
        if (yate == null) {
            return;
        }

        try {
            Date dateInicio = dateFormat.parse(fechaInicio);
            Date dateFin = dateFormat.parse(fechaFin);

            if (dateInicio != null && dateFin != null && dateFin.after(dateInicio)) {
                long days = (dateFin.getTime() - dateInicio.getTime()) / (1000 * 60 * 60 * 24);
                double precio = days * yate.getPrecioDia();
                etPrecioTotal.setText(String.format(Locale.getDefault(), "%.2f", precio));
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
    }

    private String getClienteIdByNombre(String nombreCompleto) {
        for (Cliente cliente : clientesList) {
            if (cliente.getNombreCompleto().equals(nombreCompleto)) {
                return cliente.getId();
            }
        }
        return null;
    }

    private String getYateIdByNombre(String displayName) {
        for (Yate yate : yatesList) {
            if (yate.getDisplayName().equals(displayName)) {
                return yate.getId();
            }
        }
        return null;
    }
}
