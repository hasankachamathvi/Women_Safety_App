package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the profile screen layout
        setContentView(R.layout.activity_profile);

        // Find all setting buttons from the layout
        LinearLayout btnEditProfile = findViewById(R.id.btn_edit_profile);      // Edit Profile option
        LinearLayout btnContacts = findViewById(R.id.btn_emergency_contacts);   // Emergency Contacts option
        LinearLayout btnTips = findViewById(R.id.btn_tips);                     // Safety Tips option
        LinearLayout btnLogout = findViewById(R.id.btn_logout);                 // Logout option
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);         // Bottom navigation bar

        // Set Profile as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Handle Edit Profile click
        btnEditProfile.setOnClickListener(v -> {
        });

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
}
