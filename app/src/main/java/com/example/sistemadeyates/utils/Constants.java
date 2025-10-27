package com.example.sistemadeyates.utils;

public class Constants {
    public static final String PREF_NAME = "YatesPreferences";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_USER_ROL = "user_rol";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_LOGIN_TIME = "login_time";

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_EMPLEADO = "EMPLEADO";

    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_CREATE_USER = "CREATE_USER";
    public static final String ACTION_UPDATE_USER = "UPDATE_USER";
    public static final String ACTION_DELETE_USER = "DELETE_USER";

    public static final int REQUEST_CODE_LOGIN = 1001;

    public static class ErrorCodes {
        public static final int SUCCESS = 0;
        public static final int ERROR_INVALID_CREDENTIALS = 1;
        public static final int ERROR_USER_NOT_FOUND = 2;
        public static final int ERROR_USER_INACTIVE = 3;
        public static final int ERROR_SESSION_EXPIRED = 4;
        public static final int ERROR_DATABASE = 5;
        public static final int ERROR_VALIDATION = 6;
    }

    public static class ErrorMessages {
        public static final String INVALID_CREDENTIALS = "Usuario o contrasena incorrectos";
        public static final String USER_NOT_FOUND = "Usuario no encontrado";
        public static final String USER_INACTIVE = "Usuario inactivo. Contacte al administrador";
        public static final String SESSION_EXPIRED = "Sesion expirada. Por favor inicie sesion nuevamente";
        public static final String DATABASE_ERROR = "Error de base de datos";
        public static final String VALIDATION_ERROR = "Error de validacion";
    }
}
