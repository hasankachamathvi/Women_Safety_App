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

    // Firebase service instance for database operations
    private FirebaseFirestoreService firestoreService;

    // Container that holds all complaint cards
    private LinearLayout complaintsContainer;

    // TextView to show total complaint count
    private TextView tvComplaintsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        // Initialize Firebase service
        firestoreService = FirebaseFirestoreService.getInstance();

        // Bind UI elements
        complaintsContainer = findViewById(R.id.complaints_container);
        tvComplaintsCount = findViewById(R.id.tv_complaints_count);

        // Load all complaint records when the admin opens the page
        loadComplaints();
    }

    /**
     * Load complaints from Firestore database
     */
    private void loadComplaints() {
        // Fetch all complaints from Firebase
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {

                // Set total complaint count
                tvComplaintsCount.setText(getString(R.string.admin_complaint_count, complaints.size()));

                // Render complaint cards in UI
                renderComplaints(complaints);
            }

            @Override
            public void onError(String error) {
                // Show error message if loading fails
                Toast.makeText(AdminComplaintsActivity.this,
                        getString(R.string.admin_could_not_load_complaints),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Display complaint cards dynamically
     */
    private void renderComplaints(List<Complaint> complaints) {

        // Clear old views before reloading new data
        complaintsContainer.removeAllViews();

        // Layout inflater to convert XML into View objects
        LayoutInflater inflater = LayoutInflater.from(this);

        // Loop through each complaint
        for (Complaint complaint : complaints) {

            // Inflate complaint card layout
            View card = inflater.inflate(R.layout.item_admin_complaint_card, complaintsContainer, false);

            // Bind UI elements inside card
            TextView tvTitle = card.findViewById(R.id.tv_complaint_title);
            TextView tvMeta = card.findViewById(R.id.tv_complaint_meta);
            TextView tvStatus = card.findViewById(R.id.tv_complaint_status);
            Button btnTakeAction = card.findViewById(R.id.btn_take_action);

            // Set complaint title (fallback if null)
            tvTitle.setText(complaint.getTitle() != null ? complaint.getTitle() : getString(R.string.complaint));

            // Set location and date info
            tvMeta.setText(getString(
                    R.string.admin_complaint_meta,
                    complaint.getLocation() != null ? complaint.getLocation() : "N/A",
                    complaint.getDate() != null ? complaint.getDate() : "N/A"
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
                                    Toast.makeText(AdminComplaintsActivity.this,
                                            getString(R.string.admin_action_under_review),
                                            Toast.LENGTH_SHORT).show();

                                    // Reload complaints after update
                                    loadComplaints();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(AdminComplaintsActivity.this,
                                            getString(R.string.admin_could_not_update_complaint),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    )
            );

            // Add card to container
            complaintsContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_complaints;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to dashboard
        navigateToDashboard();
    }
}