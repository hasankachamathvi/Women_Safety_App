package com.example.yuwathi.activities;

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
 * Admin analytics view for complaint and user reports.
 * Shows summary stats + recent incident reports.
 */
public class AdminReportsActivity extends BaseAdminActivity {

    // Firebase service for database operations
    private FirebaseFirestoreService firestoreService;

    // TextView that displays total report summary
    private TextView tvTotals;

    // Container for complaint report cards
    private LinearLayout reportsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load UI layout for reports screen
        setContentView(R.layout.activity_admin_reports);

        // Initialize Firestore service
        firestoreService = FirebaseFirestoreService.getInstance();

        // Bind UI components
        tvTotals = findViewById(R.id.tv_report_totals);
        reportsContainer = findViewById(R.id.reports_container);

        // Load summary stats + recent reports on screen open
        loadStats();
        loadRecentIncidentReports();
    }

    /**
     * Load dashboard-style report statistics from Firestore
     */
    private void loadStats() {

        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {

            @Override
            public void onSuccess(Map<String, Integer> stats) {

                // Set aggregated report totals (users + complaints overview)
                tvTotals.setText(getString(
                        R.string.admin_report_totals,
                        stats.getOrDefault("totalUsers", 0),
                        stats.getOrDefault("totalComplaints", 0),
                        stats.getOrDefault("resolvedComplaints", 0),
                        stats.getOrDefault("pendingComplaints", 0)
                ));
            }

            @Override
            public void onError(String error) {

                // Show error message if stats cannot be loaded
                Toast.makeText(AdminReportsActivity.this,
                        getString(R.string.admin_could_not_load_report_totals),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load recent complaint reports for quick admin review
     */
    private void loadRecentIncidentReports() {

        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {

            @Override
            public void onSuccess(List<Complaint> complaints) {

                // Clear old report cards before reloading
                reportsContainer.removeAllViews();

                // Limit number of reports shown on screen
                int limit = Math.min(complaints.size(), 12);

                // Layout inflater to convert XML card into View
                android.view.LayoutInflater inflater =
                        android.view.LayoutInflater.from(AdminReportsActivity.this);

                // Loop through complaints
                for (int i = 0; i < limit; i++) {

                    Complaint complaint = complaints.get(i);

                    // Inflate complaint card layout
                    android.view.View card = inflater.inflate(
                            R.layout.item_admin_complaint_card,
                            reportsContainer,
                            false
                    );

                    // Bind UI elements inside card
                    TextView tvTitle = card.findViewById(R.id.tv_complaint_title);
                    TextView tvMeta = card.findViewById(R.id.tv_complaint_meta);
                    TextView tvStatus = card.findViewById(R.id.tv_complaint_status);
                    android.widget.Button btnTakeAction = card.findViewById(R.id.btn_take_action);

                    // Set title with index + fallback name
                    tvTitle.setText(getString(
                            R.string.admin_complaint_title_index,
                            i + 1,
                            complaint.getTitle() != null ? complaint.getTitle() : getString(R.string.complaint)
                    ));

                    // Set full complaint details for investigation view
                    tvMeta.setText(getString(
                            R.string.admin_complaint_details,
                            complaint.getDate() != null ? complaint.getDate() : "N/A",
                            complaint.getLocation() != null ? complaint.getLocation() : "N/A",
                            complaint.getPriority() != null ? complaint.getPriority() : "N/A",
                            complaint.getDescription() != null ? complaint.getDescription() : "N/A",
                            complaint.getWitnesses() != null ? complaint.getWitnesses() : "N/A",
                            complaint.getVehicle() != null ? complaint.getVehicle() : "N/A",
                            complaint.getSuspectDescription() != null ? complaint.getSuspectDescription() : "N/A",
                            complaint.getEvidence() != null && !complaint.getEvidence().isEmpty()
                                    ? complaint.getEvidence()
                                    : "N/A",
                            complaint.getEvidenceUrls() != null ? complaint.getEvidenceUrls().size() : 0
                    ));

                    // Set complaint status (default Pending)
                    tvStatus.setText(getString(
                            R.string.admin_complaint_status,
                            complaint.getStatus() != null ? complaint.getStatus() : "Pending"
                    ));

                    /**
                     * Take Action button
                     * Updates complaint status to "Under Review"
                     */
                    btnTakeAction.setOnClickListener(v ->
                            firestoreService.updateComplaintStatus(
                                    complaint.getId(),
                                    "Under Review",
                                    new FirebaseFirestoreService.OnOperationCallback() {

                                        @Override
                                        public void onSuccess() {

                                            // Refresh list after update
                                            loadRecentIncidentReports();
                                        }

                                        @Override
                                        public void onError(String error) {

                                            // Show error if update fails
                                            Toast.makeText(AdminReportsActivity.this,
                                                    getString(R.string.admin_could_not_update_complaint),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            )
                    );

                    // Add card to reports container
                    reportsContainer.addView(card);
                }
            }

            @Override
            public void onError(String error) {

                // Error loading complaints
                Toast.makeText(AdminReportsActivity.this,
                        getString(R.string.admin_could_not_load_incident_reports),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_reports;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to dashboard
        navigateToDashboard();
    }
}