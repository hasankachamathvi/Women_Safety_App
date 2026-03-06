package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class Complaint2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint2);

        Spinner spinnerContact = findViewById(R.id.spinner_contact_pref);
        String[] contactOptions = {"In-app notification only", "Phone call", "SMS", "Do not contact"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactOptions);
        spinnerContact.setAdapter(adapter);

        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(Complaint2Activity.this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Complaint2Activity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
