package com.example.yuwathi.activities;

// Import required Android classes
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Complaint2Activity extends AppCompatActivity {

    private FirebaseFirestoreService firestoreService;
    private FirebaseAuthService authService;
    private String category;
    private String incidentTime;
    private boolean ongoing;
    private String location;
    private String vehicle;
    private String suspectDesc;
    private EditText etEvidence;
    private TextView tvEvidenceStatus;
    private final List<Uri> selectedEvidenceUris = new ArrayList<>();
    private final List<String> uploadedEvidenceUrls = new ArrayList<>();
    private boolean isUploadingEvidence = false;
    private ActivityResultLauncher<String[]> evidencePickerLauncher;

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
        etEvidence = findViewById(R.id.et_evidence);
        tvEvidenceStatus = findViewById(R.id.tv_evidence_status);

        // List of contact preference options for the dropdown
        String[] contactOptions = {"In-app notification only", "Phone call", "SMS", "Do not contact"};

        // Set up the spinner adapter with the contact options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactOptions);
        spinnerContact.setAdapter(adapter);

        evidencePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris == null || uris.isEmpty()) {
                        return;
                    }
                    selectedEvidenceUris.clear();
                    selectedEvidenceUris.addAll(uris);
                    uploadSelectedEvidence();
                }
        );

        MaterialButton btnUploadMedia = findViewById(R.id.btn_upload_media);
        btnUploadMedia.setOnClickListener(v -> evidencePickerLauncher.launch(new String[]{"image/*", "audio/*", "video/*"}));

        // Handle Submit button click - submit the complaint
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> submitComplaint(
                etDescription.getText().toString().trim(),
                etWitnesses.getText().toString().trim(),
                etEvidence.getText().toString().trim(),
                spinnerContact.getSelectedItem().toString()
        ));
    }

    private void uploadSelectedEvidence() {
        if (selectedEvidenceUris.isEmpty()) {
            tvEvidenceStatus.setText("No evidence selected");
            return;
        }

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        isUploadingEvidence = true;
        uploadedEvidenceUrls.clear();
        tvEvidenceStatus.setText(String.format(Locale.getDefault(), "Uploading %d file(s)...", selectedEvidenceUris.size()));

        AtomicInteger completed = new AtomicInteger(0);
        for (Uri uri : selectedEvidenceUris) {
            String ext = getFileExtension(uri);
            String fileName = System.currentTimeMillis() + "_" + completed.get() + (ext.isEmpty() ? "" : "." + ext);
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference()
                    .child("complaint_evidence")
                    .child(currentUser.getUid())
                    .child(fileName);

            ref.putFile(uri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException() != null ? task.getException() : new IllegalStateException("Upload failed");
                        }
                        return ref.getDownloadUrl();
                    })
                    .addOnSuccessListener(downloadUri -> {
                        uploadedEvidenceUrls.add(downloadUri.toString());
                        int count = completed.incrementAndGet();
                        tvEvidenceStatus.setText(String.format(Locale.getDefault(), "%d/%d evidence files uploaded", count, selectedEvidenceUris.size()));
                        if (count == selectedEvidenceUris.size()) {
                            isUploadingEvidence = false;
                            tvEvidenceStatus.setText(String.format(Locale.getDefault(), "%d evidence file(s) ready", uploadedEvidenceUrls.size()));
                        }
                    })
                    .addOnFailureListener(e -> {
                        isUploadingEvidence = false;
                        Toast.makeText(this, "Evidence upload failed", Toast.LENGTH_SHORT).show();
                        tvEvidenceStatus.setText("Upload failed. Please try again.");
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = resolver.getType(uri);
        return type == null ? "" : (mime.getExtensionFromMimeType(type) == null ? "" : mime.getExtensionFromMimeType(type));
    }

    private void submitComplaint(String description, String witnesses, String evidence, String contactPreference) {
        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUploadingEvidence) {
            Toast.makeText(this, "Please wait for evidence upload to finish", Toast.LENGTH_SHORT).show();
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
        complaint.setEvidence(evidence);
        complaint.setEvidenceUrls(new ArrayList<>(uploadedEvidenceUrls));

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
                Toast.makeText(Complaint2Activity.this, "Could not submit complaint. Check connection/Firebase rules.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
