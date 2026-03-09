package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class ComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the complaint form (page 1) layout
        setContentView(R.layout.activity_complaint);

        // Find the category dropdown (spinner) from the layout
        Spinner spinnerCategory = findViewById(R.id.spinner_category);

        // List of incident categories for the dropdown
        String[] categories = {
                "Harassment", "Stalking", "Physical Assault",
                "Suspicious Following", "Cyber-bullying", "Other"
        };

        // Custom adapter to style the spinner text colors (white when closed, black when open)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);  // White text when spinner is closed
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);  // Black text in dropdown list
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter); // Set the adapter to the spinner

        // Handle Next button click - go to Complaint page 2
        MaterialButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v ->
                startActivity(new Intent(ComplaintActivity.this, Complaint2Activity.class))
        );
    }
}
