package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseFirestoreService;

import java.util.List;
import java.util.Map;

/**
 * Admin landing screen with key system metrics.
 */
public class AdminDashboardActivity extends BaseAdminActivity {

    // Keep the home screen concise by showing only a short incident preview.
    private static final int INCIDENT_PREVIEW_LIMIT = 8;
    // Maximum number of incidents to show on dashboard preview
    private FirebaseFirestoreService firestoreService;
    // Firebase service instance for database operations
    private TextView tvTotalUsers;
    private TextView tvTotalComplaints;
    private TextView tvPendingComplaints;
    private LinearLayout incidentsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load XML layout for dashboard screen
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Firestore service (Singleton pattern)
        firestoreService = FirebaseFirestoreService.getInstance();
        // Bind UI components with XML views
        bindViews();
        // Setup navigation cards (quick actions)
        setupQuickActionCards();

        // Load recent incident/complaint preview
        loadStats();
        loadIncidentDetails();
    }

    /**
     * Connect Java variables with XML UI components
     */
    private void bindViews() {
        // Dashboard counters + incident preview container.
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalComplaints = findViewById(R.id.tv_total_complaints);
        tvPendingComplaints = findViewById(R.id.tv_pending_complaints);
        incidentsContainer = findViewById(R.id.dashboard_incidents_container);
    }

    /**
     * Setup click listeners for dashboard quick action cards
     * Each card opens a different admin management screen
     */
    private void setupQuickActionCards() {
        // Each card routes admin to the matching management page.
        LinearLayout cardUsers = findViewById(R.id.card_manage_users);
        LinearLayout cardComplaints = findViewById(R.id.card_manage_complaints);
        LinearLayout cardReports = findViewById(R.id.card_view_reports);
        LinearLayout cardTips = findViewById(R.id.card_manage_tips);

        // Navigate to  screen
        cardUsers.setOnClickListener(v -> openScreen(AdminUsersActivity.class));
        cardComplaints.setOnClickListener(v -> openScreen(AdminComplaintsActivity.class));
        cardReports.setOnClickListener(v -> openScreen(AdminReportsActivity.class));
        cardTips.setOnClickListener(v -> openScreen(AdminSafetyTipsActivity.class));
    }

    /**
     * Helper method to open any activity screen
     */
    private void openScreen(Class<?> screen) {
        // Single helper keeps card click handlers short and consistent.
        startActivity(new Intent(this, screen));
    }

    /**
     * Load dashboard statistics from Firestore
     * total users, total complaints, pending complaints
     */
    private void loadStats() {
        // Pull dashboard totals from Firestore and map them into the counter cards.
        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {
            @Override
            public void onSuccess(Map<String, Integer> stats) {
                tvTotalUsers.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
                tvTotalComplaints.setText(String.valueOf(stats.getOrDefault("totalComplaints", 0)));
                tvPendingComplaints.setText(String.valueOf(stats.getOrDefault("pendingComplaints", 0)));
            }

            // Show error message if stats fail to load
            @Override
            public void onError(String error) {
                Toast.makeText(AdminDashboardActivity.this, "Could not load dashboard stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load recent complaints/incident data from Firestore
     */
    private void loadIncidentDetails() {
        // Show a compact snapshot of recent complaints so the dashboard stays informative.
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {

                // Render only preview list (limited items)
                renderIncidentPreview(complaints);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminDashboardActivity.this, "Could not load incident details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Display limited number of complaints on dashboard
     */
    private void renderIncidentPreview(List<Complaint> complaints) {
        // Clear old views before adding new ones
        incidentsContainer.removeAllViews();
        // Limit number of displayed incidents
        int limit = Math.min(complaints.size(), INCIDENT_PREVIEW_LIMIT);

        for (int i = 0; i < limit; i++) {
            Complaint complaint = complaints.get(i);
            // Create a simple TextView for each complaint
            TextView row = new TextView(this);
            // Add spacing between items
            row.setPadding(0, 10, 0, 10);
            // Set formatted complaint text
            row.setText(buildIncidentText(i, complaint));
            // Add to container layout
            incidentsContainer.addView(row);
        }
    }

    /**
     * Format complaint data into readable text
     */
    private String buildIncidentText(int index, Complaint complaint) {
        // Title line gives operators quick index + complaint identity.
        String titleLine = getString(
                R.string.admin_complaint_title_index,
                index + 1,
                complaint.getTitle() != null ? complaint.getTitle() : getString(R.string.complaint));

        // Detail block mirrors the complaint card data used in reports/management pages.
        String detailBlock = getString(
                R.string.admin_complaint_details,
                fallback(complaint.getDate(), "N/A"),
                fallback(complaint.getLocation(), "N/A"),
                fallback(complaint.getPriority(), "N/A"),
                fallback(complaint.getDescription(), "N/A"),
                fallback(complaint.getWitnesses(), "N/A"),
                fallback(complaint.getVehicle(), "N/A"),
                fallback(complaint.getSuspectDescription(), "N/A"),
                fallback(complaint.getEvidence(), "None"),
                complaint.getEvidenceUrls() != null ? complaint.getEvidenceUrls().size() : 0);

        return titleLine + "\n" + detailBlock;
    }

    /**
     * Handles null or empty values safely
     */
    private String fallback(String value, String defaultValue) {
        // Firestore fields may be null/blank; normalize once before formatting text.
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    /**
     * Highlight current menu item in navigation drawer
     */
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_dashboard;
    }

    /**
     * Disable back button so dashboard remains root screen
     */
    @Override
    public void onBackPressed() {
        // Dashboard stays as the root admin page.
    }
}
