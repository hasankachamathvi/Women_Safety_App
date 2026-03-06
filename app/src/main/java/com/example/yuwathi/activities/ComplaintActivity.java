package com.example.yuwathi.activities;

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
        setContentView(R.layout.activity_complaint);

        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        String[] categories = {
                "Harassment", "Stalking", "Physical Assault",
                "Suspicious Following", "Cyber-bullying", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        MaterialButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v ->
                startActivity(new Intent(ComplaintActivity.this, Complaint2Activity.class))
        );
    }
}
