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

        loadComplaints();
    }

    private void loadComplaints() {
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                tvComplaintsCount.setText("Total complaints: " + complaints.size());
                renderComplaints(complaints);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminComplaintsActivity.this, "Could not load complaints", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderComplaints(List<Complaint> complaints) {
        complaintsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Complaint complaint : complaints) {
            View card = inflater.inflate(R.layout.item_admin_complaint_card, complaintsContainer, false);
            TextView tvTitle = card.findViewById(R.id.tv_complaint_title);
            TextView tvMeta = card.findViewById(R.id.tv_complaint_meta);
            TextView tvStatus = card.findViewById(R.id.tv_complaint_status);
            Button btnTakeAction = card.findViewById(R.id.btn_take_action);

            tvTitle.setText(complaint.getTitle() != null ? complaint.getTitle() : "Complaint");
            tvMeta.setText("Location: " + (complaint.getLocation() != null ? complaint.getLocation() : "N/A")
                    + "\nDate: " + (complaint.getDate() != null ? complaint.getDate() : "N/A"));
            tvStatus.setText("Status: " + (complaint.getStatus() != null ? complaint.getStatus() : "Pending"));

            btnTakeAction.setOnClickListener(v -> {
                firestoreService.updateComplaintStatus(complaint.getId(), "Under Review", new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AdminComplaintsActivity.this, "Action taken: moved to Under Review", Toast.LENGTH_SHORT).show();
                        loadComplaints();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminComplaintsActivity.this, "Could not update complaint", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            complaintsContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_complaints;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }
}
