package com.example.sistemadeyates.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean validateEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePassword(String password, int minLength) {
        if (!isNotEmpty(password)) {
            return false;
        }
        return password.length() >= minLength;
    }

    public static boolean validatePassword(String password) {
        return validatePassword(password, 8);
    }

    public static boolean validateUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        return username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }

    public static String getEmailError(String email) {
        if (!isNotEmpty(email)) {
            return "El email es requerido";
        }
        if (!validateEmail(email)) {
            return "El email no es valido";
        }
        return null;
    }

    public static String getPasswordError(String password, int minLength) {
        if (!isNotEmpty(password)) {
            return "La contrasena es requerida";
        }
        if (password.length() < minLength) {
            return "La contrasena debe tener al menos " + minLength + " caracteres";
        }
        return null;
    }

    public static String getUsernameError(String username) {
        if (!isNotEmpty(username)) {
            return "El usuario es requerido";
        }
        if (username.length() < 3) {
            return "El usuario debe tener al menos 3 caracteres";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "El usuario solo puede contener letras, numeros y guion bajo";
        }
        return null;
    }
}
