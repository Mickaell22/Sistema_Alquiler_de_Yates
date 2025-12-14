package com.example.sistemadeyates.utils;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    // Método validateEmail usando Patterns (para compatibilidad)
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

    /**
     * Valida que un campo no esté vacío (alias de isNotEmpty para consistencia)
     */
    public static boolean validateNotEmpty(String text) {
        return isNotEmpty(text);
    }

    /**
     * Valida una cédula ecuatoriana (10 dígitos)
     */
    public static boolean validateCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }

        // Eliminar espacios y guiones
        cedula = cedula.trim().replaceAll("\\s+", "").replaceAll("-", "");

        // Validar que tenga exactamente 10 dígitos
        if (!cedula.matches("\\d{10}")) {
            return false;
        }

        // Los dos primeros dígitos deben ser válidos para Ecuador
        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        // Validar dígito verificador
        return validarDigitoVerificador(cedula);
    }

    /**
     * Algoritmo para validar dígito verificador de cédula ecuatoriana
     */
    private static boolean validarDigitoVerificador(String cedula) {
        try {
            int total = 0;
            int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
            int verificador = Integer.parseInt(cedula.substring(9, 10));

            for (int i = 0; i < 9; i++) {
                int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
                if (valor >= 10) {
                    valor = valor - 9;
                }
                total += valor;
            }

            int digitoVerificador = total % 10;
            if (digitoVerificador != 0) {
                digitoVerificador = 10 - digitoVerificador;
            }

            return digitoVerificador == verificador;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida un número de teléfono (10 dígitos, opcional)
     */
    public static boolean validateTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return true; // Teléfono es opcional
        }

        // Eliminar espacios, guiones, paréntesis
        telefono = telefono.trim()
                .replaceAll("\\s+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");

        // Validar que tenga 10 dígitos
        return telefono.matches("\\d{10}");
    }

    /**
     * Valida nombres (solo letras y espacios)
     */
    public static boolean validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        // Permitir letras, espacios, tildes y ñ
        return nombre.trim().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$");
    }

    /**
     * Valida licencia náutica (opcional, máximo 25 caracteres alfanuméricos)
     */
    public static boolean validateLicencia(String licencia) {
        if (licencia == null || licencia.trim().isEmpty()) {
            return true; // Licencia es opcional
        }

        // Máximo 25 caracteres, solo letras y números, mayúsculas opcional
        return licencia.trim().length() <= 25 && licencia.matches("^[A-Za-z0-9]+$");
    }

    // Métodos para obtener mensajes de error específicos para clientes

    public static String getCedulaError() {
        return "Cédula inválida. Debe tener 10 dígitos y ser válida para Ecuador";
    }

    public static String getTelefonoError() {
        return "Teléfono inválido. Debe tener 10 dígitos";
    }

    public static String getNombreError() {
        return "Solo se permiten letras y espacios";
    }

    public static String getLicenciaError() {
        return "Licencia inválida. Máximo 25 caracteres alfanuméricos";
    }

    /**
     * Valida todos los campos de un cliente
     */
    public static String validateCliente(String nombres, String apellidos, String cedula,
                                         String telefono, String email, String licencia) {

        if (!validateNotEmpty(nombres)) {
            return "Los nombres son requeridos";
        }

        if (!validateNombre(nombres)) {
            return getNombreError();
        }

        if (!validateNotEmpty(apellidos)) {
            return "Los apellidos son requeridos";
        }

        if (!validateNombre(apellidos)) {
            return getNombreError();
        }

        if (!validateCedula(cedula)) {
            return getCedulaError();
        }

        if (!validateTelefono(telefono)) {
            return getTelefonoError();
        }

        if (!validateEmail(email)) {
            return getEmailError(email);
        }

        if (!validateLicencia(licencia)) {
            return getLicenciaError();
        }

        return null; // Todo válido
    }
}