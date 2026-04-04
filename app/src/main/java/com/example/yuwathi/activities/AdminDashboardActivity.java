package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuwathi.R;
import com.example.yuwathi.services.FirebaseFirestoreService;

import java.util.Map;

public class AdminDashboardActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private TextView tvTotalUsers;
    private TextView tvTotalComplaints;
    private TextView tvPendingComplaints;
    private LinearLayout incidentsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        firestoreService = FirebaseFirestoreService.getInstance();

        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalComplaints = findViewById(R.id.tv_total_complaints);
        tvPendingComplaints = findViewById(R.id.tv_pending_complaints);
        incidentsContainer = findViewById(R.id.dashboard_incidents_container);

        LinearLayout cardUsers = findViewById(R.id.card_manage_users);
        LinearLayout cardComplaints = findViewById(R.id.card_manage_complaints);
        LinearLayout cardReports = findViewById(R.id.card_view_reports);
        LinearLayout cardTips = findViewById(R.id.card_manage_tips);

        cardUsers.setOnClickListener(v -> startActivity(new Intent(this, AdminUsersActivity.class)));
        cardComplaints.setOnClickListener(v -> startActivity(new Intent(this, AdminComplaintsActivity.class)));
        cardReports.setOnClickListener(v -> startActivity(new Intent(this, AdminReportsActivity.class)));
        cardTips.setOnClickListener(v -> startActivity(new Intent(this, AdminSafetyTipsActivity.class)));

        loadStats();
        loadIncidentDetails();
    }

    private void loadStats() {
        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {
            @Override
            public void onSuccess(Map<String, Integer> stats) {
                tvTotalUsers.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
                tvTotalComplaints.setText(String.valueOf(stats.getOrDefault("totalComplaints", 0)));
                tvPendingComplaints.setText(String.valueOf(stats.getOrDefault("pendingComplaints", 0)));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminDashboardActivity.this, "Could not load dashboard stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIncidentDetails() {
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.yuwathi.models.Complaint> complaints) {
                incidentsContainer.removeAllViews();
                int limit = Math.min(complaints.size(), 8);
                for (int i = 0; i < limit; i++) {
                    com.example.yuwathi.models.Complaint c = complaints.get(i);
                    TextView row = new TextView(AdminDashboardActivity.this);
                    row.setPadding(0, 10, 0, 10);
                    row.setText((i + 1) + ") "
                            + (c.getTitle() != null ? c.getTitle() : "Complaint")
                            + "\nStatus: " + (c.getStatus() != null ? c.getStatus() : "Pending")
                            + "\nPriority: " + (c.getPriority() != null ? c.getPriority() : "N/A")
                            + "\nDate: " + (c.getDate() != null ? c.getDate() : "N/A")
                            + "\nLocation: " + (c.getLocation() != null ? c.getLocation() : "N/A")
                            + "\nDescription: " + (c.getDescription() != null ? c.getDescription() : "N/A")
                            + "\nWitnesses: " + (c.getWitnesses() != null ? c.getWitnesses() : "N/A")
                            + "\nVehicle: " + (c.getVehicle() != null ? c.getVehicle() : "N/A")
                            + "\nSuspect: " + (c.getSuspectDescription() != null ? c.getSuspectDescription() : "N/A")
                            + "\nEvidence: " + (c.getEvidence() != null && !c.getEvidence().isEmpty() ? c.getEvidence() : "None")
                            + "\nEvidence files: " + (c.getEvidenceUrls() != null ? c.getEvidenceUrls().size() : 0));
                    incidentsContainer.addView(row);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminDashboardActivity.this, "Could not load incident details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_dashboard;
    }

    @Override
    public void onBackPressed() {
        // Dashboard is admin root screen.
    }
}
