package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordDetailActivity extends AppCompatActivity {

    private TextView serviceTextView, loginTextView, passwordTextView, notesTextView;
    private DatabaseHelper db;
    private long entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_detail);

        serviceTextView = findViewById(R.id.textViewService);
        loginTextView = findViewById(R.id.textViewLogin);
        passwordTextView = findViewById(R.id.textViewPassword);
        notesTextView = findViewById(R.id.textViewNotes);

        db = new DatabaseHelper(this);
        entryId = getIntent().getLongExtra("entry_id", -1);

        loadEntry();
    }

    private void loadEntry() {
        PasswordEntry entry = db.getPasswordEntry(entryId);
        if (entry != null) {
            serviceTextView.setText(entry.getServiceName());
            loginTextView.setText(entry.getLogin());
            String decryptedPassword = EncryptionUtil.decrypt(entry.getEncryptedPassword());
            passwordTextView.setText(decryptedPassword);
            notesTextView.setText(entry.getNotes());
        }
    }
}