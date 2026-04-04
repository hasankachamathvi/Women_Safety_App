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
import com.example.yuwathi.utils.AdminSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Common admin base activity enforcing access and shared navigation.
 */
public abstract class BaseAdminActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNav;
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;

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
        setupAdminBackButton();
        setupAdminLogoutButton();
    }

    private void enforceAdminAccess() {
        // Fast path: trusted local admin session already exists.
        if (AdminSessionManager.isAdminLoggedIn(this)) {
            return;
        }

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin("Please login as admin");
            return;
        }
        // Verify role from Firestore before allowing entry to admin screens.
        firestoreService.isUserAdmin(currentUser.getUid(), isAdmin -> {
            if (!isAdmin) {
                redirectToLogin("Admin access required");
            }
        });
    }

    private void redirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        AdminSessionManager.clearSession(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected void setupBottomNavigation() {
        bottomNav = findViewById(R.id.admin_bottom_nav);
        if (bottomNav == null) {
            return;
        }

        // Keep selected tab in sync with the current admin screen.
        bottomNav.setSelectedItemId(getNavigationMenuItemId());
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_dashboard) {
                navigateTo(AdminDashboardActivity.class);
                return true;
            }
            if (itemId == R.id.nav_admin_users) {
                navigateTo(AdminUsersActivity.class);
                return true;
            }
            if (itemId == R.id.nav_admin_complaints) {
                navigateTo(AdminComplaintsActivity.class);
                return true;
            }
            if (itemId == R.id.nav_admin_reports) {
                navigateTo(AdminReportsActivity.class);
                return true;
            }
            if (itemId == R.id.nav_admin_safety_tips) {
                navigateTo(AdminSafetyTipsActivity.class);
                return true;
            }
            return false;
        });
    }

    protected void setupAdminBackButton() {
        View back = findViewById(R.id.btn_admin_back);
        if (back != null) {
            back.setOnClickListener(v -> navigateToDashboard());
        }
    }

    protected void setupAdminLogoutButton() {
        View logout = findViewById(R.id.card_admin_logout);
        if (logout != null) {
            logout.setOnClickListener(v -> {
                AdminSessionManager.clearSession(this);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    protected void navigateToDashboard() {
        navigateTo(AdminDashboardActivity.class);
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }

    private void navigateTo(Class<?> target) {
        if (!getClass().equals(target)) {
            startActivity(new Intent(this, target));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    protected abstract int getNavigationMenuItemId();
}
