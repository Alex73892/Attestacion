package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PasswordListActivity extends AppCompatActivity {

    private ListView listView;
    private PasswordAdapter adapter;
    private List<PasswordEntry> passwordList;
    private DatabaseHelper db;
    private String userLogin;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        userLogin = getIntent().getStringExtra("user_login");
        db = new DatabaseHelper(this);

        listView = findViewById(R.id.listViewPasswords);
        Button addButton = findViewById(R.id.buttonAddPassword);
        EditText searchEditText = findViewById(R.id.searchEditText);
        emptyTextView = findViewById(R.id.textViewEmpty);

        passwordList = db.getAllPasswords(userLogin);
        adapter = new PasswordAdapter(this, passwordList);
        listView.setAdapter(adapter);

        if (passwordList.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordListActivity.this, AddPasswordActivity.class);
            intent.putExtra("user_login", userLogin);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            PasswordEntry entry = adapter.getItem(position);
            Intent intent = new Intent(PasswordListActivity.this, PasswordDetailActivity.class);
            intent.putExtra("entry_id", entry.getId());
            startActivity(intent);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after) { }

            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count) {
                filterPasswords(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterPasswords(String query) {
        List<PasswordEntry> filtered = new ArrayList<>();
        for (PasswordEntry entry : passwordList) {
            if (entry.getServiceName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(entry);
            }
        }
        adapter.updateList(filtered);
        emptyTextView.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновление списка при возвращении
        passwordList = db.getAllPasswords(userLogin);
        adapter.updateList(passwordList);
        emptyTextView.setVisibility(passwordList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}