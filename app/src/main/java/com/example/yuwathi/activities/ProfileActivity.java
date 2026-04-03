package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.example.yuwathi.models.User;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private String currentUserId;
    private TextView tvProfileName;
    private TextView tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the profile screen layout
        setContentView(R.layout.activity_profile);

        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();
        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid();

        // Find all setting buttons from the layout
        LinearLayout btnEditProfile = findViewById(R.id.btn_edit_profile);      // Edit Profile option
        LinearLayout btnContacts = findViewById(R.id.btn_emergency_contacts);   // Emergency Contacts option
        LinearLayout btnTips = findViewById(R.id.btn_tips);                     // Safety Tips option
        LinearLayout btnLogout = findViewById(R.id.btn_logout);                 // Logout option
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);         // Bottom navigation bar
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);

        loadProfile();

        // Set Profile as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Handle Edit Profile click
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Handle Emergency Contacts click - go to Contacts page
        btnContacts.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ContactsActivity.class))
        );

        // Handle Safety Tips click - go to Tips page
        btnTips.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SafetyTipsActivity.class))
        );

        // Handle Logout click - go back to Login page and clear all history
        btnLogout.setOnClickListener(v -> {
            com.example.yuwathi.utils.AdminSessionManager.clearSession(ProfileActivity.this);
            authService.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close profile page
        });

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(ProfileActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(ProfileActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(ProfileActivity.this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;  // Already on Profile, do nothing
            }
            return false;
        });
    }

    private void loadProfile() {
        firestoreService.getUser(currentUserId, new FirebaseFirestoreService.OnUserFetchCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    tvProfileName.setText(user.getName());
                    tvProfileEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, "Failed to load profile: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditProfileDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Full Name");
        etName.setText(tvProfileName.getText().toString());
        layout.addView(etName);

        EditText etPhone = new EditText(this);
        etPhone.setHint("Phone Number");
        layout.addView(etPhone);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", etName.getText().toString().trim());
                    updates.put("phone", etPhone.getText().toString().trim());
                    firestoreService.updateUser(currentUserId, updates, new FirebaseFirestoreService.OnOperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            loadProfile();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(ProfileActivity.this, "Update failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
