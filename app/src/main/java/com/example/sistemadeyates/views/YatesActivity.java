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
import com.example.sistemadeyates.adapters.YateAdapter;
import com.example.sistemadeyates.models.Yate;
import com.example.sistemadeyates.repositories.YateRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class YatesActivity extends AppCompatActivity implements YateAdapter.OnYateClickListener {

    private RecyclerView recyclerView;
    private YateAdapter adapter;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialToolbar toolbar;
    private YateRepository yateRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yates);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        yateRepository = new YateRepository(this);
        loadYates();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewYates);
        fabAdd = findViewById(R.id.fabAddYate);
        etSearch = findViewById(R.id.etSearchYates);
        progressBar = findViewById(R.id.progressBarYates);
        emptyState = findViewById(R.id.emptyStateYates);
        toolbar = findViewById(R.id.toolbarYates);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new YateAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddYateDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadYates() {
        showLoading(true);
        yateRepository.getAllYates(new YateRepository.YatesCallback() {
            @Override
            public void onSuccess(List<Yate> yates) {
                runOnUiThread(() -> {
                    showLoading(false);
                    adapter.setYates(yates);
                    updateEmptyState(yates.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(YatesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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
    public void onYateClick(Yate yate) {
        showEditYateDialog(yate);
    }

    @Override
    public void onYateMenuClick(Yate yate, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_yate_item, popup.getMenu());

        MenuItem toggleItem = popup.getMenu().findItem(R.id.action_toggle_disponible);
        if (toggleItem != null) {
            String statusText = yate.isDisponible() ? "Marcar no disponible" : "Marcar disponible";
            toggleItem.setTitle(statusText);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                showEditYateDialog(yate);
                return true;
            } else if (itemId == R.id.action_toggle_disponible) {
                toggleYateDisponible(yate);
                return true;
            } else if (itemId == R.id.action_delete) {
                showDeleteConfirmation(yate);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showAddYateDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_yate, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvTituloYate);
        tvTitle.setText("Registro de Yate");

        TextInputLayout tilMarca = dialogView.findViewById(R.id.tilMarca);
        TextInputLayout tilModelo = dialogView.findViewById(R.id.tilModelo);
        TextInputLayout tilAnio = dialogView.findViewById(R.id.tilAnio);
        TextInputLayout tilTamanio = dialogView.findViewById(R.id.tilTamanio);
        TextInputLayout tilCapacidad = dialogView.findViewById(R.id.tilCapacidad);
        TextInputLayout tilMatricula = dialogView.findViewById(R.id.tilMatricula);
        TextInputLayout tilPrecioDia = dialogView.findViewById(R.id.tilPrecioDia);

        TextInputEditText etMarca = dialogView.findViewById(R.id.etMarca);
        TextInputEditText etModelo = dialogView.findViewById(R.id.etModelo);
        TextInputEditText etAnio = dialogView.findViewById(R.id.etAnio);
        TextInputEditText etTamanio = dialogView.findViewById(R.id.etTamanio);
        TextInputEditText etCapacidad = dialogView.findViewById(R.id.etCapacidad);
        TextInputEditText etMatricula = dialogView.findViewById(R.id.etMatricula);
        TextInputEditText etPrecioDia = dialogView.findViewById(R.id.etPrecioDia);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnGuardarYate);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelarYate);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String marca = etMarca.getText().toString().trim();
            String modelo = etModelo.getText().toString().trim();
            String anioStr = etAnio.getText().toString().trim();
            String tamanio = etTamanio.getText().toString().trim();
            String capacidadStr = etCapacidad.getText().toString().trim();
            String matricula = etMatricula.getText().toString().trim();
            String precioDiaStr = etPrecioDia.getText().toString().trim();

            tilMarca.setError(null);
            tilModelo.setError(null);
            tilAnio.setError(null);
            tilTamanio.setError(null);
            tilCapacidad.setError(null);
            tilMatricula.setError(null);
            tilPrecioDia.setError(null);

            boolean isValid = true;

            if (marca.isEmpty()) {
                tilMarca.setError("La marca es obligatoria");
                isValid = false;
            }

            if (modelo.isEmpty()) {
                tilModelo.setError("El modelo es obligatorio");
                isValid = false;
            }

            int anio = 0;
            try {
                anio = Integer.parseInt(anioStr);
            } catch (NumberFormatException e) {
                tilAnio.setError("Año inválido");
                isValid = false;
            }

            if (tamanio.isEmpty()) {
                tilTamanio.setError("El tamaño es obligatorio");
                isValid = false;
            }

            int capacidad = 0;
            try {
                capacidad = Integer.parseInt(capacidadStr);
            } catch (NumberFormatException e) {
                tilCapacidad.setError("Capacidad inválida");
                isValid = false;
            }

            if (matricula.isEmpty()) {
                tilMatricula.setError("La matrícula es obligatoria");
                isValid = false;
            }

            double precioDia = 0;
            try {
                precioDia = Double.parseDouble(precioDiaStr);
            } catch (NumberFormatException e) {
                tilPrecioDia.setError("Precio por día inválido");
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            long now = System.currentTimeMillis();
            Yate newYate = new Yate();
            newYate.setMarca(marca);
            newYate.setModelo(modelo);
            newYate.setAnio(anio);
            newYate.setTamanio(tamanio);
            newYate.setCapacidad(capacidad);
            newYate.setMatricula(matricula);
            newYate.setPrecioDia(precioDia);
            newYate.setDisponible(true);
            newYate.setFechaCreacion(now);

            yateRepository.insertYate(newYate, new YateRepository.YateCallback() {
                @Override
                public void onSuccess(Yate yate) {
                    runOnUiThread(() -> {
                        Toast.makeText(YatesActivity.this,
                                "Yate creado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadYates();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(YatesActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showEditYateDialog(Yate yate) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_yate, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvTituloYate);
        tvTitle.setText("Editar Yate");

        TextInputLayout tilMarca = dialogView.findViewById(R.id.tilMarca);
        TextInputLayout tilModelo = dialogView.findViewById(R.id.tilModelo);
        TextInputLayout tilAnio = dialogView.findViewById(R.id.tilAnio);
        TextInputLayout tilTamanio = dialogView.findViewById(R.id.tilTamanio);
        TextInputLayout tilCapacidad = dialogView.findViewById(R.id.tilCapacidad);
        TextInputLayout tilMatricula = dialogView.findViewById(R.id.tilMatricula);
        TextInputLayout tilPrecioDia = dialogView.findViewById(R.id.tilPrecioDia);

        TextInputEditText etMarca = dialogView.findViewById(R.id.etMarca);
        TextInputEditText etModelo = dialogView.findViewById(R.id.etModelo);
        TextInputEditText etAnio = dialogView.findViewById(R.id.etAnio);
        TextInputEditText etTamanio = dialogView.findViewById(R.id.etTamanio);
        TextInputEditText etCapacidad = dialogView.findViewById(R.id.etCapacidad);
        TextInputEditText etMatricula = dialogView.findViewById(R.id.etMatricula);
        TextInputEditText etPrecioDia = dialogView.findViewById(R.id.etPrecioDia);

        etMarca.setText(yate.getMarca());
        etModelo.setText(yate.getModelo());
        etAnio.setText(String.valueOf(yate.getAnio()));
        etTamanio.setText(yate.getTamanio());
        etCapacidad.setText(String.valueOf(yate.getCapacidad()));
        etMatricula.setText(yate.getMatricula());
        etPrecioDia.setText(String.valueOf(yate.getPrecioDia()));

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnGuardarYate);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelarYate);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String marca = etMarca.getText().toString().trim();
            String modelo = etModelo.getText().toString().trim();
            String anioStr = etAnio.getText().toString().trim();
            String tamanio = etTamanio.getText().toString().trim();
            String capacidadStr = etCapacidad.getText().toString().trim();
            String matricula = etMatricula.getText().toString().trim();
            String precioDiaStr = etPrecioDia.getText().toString().trim();

            tilMarca.setError(null);
            tilModelo.setError(null);
            tilAnio.setError(null);
            tilTamanio.setError(null);
            tilCapacidad.setError(null);
            tilMatricula.setError(null);
            tilPrecioDia.setError(null);

            boolean isValid = true;

            if (marca.isEmpty()) {
                tilMarca.setError("La marca es obligatoria");
                isValid = false;
            }

            if (modelo.isEmpty()) {
                tilModelo.setError("El modelo es obligatorio");
                isValid = false;
            }

            int anio = yate.getAnio();
            try {
                anio = Integer.parseInt(anioStr);
            } catch (NumberFormatException e) {
                tilAnio.setError("Año inválido");
                isValid = false;
            }

            if (tamanio.isEmpty()) {
                tilTamanio.setError("El tamaño es obligatorio");
                isValid = false;
            }

            int capacidad = yate.getCapacidad();
            try {
                capacidad = Integer.parseInt(capacidadStr);
            } catch (NumberFormatException e) {
                tilCapacidad.setError("Capacidad inválida");
                isValid = false;
            }

            if (matricula.isEmpty()) {
                tilMatricula.setError("La matrícula es obligatoria");
                isValid = false;
            }

            double precioDia = yate.getPrecioDia();
            try {
                precioDia = Double.parseDouble(precioDiaStr);
            } catch (NumberFormatException e) {
                tilPrecioDia.setError("Precio por día inválido");
                isValid = false;
            }

            if (!isValid) {
                return;
            }

            yate.setMarca(marca);
            yate.setModelo(modelo);
            yate.setAnio(anio);
            yate.setTamanio(tamanio);
            yate.setCapacidad(capacidad);
            yate.setMatricula(matricula);
            yate.setPrecioDia(precioDia);

            yateRepository.updateYate(yate, new YateRepository.YateCallback() {
                @Override
                public void onSuccess(Yate updatedYate) {
                    runOnUiThread(() -> {
                        Toast.makeText(YatesActivity.this,
                                "Yate actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadYates();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(YatesActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void toggleYateDisponible(Yate yate) {
        boolean nuevoEstado = !yate.isDisponible();
        String accion = nuevoEstado ? "Marcar disponible" : "Marcar no disponible";

        new MaterialAlertDialogBuilder(this)
                .setTitle(accion)
                .setMessage("¿Está seguro que desea " + accion.toLowerCase() + " el yate " +
                        (yate.getMarca() != null ? yate.getMarca() : "") + " " +
                        (yate.getModelo() != null ? yate.getModelo() : "") + "?")
                .setPositiveButton(accion, (dialog, which) -> {
                    yate.setDisponible(nuevoEstado);
                    yateRepository.updateYate(yate, new YateRepository.YateCallback() {
                        @Override
                        public void onSuccess(Yate updatedYate) {
                            runOnUiThread(() -> {
                                String resultado = nuevoEstado ? "disponible" : "no disponible";
                                Toast.makeText(YatesActivity.this,
                                        "Yate marcado como " + resultado,
                                        Toast.LENGTH_SHORT).show();
                                loadYates();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(YatesActivity.this,
                                        "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteConfirmation(Yate yate) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar Yate")
                .setMessage("¿Está seguro que desea marcar como no disponible el yate " +
                        (yate.getMarca() != null ? yate.getMarca() : "") + " " +
                        (yate.getModelo() != null ? yate.getModelo() : "") + "?")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    yateRepository.deleteYate(yate, new YateRepository.YateCallback() {
                        @Override
                        public void onSuccess(Yate deletedYate) {
                            runOnUiThread(() -> {
                                Toast.makeText(YatesActivity.this,
                                        "Yate marcado como no disponible",
                                        Toast.LENGTH_SHORT).show();
                                loadYates();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(YatesActivity.this,
                                        "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
