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

        loadStats();
        loadRecentIncidentReports();
    }

    private void loadStats() {
        firestoreService.getDashboardStats(new FirebaseFirestoreService.OnStatsCallback() {
            @Override
            public void onSuccess(Map<String, Integer> stats) {
                String totals = "Users: " + stats.getOrDefault("totalUsers", 0)
                        + "\nComplaints: " + stats.getOrDefault("totalComplaints", 0)
                        + "\nResolved: " + stats.getOrDefault("resolvedComplaints", 0)
                        + "\nPending: " + stats.getOrDefault("pendingComplaints", 0);
                tvTotals.setText(totals);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminReportsActivity.this, "Could not load report totals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecentIncidentReports() {
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

                    tvTitle.setText((i + 1) + ". " + (complaint.getTitle() != null ? complaint.getTitle() : "Complaint"));
                    tvMeta.setText("Date: " + (complaint.getDate() != null ? complaint.getDate() : "N/A")
                            + "\nLocation: " + (complaint.getLocation() != null ? complaint.getLocation() : "N/A")
                            + "\nPriority: " + (complaint.getPriority() != null ? complaint.getPriority() : "N/A")
                            + "\nDescription: " + (complaint.getDescription() != null ? complaint.getDescription() : "N/A")
                            + "\nWitnesses: " + (complaint.getWitnesses() != null ? complaint.getWitnesses() : "N/A")
                            + "\nVehicle: " + (complaint.getVehicle() != null ? complaint.getVehicle() : "N/A")
                            + "\nSuspect: " + (complaint.getSuspectDescription() != null ? complaint.getSuspectDescription() : "N/A")
                            + "\nEvidence: " + (complaint.getEvidence() != null && !complaint.getEvidence().isEmpty() ? complaint.getEvidence() : "N/A")
                            + "\nEvidence files: " + (complaint.getEvidenceUrls() != null ? complaint.getEvidenceUrls().size() : 0));
                    tvStatus.setText("Status: " + (complaint.getStatus() != null ? complaint.getStatus() : "Pending"));

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
                                    Toast.makeText(AdminReportsActivity.this, "Could not update complaint", Toast.LENGTH_SHORT).show();
                                }
                            }
                    ));

                    reportsContainer.addView(card);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminReportsActivity.this, "Could not load incident reports", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_reports;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }
}
