package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText loginEditText, passwordEditText;
    private TextView messageTextView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        loginEditText = findViewById(R.id.editTextLogin);
        passwordEditText = findViewById(R.id.editTextPassword);
        messageTextView = findViewById(R.id.textViewMessage);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonOpenRegister);

        loginButton.setOnClickListener(v -> authenticate());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void authenticate() {
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
            messageTextView.setText("Введите логин и пароль");
            return;
        }

        String storedPasswordHash = db.getPasswordHash(login);
        if (storedPasswordHash == null) {
            messageTextView.setText("Неверный логин или пароль");
            return;
        }

        if (PasswordUtils.verifyPassword(password, storedPasswordHash)) {
            // Успешный вход
            Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
            intent.putExtra("user_login", login);
            startActivity(intent);
            finish();
        } else {
            messageTextView.setText("Неверный логин или пароль");
        }
    }
}