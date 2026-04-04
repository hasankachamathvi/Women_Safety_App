package com.example.yuwathi.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuwathi.R;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseFirestoreService;

import java.util.List;

/**
 * Admin screen for reviewing and updating complaints.
 */
public class AdminComplaintsActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private LinearLayout complaintsContainer;
    private TextView tvComplaintsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        firestoreService = FirebaseFirestoreService.getInstance();
        complaintsContainer = findViewById(R.id.complaints_container);
        tvComplaintsCount = findViewById(R.id.tv_complaints_count);

        // Load all complaint records when the admin opens the page.
        loadComplaints();
    }

    private void loadComplaints() {
        // Refresh the complaint list from Firestore before rendering the cards.
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                tvComplaintsCount.setText(getString(R.string.admin_complaint_count, complaints.size()));
                renderComplaints(complaints);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminComplaintsActivity.this, getString(R.string.admin_could_not_load_complaints), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderComplaints(List<Complaint> complaints) {
        // Rebuild the complaint cards so the admin always sees current status data.
        complaintsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Complaint complaint : complaints) {
            View card = inflater.inflate(R.layout.item_admin_complaint_card, complaintsContainer, false);
            TextView tvTitle = card.findViewById(R.id.tv_complaint_title);
            TextView tvMeta = card.findViewById(R.id.tv_complaint_meta);
            TextView tvStatus = card.findViewById(R.id.tv_complaint_status);
            Button btnTakeAction = card.findViewById(R.id.btn_take_action);

            // Show the minimum key details needed for triage from the dashboard context.
            tvTitle.setText(complaint.getTitle() != null ? complaint.getTitle() : getString(R.string.complaint));
            tvMeta.setText(getString(
                    R.string.admin_complaint_meta,
                    complaint.getLocation() != null ? complaint.getLocation() : "N/A",
                    complaint.getDate() != null ? complaint.getDate() : "N/A"));
            tvStatus.setText(getString(R.string.admin_complaint_status, complaint.getStatus() != null ? complaint.getStatus() : "Pending"));

            // "Take Action" is intentionally simple: move status to Under Review and refresh.
            btnTakeAction.setOnClickListener(v -> firestoreService.updateComplaintStatus(complaint.getId(), "Under Review", new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AdminComplaintsActivity.this, getString(R.string.admin_action_under_review), Toast.LENGTH_SHORT).show();
                    loadComplaints();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminComplaintsActivity.this, getString(R.string.admin_could_not_update_complaint), Toast.LENGTH_SHORT).show();
                }
            }));

            complaintsContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_complaints;
    }

    @Override
    public void onBackPressed() {
        // Complaints page returns to the dashboard.
        navigateToDashboard();
    }
}
