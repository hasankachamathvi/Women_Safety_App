package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AlertDialog;
import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import com.example.yuwathi.services.FirebaseAuthService;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.example.yuwathi.services.FirebaseRealtimeDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

/**
 * Admin Dashboard Activity
 * Main admin panel showing overview statistics and quick navigation
 */
public class AdminDashboardActivity extends BaseAdminActivity {

    // UI Components
    private TextView tvTotalUsers, tvTotalComplaints, tvResolvedComplaints, tvTotalReports;
    private CardView cardUsers, cardComplaints, cardReports, cardSafetyTips;
    private MaterialButton btnLogout;

    // Services
    private FirebaseAuthService authService;
    private FirebaseFirestoreService firestoreService;
    private ValueEventListener sosListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize services
        authService = new FirebaseAuthService();
        firestoreService = FirebaseFirestoreService.getInstance();

        initializeViews();
        setupListeners();
        loadStatistics();
        listenToLiveSOSAlerts(); // Add real-time SOS listener
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_dashboard;
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        // Statistics TextViews
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalComplaints = findViewById(R.id.tv_total_complaints);
        tvResolvedComplaints = findViewById(R.id.tv_resolved_complaints);
        tvTotalReports = findViewById(R.id.tv_total_reports);

        // Navigation Cards
        cardUsers = findViewById(R.id.card_users);
        cardComplaints = findViewById(R.id.card_complaints);
        cardReports = findViewById(R.id.card_reports);
        cardSafetyTips = findViewById(R.id.card_safety_tips);

        // Logout Button
        btnLogout = findViewById(R.id.btn_logout);
    }

    /**
     * Setup click listeners for navigation
     */
    private void setupListeners() {
        // Navigate to Users Management
        cardUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });

        // Navigate to Complaints Management
        cardComplaints.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminComplaintsActivity.class));
        });

        // Navigate to Reports
        cardReports.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReportsActivity.class));
        });

        // Navigate to Safety Tips Management
        cardSafetyTips.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSafetyTipsActivity.class));
        });

        // Logout
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    /**
     * Load statistics from Firebase Firestore
     */
    private void loadStatistics() {
        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Integer> stats) {
                tvTotalUsers.setText(String.valueOf(stats.get("totalUsers")));
                tvTotalComplaints.setText(String.valueOf(stats.get("totalComplaints")));
                tvResolvedComplaints.setText(String.valueOf(stats.get("resolvedComplaints")));
                tvTotalReports.setText(String.valueOf(stats.get("pendingComplaints")));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminDashboardActivity.this,
                    "Failed to load statistics: " + error, Toast.LENGTH_SHORT).show();
                // Set default values on error
                tvTotalUsers.setText("0");
                tvTotalComplaints.setText("0");
                tvResolvedComplaints.setText("0");
                tvTotalReports.setText("0");
            }
        });
    }

    private void listenToLiveSOSAlerts() {
        FirebaseRealtimeDatabaseService realtimeService = FirebaseRealtimeDatabaseService.getInstance();
        sosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This fires whenever SOS data changes in Realtime DB
                if (snapshot.exists()) {
                    // You have live SOS data here
                    // Could update a list or UI badge
                    Toast.makeText(AdminDashboardActivity.this, "Live SOS received", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };
        realtimeService.listenToSOSAlerts(sosListener);
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authService.logout();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening to prevent memory leaks
        if (sosListener != null) {
            FirebaseRealtimeDatabaseService.getInstance().removeListener("sos_alerts", sosListener);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable back button on admin dashboard
        // Admin must logout explicitly
    }
}

