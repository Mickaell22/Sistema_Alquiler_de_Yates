package com.example.sistemadeyates.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sistemadeyates.MainActivity;
import com.example.sistemadeyates.R;
import com.example.sistemadeyates.controllers.AuthController;
import com.example.sistemadeyates.database.DatabaseHelper;
import com.example.sistemadeyates.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;

    private AuthController authController;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme preference
        applyTheme();

        setContentView(R.layout.activity_login);

        initializeViews();
        initializeControllers();
        setupListeners();

        // Mostrar loading mientras se inicializan usuarios
        showLoading(true);
        btnLogin.setEnabled(false);

        databaseHelper.initializeDefaultUsers(() -> {
            // After users are initialized, initialize clientes
            databaseHelper.initializeDefaultClientes(() -> {
                // After clientes are initialized, initialize yates
                databaseHelper.initializeDefaultYates(() -> {
                    // After yates are initialized, initialize reservas
                    databaseHelper.initializeDefaultReservas(() -> {
                        runOnUiThread(() -> {
                            showLoading(false);
                            btnLogin.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Sistema iniciado. Use: admin/admin123", Toast.LENGTH_LONG).show();
                        });
                    });
                });
            });
        });
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBarUsers);
    }

    private void initializeControllers() {
        authController = new AuthController(this);
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        btnLogin.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            authController.login(username, password, new AuthController.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, R.string.access_granted, Toast.LENGTH_LONG).show();

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", user.getUsername());
                            intent.putExtra("rol", user.getRol());
                            startActivity(intent);
                            finish();
                        }, 1000);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        btnLogin.setEnabled(true);
                        // Mostrar error específico
                        String errorMsg = error != null ? error : getString(R.string.access_denied);
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }, 500);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void applyTheme() {
        SharedPreferences themePrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
