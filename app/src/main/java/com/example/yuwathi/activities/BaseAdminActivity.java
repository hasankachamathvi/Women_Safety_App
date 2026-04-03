package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

/**
 * Base Admin Activity
 * Provides common bottom navigation functionality for all admin activities
 */
public abstract class BaseAdminActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNav;
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private boolean adminVerified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enforceAdminAccess();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupBottomNavigation();
    }

    private void enforceAdminAccess() {
        if (adminVerified) {
            return;
        }

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin("Please login as admin");
            return;
        }

        firestoreService.isUserAdmin(currentUser.getUid(), isAdmin -> {
            if (!isAdmin) {
                redirectToLogin("Admin access required");
            } else {
                adminVerified = true;
            }
        });
    }

    private void redirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Setup Bottom Navigation
     * Must be called after setContentView
     */
    protected void setupBottomNavigation() {
        bottomNav = findViewById(R.id.admin_bottom_nav);

        if (bottomNav != null) {
            // Set the current item as selected
            selectCurrentMenuItem();

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_admin_dashboard) {
                    navigateToActivity(AdminDashboardActivity.class);
                    return true;
                } else if (itemId == R.id.nav_admin_users) {
                    navigateToActivity(AdminUsersActivity.class);
                    return true;
                } else if (itemId == R.id.nav_admin_complaints) {
                    navigateToActivity(AdminComplaintsActivity.class);
                    return true;
                } else if (itemId == R.id.nav_admin_reports) {
                    navigateToActivity(AdminReportsActivity.class);
                    return true;
                } else if (itemId == R.id.nav_admin_safety_tips) {
                    navigateToActivity(AdminSafetyTipsActivity.class);
                    return true;
                }

                return false;
            });
        }
    }

    /**
     * Wire a custom back button in the layout to return to the admin dashboard.
     * NoActionBar theme means we need an explicit button in the screen layout.
     */
    protected void setupAdminBackButton() {
        int backButtonId = getResources().getIdentifier("btn_admin_back", "id", getPackageName());
        if (backButtonId != 0) {
            View backButton = findViewById(backButtonId);
            if (backButton != null) {
                backButton.setOnClickListener(v -> navigateToDashboard());
            }
        }
    }

    /**
     * Return to the admin dashboard.
     */
    protected void navigateToDashboard() {
        if (!this.getClass().equals(AdminDashboardActivity.class)) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    /**
     * Navigate to specified activity
     */
    private void navigateToActivity(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            startActivity(new Intent(this, activityClass));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    /**
     * Get the menu item ID for the current activity
     * Override in child activities
     */
    protected abstract int getNavigationMenuItemId();

    /**
     * Select the appropriate menu item for the current activity
     */
    private void selectCurrentMenuItem() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(getNavigationMenuItemId());
        }
    }
}
