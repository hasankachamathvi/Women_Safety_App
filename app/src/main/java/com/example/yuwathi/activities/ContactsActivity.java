package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private String currentUserId;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the contacts screen layout
        setContentView(R.layout.activity_contacts);

        firestoreService = FirebaseFirestoreService.getInstance();
        FirebaseAuthService authService = new FirebaseAuthService();
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

        List<Map<String, Object>> contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList,
                this::showContactDialog,
                contact -> {
                    String contactId = getContactId(contact);
                    if (contactId == null) {
                        Toast.makeText(ContactsActivity.this, "Missing contact ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
        btnAddContact.setOnClickListener(v -> showContactDialog(null));

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

    private void showContactDialog(Map<String, Object> existingContact) {
        boolean isEdit = existingContact != null;
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etRelationship = dialogView.findViewById(R.id.et_relationship);
        android.widget.TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);

        tvDialogTitle.setText(isEdit ? R.string.contact_edit_title : R.string.contact_add_title);
        if (isEdit) {
            etName.setText(stringValue(existingContact.get("name")));
            etPhone.setText(stringValue(existingContact.get("phone")));
            etRelationship.setText(stringValue(existingContact.get("relationship")));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
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

                FirebaseFirestoreService.OnOperationCallback callback = new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ContactsActivity.this, isEdit ? "Contact updated" : "Contact added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadContacts();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ContactsActivity.this, (isEdit ? "Could not update contact: " : "Could not add contact: ") + error, Toast.LENGTH_SHORT).show();
                    }
                };

                if (isEdit) {
                    String contactId = getContactId(existingContact);
                    if (contactId == null) {
                        Toast.makeText(this, "Missing contact ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    firestoreService.updateEmergencyContact(currentUserId, contactId, contact, callback);
                } else {
                    firestoreService.addEmergencyContact(currentUserId, contact, callback);
                }
            });
        });

        dialog.show();
    }

    private String getContactId(Map<String, Object> contact) {
        if (contact == null) {
            return null;
        }
        Object idValue = contact.get("id");
        if (idValue == null) {
            return null;
        }
        String contactId = String.valueOf(idValue).trim();
        if (contactId.isEmpty() || "null".equalsIgnoreCase(contactId)) {
            return null;
        }
        return contactId;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
