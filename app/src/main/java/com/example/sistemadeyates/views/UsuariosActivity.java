package com.example.sistemadeyates.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemadeyates.R;
import com.example.sistemadeyates.adapters.UsuarioAdapter;
import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.repositories.UserRepository;
import com.example.sistemadeyates.utils.EncryptionUtils;
import com.example.sistemadeyates.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class UsuariosActivity extends AppCompatActivity implements UsuarioAdapter.OnUsuarioClickListener {

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialToolbar toolbar;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        userRepository = new UserRepository(this);
        loadUsuarios();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAddUser);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBarUsers);
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
        adapter = new UsuarioAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddUsuarioDialog());

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

    private void loadUsuarios() {
        showLoading(true);
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                runOnUiThread(() -> {
                    showLoading(false);
                    adapter.setUsuarios(users);
                    updateEmptyState(users.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(UsuariosActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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
    public void onUsuarioClick(User usuario) {
        showEditUsuarioDialog(usuario);
    }

    @Override
    public void onUsuarioMenuClick(User usuario, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_usuario_item, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                showEditUsuarioDialog(usuario);
                return true;
            } else if (itemId == R.id.action_delete) {
                showDeleteConfirmation(usuario);
                return true;
            } else if (itemId == R.id.action_toggle_status) {
                toggleUsuarioStatus(usuario);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showAddUsuarioDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null);

        TextInputLayout tilUsername = dialogView.findViewById(R.id.tilClientName);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.tilIdentityCard);
        TextInputLayout tilRol = dialogView.findViewById(R.id.tilRol);

        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);
        AutoCompleteTextView actvRol = dialogView.findViewById(R.id.actvRol);
        SwitchMaterial switchActivo = dialogView.findViewById(R.id.switchActivo);

        // Setup rol dropdown
        String[] roles = {"ADMIN", "EMPLEADO"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        actvRol.setAdapter(adapterRoles);
        actvRol.setText(roles[1], false); // Default: EMPLEADO

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = actvRol.getText().toString().trim();
            boolean activo = switchActivo.isChecked();

            // Validaciones
            if (!ValidationUtils.validateUsername(username)) {
                tilUsername.setError(ValidationUtils.getUsernameError(username));
                return;
            } else {
                tilUsername.setError(null);
            }

            if (!ValidationUtils.validateEmail(email)) {
                tilEmail.setError(ValidationUtils.getEmailError(email));
                return;
            } else {
                tilEmail.setError(null);
            }

            if (!ValidationUtils.validatePassword(password)) {
                tilPassword.setError(ValidationUtils.getPasswordError(password, 8));
                return;
            } else {
                tilPassword.setError(null);
            }

            if (rol.isEmpty()) {
                Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear usuario
            User newUser = new User(username, email, EncryptionUtils.hashPassword(password), rol);
            newUser.setActivo(activo);

            userRepository.insertUser(newUser, new UserRepository.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        Toast.makeText(UsuariosActivity.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUsuarios();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(UsuariosActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showEditUsuarioDialog(User usuario) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_usuario, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputLayout tilUsername = dialogView.findViewById(R.id.tilClientName);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.tilEmail);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.tilIdentityCard);
        TextInputLayout tilRol = dialogView.findViewById(R.id.tilRol);

        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);
        AutoCompleteTextView actvRol = dialogView.findViewById(R.id.actvRol);
        SwitchMaterial switchActivo = dialogView.findViewById(R.id.switchActivo);

        // Cambiar título
        tvTitle.setText("Editar Usuario");

        // Rellenar datos
        etUsername.setText(usuario.getUsername());
        etEmail.setText(usuario.getEmail());
        switchActivo.setChecked(usuario.isActivo());

        // Password opcional en edición
        tilPassword.setHelperText("Dejar vacío para mantener contraseña actual");

        // Setup rol dropdown
        String[] roles = {"ADMIN", "EMPLEADO"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        actvRol.setAdapter(adapterRoles);
        actvRol.setText(usuario.getRol(), false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = actvRol.getText().toString().trim();
            boolean activo = switchActivo.isChecked();

            // Validaciones
            if (!ValidationUtils.validateUsername(username)) {
                tilUsername.setError(ValidationUtils.getUsernameError(username));
                return;
            } else {
                tilUsername.setError(null);
            }

            if (!ValidationUtils.validateEmail(email)) {
                tilEmail.setError(ValidationUtils.getEmailError(email));
                return;
            } else {
                tilEmail.setError(null);
            }

            // Password es opcional en edición
            if (!password.isEmpty() && !ValidationUtils.validatePassword(password)) {
                tilPassword.setError(ValidationUtils.getPasswordError(password, 8));
                return;
            } else {
                tilPassword.setError(null);
            }

            if (rol.isEmpty()) {
                Toast.makeText(this, "Seleccione un rol", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar usuario
            usuario.setUsername(username);
            usuario.setEmail(email);
            if (!password.isEmpty()) {
                usuario.setPassword(EncryptionUtils.hashPassword(password));
            }
            usuario.setRol(rol);
            usuario.setActivo(activo);

            userRepository.updateUser(usuario, new UserRepository.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        Toast.makeText(UsuariosActivity.this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUsuarios();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(UsuariosActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        dialog.show();
    }

    private void showDeleteConfirmation(User usuario) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Está seguro que desea eliminar a " + usuario.getUsername() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    userRepository.deleteUser(usuario, new UserRepository.UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            runOnUiThread(() -> {
                                Toast.makeText(UsuariosActivity.this, "Usuario eliminado (inactivo)", Toast.LENGTH_SHORT).show();
                                loadUsuarios();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(UsuariosActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void toggleUsuarioStatus(User usuario) {
        usuario.setActivo(!usuario.isActivo());
        userRepository.updateUser(usuario, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    String status = user.isActivo() ? "activado" : "desactivado";
                    Toast.makeText(UsuariosActivity.this, "Usuario " + status, Toast.LENGTH_SHORT).show();
                    loadUsuarios();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(UsuariosActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
