package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPasswordActivity extends AppCompatActivity {

    private EditText serviceEditText, loginEditText, passwordEditText, notesEditText;
    private boolean passwordVisible = false;
    private String userLogin;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        userLogin = getIntent().getStringExtra("user_login");
        db = new DatabaseHelper(this);

        serviceEditText = findViewById(R.id.editTextService);
        loginEditText = findViewById(R.id.editTextLogin);
        passwordEditText = findViewById(R.id.editTextPassword);
        notesEditText = findViewById(R.id.editTextNotes);
        Button showPasswordButton = findViewById(R.id.buttonShowPassword);
        Button saveButton = findViewById(R.id.buttonSave);

        showPasswordButton.setOnClickListener(v -> togglePasswordVisibility());

        saveButton.setOnClickListener(v -> savePassword());
    }

    private void togglePasswordVisibility() {
        if (!passwordVisible) {
            passwordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisible = true;
        } else {
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisible = false;
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void savePassword() {
        String service = serviceEditText.getText().toString().trim();
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String notes = notesEditText.getText().toString();

        if (TextUtils.isEmpty(service) || TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String encryptedPassword = EncryptionUtil.encrypt(password);

        boolean success = db.addPassword(userLogin, service, login, encryptedPassword, notes);
        if (success) {
            Toast.makeText(this, "Пароль сохранен", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
        }
    }
}