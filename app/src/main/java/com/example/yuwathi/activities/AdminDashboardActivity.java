package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

/**
 * Admin Dashboard Activity
 * Main admin panel showing overview statistics and quick navigation
 */
public class AdminDashboardActivity extends BaseAdminActivity {

    // UI Components
    private TextView tvTotalUsers, tvTotalComplaints, tvResolvedComplaints, tvTotalReports;
    private CardView cardUsers, cardComplaints, cardReports, cardSafetyTips;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();
        setupListeners();
        loadStatistics();
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
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Load statistics from backend API
     * TODO: Implement actual API call
     */
    private void loadStatistics() {
        // Mock data for now - replace with actual API call
        tvTotalUsers.setText("142");
        tvTotalComplaints.setText("28");
        tvResolvedComplaints.setText("21");
        tvTotalReports.setText("35");
    }

    @Override
    public void onBackPressed() {
        // Disable back button on admin dashboard
        // Admin must logout explicitly
    }
}
