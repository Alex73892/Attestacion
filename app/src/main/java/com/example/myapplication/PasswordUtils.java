package com.example.myapplication;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final int SALT_LENGTH = 16; // байт
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            byte[] saltPlusHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltPlusHash, 0, salt.length);
            System.arraycopy(hash, 0, saltPlusHash, salt.length, hash.length);

            return Base64.encodeToString(saltPlusHash, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String password, String stored) {
        try {
            byte[] saltPlusHash = Base64.decode(stored, Base64.NO_WRAP);
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[saltPlusHash.length - SALT_LENGTH];

            System.arraycopy(saltPlusHash, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(saltPlusHash, SALT_LENGTH, hash, 0, hash.length);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            return slowEquals(hash, testHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i=0; i<a.length && i<b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}