package com.example.sistemadeyates.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sistemadeyates.models.ActivityLog;
import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.repositories.ActivityLogRepository;
import com.example.sistemadeyates.repositories.UserRepository;
import com.example.sistemadeyates.utils.Constants;
import com.example.sistemadeyates.utils.EncryptionUtils;

public class AuthController {
    private final Context context;
    private final UserRepository userRepository;
    private final ActivityLogRepository logRepository;
    private final SharedPreferences preferences;

    public AuthController(Context context) {
        this.context = context;
        this.userRepository = new UserRepository(context);
        this.logRepository = new ActivityLogRepository(context);
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public void login(String username, String password, LoginCallback callback) {
        userRepository.getUserByUsername(username, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    callback.onError(Constants.ErrorMessages.USER_NOT_FOUND);
                    return;
                }

                if (!user.isActivo()) {
                    callback.onError(Constants.ErrorMessages.USER_INACTIVE);
                    return;
                }

                if (!EncryptionUtils.verifyPassword(password, user.getPassword())) {
                    callback.onError(Constants.ErrorMessages.INVALID_CREDENTIALS);
                    return;
                }

                saveSession(user);
                logActivity(user.getId(), Constants.ACTION_LOGIN, "Usuario inicio sesion");
                callback.onSuccess(user);
            }

            @Override
            public void onError(String error) {
                callback.onError(Constants.ErrorMessages.DATABASE_ERROR);
            }
        });
    }

    public void logout() {
        int userId = preferences.getInt(Constants.PREF_USER_ID, 0);
        if (userId > 0) {
            logActivity(userId, Constants.ACTION_LOGOUT, "Usuario cerro sesion");
        }
        clearSession();
    }

    public boolean isLoggedIn() {
        boolean isLoggedIn = preferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            long loginTime = preferences.getLong(Constants.PREF_LOGIN_TIME, 0);
            long currentTime = System.currentTimeMillis();
            long sessionTimeout = 3600000;

            if (currentTime - loginTime > sessionTimeout) {
                clearSession();
                return false;
            }
        }
        return isLoggedIn;
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(preferences.getInt(Constants.PREF_USER_ID, 0));
        user.setUsername(preferences.getString(Constants.PREF_USERNAME, ""));
        user.setRol(preferences.getString(Constants.PREF_USER_ROL, ""));
        return user;
    }

    private void saveSession(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.PREF_USER_ID, user.getId());
        editor.putString(Constants.PREF_USERNAME, user.getUsername());
        editor.putString(Constants.PREF_USER_ROL, user.getRol());
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
        editor.putLong(Constants.PREF_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    private void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void logActivity(int userId, String action, String details) {
        ActivityLog log = new ActivityLog(userId, action, details);
        logRepository.insertLog(log, null);
    }
}
