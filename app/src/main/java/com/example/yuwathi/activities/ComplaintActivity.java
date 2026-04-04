package com.example.yuwathi.activities;

// Import required Android classes
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Collects first-step complaint details from users.
 */
public class ComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the complaint form (page 1) layout
        setContentView(R.layout.activity_complaint);

        // Find the category dropdown (spinner) from the layout
        Spinner spinnerCategory = findViewById(R.id.spinner_category); //Dropdown
        EditText etIncidentTime = findViewById(R.id.et_incident_time); //Date & time night
        SwitchCompat switchOngoing = findViewById(R.id.switch_ongoing);
        EditText etLocation = findViewById(R.id.et_location);
        EditText etVehicle = findViewById(R.id.et_vehicle);
        EditText etSuspectDesc = findViewById(R.id.et_suspect_desc);

        etIncidentTime.setOnClickListener(v -> showDateTimePicker(etIncidentTime));
        etIncidentTime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDateTimePicker(etIncidentTime);
            }
        });

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
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(ComplaintActivity.this, Complaint2Activity.class);
            intent.putExtra("category", spinnerCategory.getSelectedItem().toString());
            intent.putExtra("incident_time", etIncidentTime.getText().toString().trim());
            intent.putExtra("ongoing", switchOngoing.isChecked());
            intent.putExtra("location", etLocation.getText().toString().trim());
            intent.putExtra("vehicle", etVehicle.getText().toString().trim());
            intent.putExtra("suspect_desc", etSuspectDesc.getText().toString().trim());
            startActivity(intent);
        });
    }

    private void showDateTimePicker(EditText targetField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(Calendar.YEAR, year);
                    selected.set(Calendar.MONTH, month);
                    selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selected.set(Calendar.MINUTE, minute);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                targetField.setText(formatter.format(selected.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
