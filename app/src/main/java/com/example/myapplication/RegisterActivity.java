package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText loginEditText, passwordEditText, confirmPasswordEditText;
    private TextView messageTextView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        loginEditText = findViewById(R.id.editTextRegisterLogin);
        passwordEditText = findViewById(R.id.editTextRegisterPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        messageTextView = findViewById(R.id.textViewRegisterMessage);
        Button registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(v -> register());
    }

    private void register() {
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            messageTextView.setText("Заполните все поля");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageTextView.setText("Пароли не совпадают");
            return;
        }

        if (db.isUserExists(login)) {
            messageTextView.setText("Логин уже занят");
            return;
        }

        String passwordHash = PasswordUtils.hashPassword(password);
        boolean success = db.addUser(login, passwordHash);
        if (success) {
            messageTextView.setText("Успешная регистрация");
            // Автоматический переход к авторизации через 1 сек
            loginEditText.postDelayed(() -> {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        } else {
            messageTextView.setText("Ошибка при регистрации");
        }
    }
}