package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.ComplaintAdapter;
import com.example.yuwathi.models.Complaint;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Complaints Management Activity
 * View, filter, and manage complaints submitted by users
 */
public class AdminComplaintsActivity extends BaseAdminActivity {

    private RecyclerView recyclerView;
    private ComplaintAdapter complaintAdapter;
    private SearchView searchView;
    private ChipGroup chipGroupStatus;
    private List<Complaint> complaintList;
    private String currentFilter = "All";
    private FirebaseFirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

        firestoreService = FirebaseFirestoreService.getInstance();

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Complaints Management");
        }

        initializeViews();
        setupRecyclerView();
        loadComplaints();
        setupFilters();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_complaints);
        searchView = findViewById(R.id.search_complaints);
        chipGroupStatus = findViewById(R.id.chip_group_status);
    }

    private void setupRecyclerView() {
        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(this, complaintList, new ComplaintAdapter.OnComplaintActionListener() {
            @Override
            public void onViewDetails(Complaint complaint) {
                String details = "Type: " + complaint.getTitle() +
                        "\nLocation: " + complaint.getLocation() +
                        "\nDate: " + complaint.getDate() +
                        "\nStatus: " + complaint.getStatus() +
                        "\nPriority: " + complaint.getPriority() +
                        "\nDescription: " + String.valueOf(complaint.getDescription());
                new AlertDialog.Builder(AdminComplaintsActivity.this)
                        .setTitle("Complaint Details")
                        .setMessage(details)
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onUpdateStatus(Complaint complaint, String newStatus) {
                firestoreService.updateComplaintStatus(complaint.getId(), newStatus, new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AdminComplaintsActivity.this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                        loadComplaints();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminComplaintsActivity.this, "Update failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDelete(Complaint complaint) {
                new AlertDialog.Builder(AdminComplaintsActivity.this)
                        .setTitle("Delete Complaint")
                        .setMessage("Delete this complaint permanently?")
                        .setPositiveButton("Delete", (dialog, which) -> firestoreService.deleteComplaint(complaint.getId(), new FirebaseFirestoreService.OnOperationCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AdminComplaintsActivity.this, "Complaint deleted", Toast.LENGTH_SHORT).show();
                                loadComplaints();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AdminComplaintsActivity.this, "Delete failed: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(complaintAdapter);
    }

    private void loadComplaints() {
        firestoreService.getAllComplaints(new FirebaseFirestoreService.OnComplaintsListCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                complaintAdapter.setComplaints(complaints);
                complaintAdapter.filterByStatus(currentFilter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminComplaintsActivity.this, "Failed to load complaints: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilters() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int chipId = checkedIds.get(0);
                if (chipId == R.id.chip_all) {
                    currentFilter = "All";
                } else if (chipId == R.id.chip_pending) {
                    currentFilter = "Pending";
                } else if (chipId == R.id.chip_under_review) {
                    currentFilter = "Under Review";
                } else if (chipId == R.id.chip_resolved) {
                    currentFilter = "Resolved";
                }
                applyFilter();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                complaintAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                complaintAdapter.filter(newText);
                return true;
            }
        });
    }

    private void applyFilter() {
        complaintAdapter.filterByStatus(currentFilter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_complaints;
    }
}
