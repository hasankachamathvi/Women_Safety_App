package com.example.yuwathi.activities;

// Import required Android classes
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
        // Set the complaint form (page 2) layout
        setContentView(R.layout.activity_complaint2);

        // Find the contact preference dropdown (spinner) from the layout
        Spinner spinnerContact = findViewById(R.id.spinner_contact_pref);

        // List of contact preference options for the dropdown
        String[] contactOptions = {"In-app notification only", "Phone call", "SMS", "Do not contact"};

        // Set up the spinner adapter with the contact options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactOptions);
        spinnerContact.setAdapter(adapter);

        // Handle Submit button click - submit the complaint
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            // Show success message
            Toast.makeText(Complaint2Activity.this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();

            // Go back to Home page and clear the complaint pages from back stack
            Intent intent = new Intent(Complaint2Activity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish(); // Close this page
        });
    }
}
