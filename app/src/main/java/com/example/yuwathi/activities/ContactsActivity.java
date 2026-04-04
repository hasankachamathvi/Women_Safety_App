package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.R;
import com.example.yuwathi.adapters.ContactAdapter;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages emergency contacts for the current user.
 */
public class ContactsActivity extends AppCompatActivity {

    private FirebaseFirestoreService firestoreService;
    private FirebaseAuthService authService;
    private String currentUserId;
    private ContactAdapter contactAdapter;
    private List<Map<String, Object>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the contacts screen layout
        setContentView(R.layout.activity_contacts);

        firestoreService = FirebaseFirestoreService.getInstance();
        authService = new FirebaseAuthService();
        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid();

        // Find views from the layout
        RecyclerView rvContacts = findViewById(R.id.rv_contacts);       // List of emergency contacts
        MaterialButton btnAddContact = findViewById(R.id.btn_add_contact); // Add new contact button
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);    // Bottom navigation bar
        android.widget.ImageView btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> onBackPressed());

        // Set Contacts as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_contacts);

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList, contact -> {
            String contactId = String.valueOf(contact.get("id"));
            firestoreService.deleteEmergencyContact(currentUserId, contactId, new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ContactsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                    loadContacts();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ContactsActivity.this, "Delete failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(contactAdapter);

        loadContacts();

        // Handle Add Contact button click
        btnAddContact.setOnClickListener(v -> showAddContactDialog());

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ContactsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                return true;  // Already on Contacts, do nothing
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(ContactsActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(ContactsActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ContactsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadContacts() {
        firestoreService.getEmergencyContacts(currentUserId, new FirebaseFirestoreService.OnContactsListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> contacts) {
                contactAdapter.setContacts(contacts);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ContactsActivity.this, "Failed to load contacts: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddContactDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Contact Name");
        layout.addView(etName);

        EditText etPhone = new EditText(this);
        etPhone.setHint("Phone Number");
        layout.addView(etPhone);

        EditText etRelationship = new EditText(this);
        etRelationship.setHint("Relationship");
        layout.addView(etRelationship);

        new AlertDialog.Builder(this)
                .setTitle("Add Emergency Contact")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String relationship = etRelationship.getText().toString().trim();

                    if (name.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, "Please enter contact name and phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> contact = new HashMap<>();
                    contact.put("name", name);
                    contact.put("phone", phone);
                    contact.put("relationship", relationship);
                    firestoreService.addEmergencyContact(currentUserId, contact, new FirebaseFirestoreService.OnOperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ContactsActivity.this, "Contact added", Toast.LENGTH_SHORT).show();
                            loadContacts();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(ContactsActivity.this, "Could not add contact. Check connection/Firebase rules.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
