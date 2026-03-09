package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.ComplaintAdapter;
import com.example.yuwathi.models.Complaint;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Complaints Management Activity
 * View, filter, and manage complaints submitted by users
 */
public class AdminComplaintsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComplaintAdapter complaintAdapter;
    private SearchView searchView;
    private ChipGroup chipGroupStatus;
    private List<Complaint> complaintList;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_complaints);

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
                // TODO: Show complaint details
                Toast.makeText(AdminComplaintsActivity.this, "Details: " + complaint.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdateStatus(Complaint complaint, String newStatus) {
                // TODO: Update complaint status via API
                Toast.makeText(AdminComplaintsActivity.this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(Complaint complaint) {
                // TODO: Confirm and delete
                Toast.makeText(AdminComplaintsActivity.this, "Delete: " + complaint.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(complaintAdapter);
    }

    private void loadComplaints() {
        // Mock data - replace with actual API call
        complaintList.clear();
        complaintList.add(new Complaint("1", "Harassment at Bus Stand", "Colombo 07", "2024-03-08", "Pending", "High"));
        complaintList.add(new Complaint("2", "Suspicious Person", "Kandy", "2024-03-07", "Under Review", "Medium"));
        complaintList.add(new Complaint("3", "Inappropriate Behavior", "Galle", "2024-03-06", "Resolved", "High"));
        complaintList.add(new Complaint("4", "Street Harassment", "Negombo", "2024-03-05", "Pending", "Medium"));
        complaintList.add(new Complaint("5", "Stalking Incident", "Colombo 03", "2024-03-04", "Resolved", "High"));
        complaintAdapter.notifyDataSetChanged();
    }

    private void setupFilters() {
        // Status filter chips
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

        // Search functionality
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
}
