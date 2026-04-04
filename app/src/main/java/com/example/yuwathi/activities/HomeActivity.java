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
    // Authentication helper for current-user and logout actions.
    private FirebaseAuthService authService;
    // Firestore helper for reading user profile data.
    private FirebaseFirestoreService firestoreService;
    // TextView that shows the logged-in user's name.
    private TextView tvUserName;
    // Firebase UID of the currently logged-in user.
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity and load home screen layout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create service instances used by this screen.
        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();

        // Check whether a user is already logged in.
        FirebaseUser firebaseUser = authService.getCurrentUser();
        if (firebaseUser != null) {
            // Save user id and load profile details from Firestore.
            currentUserId = firebaseUser.getUid();
            loadUserProfile();
        } else {
            // If no user is logged in, return to login screen.
            redirectToLogin();
        }

        // Connect XML views to Java variables.
        tvUserName = findViewById(R.id.tv_user_name);
        MaterialButton btnSos = findViewById(R.id.btn_sos);
        LinearLayout btnShareLoc = findViewById(R.id.btn_share_location);
        LinearLayout btnContacts = findViewById(R.id.btn_contacts);
        LinearLayout btnReport = findViewById(R.id.btn_report);
        LinearLayout cardTip = findViewById(R.id.card_safety_tip);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Handle bottom navigation tab clicks.
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on home screen.
                return true;
            } else if (id == R.id.nav_contacts) {
                // Open contacts screen.
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                // Open SOS screen.
                startActivity(new Intent(this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                // Open safety tips screen.
                startActivity(new Intent(this, SafetyTipsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                // Open profile screen.
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            // Unknown menu item.
            return false;
        });

        // Mark Home as selected tab.
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Home quick-action buttons.
        btnSos.setOnClickListener(v -> startActivity(new Intent(this, SosActivity.class)));
        btnShareLoc.setOnClickListener(v -> startActivity(new Intent(this, LocationActivity.class)));
        btnContacts.setOnClickListener(v -> startActivity(new Intent(this, ContactsActivity.class)));
        btnReport.setOnClickListener(v -> startActivity(new Intent(this, ComplaintActivity.class)));
        cardTip.setOnClickListener(v -> startActivity(new Intent(this, SafetyTipsActivity.class)));

        // Keep logout behavior organized in one place.
        setUpLogoutListener();
    }

    /**
     * Load logged-in user profile from Firestore.
     */
    private void loadUserProfile() {
        firestoreService.getUser(currentUserId, new FirebaseFirestoreService.OnUserFetchCallback() {
            @Override
            public void onSuccess(com.example.yuwathi.models.User user) {
                if (user != null) {
                    // Show user's name on the home screen.
                    tvUserName.setText(user.getName());
                }
            }

            @Override
            public void onError(String error) {
                // Inform user when profile data cannot be loaded.
                Toast.makeText(HomeActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Placeholder for dedicated logout button behavior.
     */
    private void setUpLogoutListener() {
        // Logout is currently handled in onBackPressed().
    }

    /**
     * Navigate to login and clear old screens from back stack.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Ask user to confirm logout when back is pressed.
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Sign out and return to login.
                    authService.logout();
                    redirectToLogin();
                })
                // Stay on current screen if user cancels.
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
