package com.example.sistemadeyates.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class EncryptionUtils {
    private static final int DEFAULT_COST = 12;

    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(DEFAULT_COST, plainPassword.toCharArray());
    }

    public static String hashPassword(String plainPassword, int cost) {
        return BCrypt.withDefaults().hashToString(cost, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
