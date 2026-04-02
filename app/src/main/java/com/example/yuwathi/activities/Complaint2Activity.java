package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Complaint2Activity extends AppCompatActivity {

    private FirebaseFirestoreService firestoreService;
    private FirebaseAuthService authService;
    private String category;
    private String incidentTime;
    private boolean ongoing;
    private String location;
    private String vehicle;
    private String suspectDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the complaint form (page 2) layout
        setContentView(R.layout.activity_complaint2);

        firestoreService = FirebaseFirestoreService.getInstance();
        authService = new FirebaseAuthService();

        category = getIntent().getStringExtra("category");
        incidentTime = getIntent().getStringExtra("incident_time");
        ongoing = getIntent().getBooleanExtra("ongoing", false);
        location = getIntent().getStringExtra("location");
        vehicle = getIntent().getStringExtra("vehicle");
        suspectDesc = getIntent().getStringExtra("suspect_desc");

        // Find the contact preference dropdown (spinner) from the layout
        Spinner spinnerContact = findViewById(R.id.spinner_contact_pref);
        EditText etDescription = findViewById(R.id.et_description);
        EditText etWitnesses = findViewById(R.id.et_witnesses);

        // List of contact preference options for the dropdown
        String[] contactOptions = {"In-app notification only", "Phone call", "SMS", "Do not contact"};

        // Set up the spinner adapter with the contact options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactOptions);
        spinnerContact.setAdapter(adapter);

        // Handle Submit button click - submit the complaint
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> submitComplaint(
                etDescription.getText().toString().trim(),
                etWitnesses.getText().toString().trim(),
                spinnerContact.getSelectedItem().toString()
        ));
    }

    private void submitComplaint(String description, String witnesses, String contactPreference) {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter incident description", Toast.LENGTH_SHORT).show();
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setUserId(currentUser.getUid());
        complaint.setTitle(category != null ? category : "Complaint");
        complaint.setLocation(location != null && !location.isEmpty() ? location : "Not specified");
        complaint.setDate(incidentTime != null && !incidentTime.isEmpty() ? incidentTime : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(new Date()));
        complaint.setStatus("Pending");
        complaint.setPriority(ongoing ? "High" : "Medium");
        complaint.setDescription(description);
        complaint.setWitnesses(witnesses);
        complaint.setVehicle(vehicle);
        complaint.setSuspectDescription(suspectDesc);
        complaint.setOngoing(ongoing);
        complaint.setContactPreference(contactPreference);

        firestoreService.submitComplaint(complaint, new FirebaseFirestoreService.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(Complaint2Activity.this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Complaint2Activity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Complaint2Activity.this, "Failed to submit: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
