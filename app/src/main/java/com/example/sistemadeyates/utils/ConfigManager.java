package com.example.sistemadeyates.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static ConfigManager instance;
    private Map<String, String> config;

    private ConfigManager(Context context) {
        config = new HashMap<>();
        loadConfig(context);
    }

    public static synchronized ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadConfig(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(".env");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading .env file", e);
        }
    }

    public String get(String key) {
        return config.get(key);
    }

    public String get(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }

    public int getInt(String key) {
        String value = config.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing int value for key: " + key, e);
            }
        }
        return 0;
    }

    public int getInt(String key, int defaultValue) {
        String value = config.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing int value for key: " + key, e);
            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String key) {
        String value = config.get(key);
        return value != null && Boolean.parseBoolean(value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = config.get(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}
