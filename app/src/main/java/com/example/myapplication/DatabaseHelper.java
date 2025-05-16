package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "password_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD_HASH = "password_hash";

    // Таблица паролей
    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_ENTRY_ID = "id";
    private static final String COLUMN_USER_LOGIN = "user_login";
    private static final String COLUMN_SERVICE = "service";
    private static final String COLUMN_LOGIN_ENTRY = "login_entry";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NOTES = "notes";

    private static final String PASSWORD_ENCRYPTION_KEY = "MySuperSecretKey"; // Для простоты

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LOGIN + " TEXT UNIQUE," +
                COLUMN_PASSWORD_HASH + " TEXT" + ")";

        String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + TABLE_PASSWORDS + " (" +
                COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_LOGIN + " TEXT," +
                COLUMN_SERVICE + " TEXT," +
                COLUMN_LOGIN_ENTRY + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_NOTES + " TEXT" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PASSWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление схемы базы данных при необходимости
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

    // Работа с пользователями
    public boolean addUser(String login, String passwordHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD_HASH, passwordHash);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public String getPasswordHash(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD_HASH},
                COLUMN_LOGIN + "=?", new String[]{login},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String hash = cursor.getString(0);
            cursor.close();
            return hash;
        }
        return null;
    }

    public boolean isUserExists(String login) {
        return getPasswordHash(login) != null;
    }

    // Работа с паролями
    public boolean addPassword(String userLogin, String service, String login, String encryptedPassword, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_LOGIN, userLogin);
        values.put(COLUMN_SERVICE, service);
        values.put(COLUMN_LOGIN_ENTRY, login);
        values.put(COLUMN_PASSWORD, encryptedPassword);
        values.put(COLUMN_NOTES, notes);
        long result = db.insert(TABLE_PASSWORDS, null, values);
        return result != -1;
    }

    public List<PasswordEntry> getAllPasswords(String userLogin) {
        List<PasswordEntry> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PASSWORDS, null,
                COLUMN_USER_LOGIN + "=?", new String[]{userLogin},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ENTRY_ID));
                String service = cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE));
                String login = cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN_ENTRY));
                String encryptedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
                String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                PasswordEntry entry = new PasswordEntry(id, service, login, encryptedPassword, notes);
                list.add(entry);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public PasswordEntry getPasswordEntry(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PASSWORDS, null,
                COLUMN_ENTRY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String service = cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE));
            String login = cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN_ENTRY));
            String encryptedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
            cursor.close();
            return new PasswordEntry(id, service, login, encryptedPassword, notes);
        }
        return null;
    }
}