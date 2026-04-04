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
 */
public class AdminReportsActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private TextView tvTotals;
    private LinearLayout reportsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        firestoreService = FirebaseFirestoreService.getInstance();
        tvTotals = findViewById(R.id.tv_report_totals);
        reportsContainer = findViewById(R.id.reports_container);

        // Load both summary numbers and recent incident cards when the page opens.
        loadStats();
        loadRecentIncidentReports();
    }

    private void loadStats() {
        // Build the report summary block from Firestore dashboard metrics.
        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {
            @Override
            public void onSuccess(Map<String, Integer> stats) {
                // These totals are the same counters used elsewhere, aggregated for one quick view.
                tvTotals.setText(getString(
                        R.string.admin_report_totals,
                        stats.getOrDefault("totalUsers", 0),
                        stats.getOrDefault("totalComplaints", 0),
                        stats.getOrDefault("resolvedComplaints", 0),
                        stats.getOrDefault("pendingComplaints", 0)));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminReportsActivity.this, getString(R.string.admin_could_not_load_report_totals), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecentIncidentReports() {
        // Show a compact feed of recent complaints with a direct action button.
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                reportsContainer.removeAllViews();
                int limit = Math.min(complaints.size(), 12);
                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(AdminReportsActivity.this);
                for (int i = 0; i < limit; i++) {
                    Complaint complaint = complaints.get(i);
                    android.view.View card = inflater.inflate(R.layout.item_admin_complaint_card, reportsContainer, false);
                    TextView tvTitle = card.findViewById(R.id.tv_complaint_title);
                    TextView tvMeta = card.findViewById(R.id.tv_complaint_meta);
                    TextView tvStatus = card.findViewById(R.id.tv_complaint_status);
                    android.widget.Button btnTakeAction = card.findViewById(R.id.btn_take_action);

                    // Full details help admins investigate without opening another screen.
                    tvTitle.setText(getString(R.string.admin_complaint_title_index, i + 1, complaint.getTitle() != null ? complaint.getTitle() : getString(R.string.complaint)));
                    tvMeta.setText(getString(
                            R.string.admin_complaint_details,
                            complaint.getDate() != null ? complaint.getDate() : "N/A",
                            complaint.getLocation() != null ? complaint.getLocation() : "N/A",
                            complaint.getPriority() != null ? complaint.getPriority() : "N/A",
                            complaint.getDescription() != null ? complaint.getDescription() : "N/A",
                            complaint.getWitnesses() != null ? complaint.getWitnesses() : "N/A",
                            complaint.getVehicle() != null ? complaint.getVehicle() : "N/A",
                            complaint.getSuspectDescription() != null ? complaint.getSuspectDescription() : "N/A",
                            complaint.getEvidence() != null && !complaint.getEvidence().isEmpty() ? complaint.getEvidence() : "N/A",
                            complaint.getEvidenceUrls() != null ? complaint.getEvidenceUrls().size() : 0));
                    tvStatus.setText(getString(R.string.admin_complaint_status, complaint.getStatus() != null ? complaint.getStatus() : "Pending"));

                    // Action flow mirrors complaints page: mark as Under Review and refresh list.
                    btnTakeAction.setOnClickListener(v -> firestoreService.updateComplaintStatus(
                            complaint.getId(),
                            "Under Review",
                            new FirebaseFirestoreService.OnOperationCallback() {
                                @Override
                                public void onSuccess() {
                                    loadRecentIncidentReports();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(AdminReportsActivity.this, getString(R.string.admin_could_not_update_complaint), Toast.LENGTH_SHORT).show();
                                }
                            }
                    ));

                    reportsContainer.addView(card);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminReportsActivity.this, getString(R.string.admin_could_not_load_incident_reports), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_reports;
    }

    @Override
    public void onBackPressed() {
        // Reports page returns to the dashboard.
        navigateToDashboard();
    }
}
