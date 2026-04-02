package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.example.yuwathi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private TextView tvUserName;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize services
        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();

        // Initialize Views before any async callbacks update UI
        tvUserName = findViewById(R.id.tv_user_name);
        MaterialButton btnSos = findViewById(R.id.btn_sos);
        LinearLayout btnShareLoc = findViewById(R.id.btn_share_location);
        LinearLayout btnContacts = findViewById(R.id.btn_contacts);
        LinearLayout btnReport = findViewById(R.id.btn_report);
        LinearLayout cardTip = findViewById(R.id.card_safety_tip);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Get current user
        FirebaseUser firebaseUser = authService.getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            loadUserProfile();
        } else {
            // Redirect to login if not authenticated
            redirectToLogin();
            return;
        }

        // 1. Set Listener FIRST
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                startActivity(new Intent(this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // 2. Set default selection
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Quick Action Click Listeners
        btnSos.setOnClickListener(v -> startActivity(new Intent(this, SosActivity.class)));
        btnShareLoc.setOnClickListener(v -> startActivity(new Intent(this, LocationActivity.class)));
        btnContacts.setOnClickListener(v -> startActivity(new Intent(this, ContactsActivity.class)));
        btnReport.setOnClickListener(v -> startActivity(new Intent(this, ComplaintActivity.class)));
        cardTip.setOnClickListener(v -> startActivity(new Intent(this, SafetyTipsActivity.class)));

        // Handle logout on back press
        setUpLogoutListener();
    }

    /**
     * Load user profile from Firebase
     */
    private void loadUserProfile() {
        firestoreService.getUser(currentUserId, new FirebaseFirestoreService.OnUserFetchCallback() {
            @Override
            public void onSuccess(com.example.yuwathi.models.User user) {
                if (user != null) {
                    tvUserName.setText(user.getName());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set up logout functionality
     */
    private void setUpLogoutListener() {
        // You can add a logout button in the menu or use onBackPressed
    }

    /**
     * Redirect to login if user is not authenticated
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Show logout confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authService.logout();
                    redirectToLogin();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
