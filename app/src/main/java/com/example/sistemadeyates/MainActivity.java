package com.example.sistemadeyates;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sistemadeyates.controllers.AuthController;
import com.example.sistemadeyates.views.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private MaterialToolbar toolbar;
    private TextView tvWelcome;
    private TextView tvUserRole;
    private MaterialButton btnLogout;
    private MaterialCardView cardUsuarios;
    private MaterialCardView cardClientes;
    private MaterialCardView cardYates;
    private MaterialCardView cardReservas;

    private AuthController authController;
    private String username;
    private String userRole;
    private SharedPreferences themePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme preference
        themePrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        applyTheme();

        setContentView(R.layout.activity_main);

        authController = new AuthController(this);

        if (!authController.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserRole = findViewById(R.id.tvUserRole);
        btnLogout = findViewById(R.id.btnLogout);
        cardUsuarios = findViewById(R.id.cardUsuarios);
        cardClientes = findViewById(R.id.cardClientes);
        cardYates = findViewById(R.id.cardYates);
        cardReservas = findViewById(R.id.cardReservas);

        setSupportActionBar(toolbar);
    }

    private void loadUserData() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userRole = intent.getStringExtra("rol");

        if (username != null) {
            tvWelcome.setText(getString(R.string.welcome_user, username));
        }

        if (userRole != null) {
            tvUserRole.setText("Rol: " + userRole);
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());

        cardUsuarios.setOnClickListener(v -> {
            Toast.makeText(this, "Modulo de Usuarios - Proximamente", Toast.LENGTH_SHORT).show();
        });

        cardClientes.setOnClickListener(v -> {
            Toast.makeText(this, "Modulo de Clientes - Proximamente", Toast.LENGTH_SHORT).show();
        });

        cardYates.setOnClickListener(v -> {
            Toast.makeText(this, "Modulo de Yates - Proximamente", Toast.LENGTH_SHORT).show();
        });

        cardReservas.setOnClickListener(v -> {
            Toast.makeText(this, "Modulo de Reservas - Proximamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        authController.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        boolean isDarkMode = themePrefs.getBoolean(KEY_DARK_MODE, false);
        String currentMode = isDarkMode ? getString(R.string.disable_dark_mode) : getString(R.string.enable_dark_mode);

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.settings))
                .setMessage(getString(R.string.dark_mode))
                .setPositiveButton(currentMode, (dialog, which) -> {
                    toggleTheme();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void toggleTheme() {
        boolean isDarkMode = themePrefs.getBoolean(KEY_DARK_MODE, false);
        themePrefs.edit().putBoolean(KEY_DARK_MODE, !isDarkMode).apply();
        recreate(); // Recreate activity to apply new theme
    }

    private void applyTheme() {
        boolean isDarkMode = themePrefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}