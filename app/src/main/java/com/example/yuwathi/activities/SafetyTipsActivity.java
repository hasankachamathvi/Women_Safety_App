package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuwathi.R;
import com.example.yuwathi.adapters.SafetyTipAdapter;
import com.example.yuwathi.models.SafetyTip;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SafetyTipsActivity extends AppCompatActivity {

    // Adapter used to show tips in the RecyclerView.
    private SafetyTipAdapter safetyTipAdapter;
    // In-memory list that backs the adapter.
    private List<SafetyTip> safetyTipList;
    // Shared Firestore helper for loading tips.
    private FirebaseFirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity lifecycle.
        super.onCreate(savedInstanceState);
        // Set the safety tips screen layout
        setContentView(R.layout.activity_tips);

        // Get singleton Firestore service.
        firestoreService = FirebaseFirestoreService.getInstance();

        // Find views from the layout
        RecyclerView rvTips = findViewById(R.id.rv_tips);            // List of safety tips
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav); // Bottom navigation bar

        // Create empty list before loading data from Firestore.
        safetyTipList = new ArrayList<>();
        // Connect adapter with action callbacks (currently no-op on this screen).
        safetyTipAdapter = new SafetyTipAdapter(this, safetyTipList, new SafetyTipAdapter.OnSafetyTipActionListener() {
            @Override
            public void onEdit(SafetyTip tip) {
                // Edit is not handled in this user-facing tips screen.
            }

            @Override
            public void onDelete(SafetyTip tip) {
                // Delete is not handled in this user-facing tips screen.
            }

            @Override
            public void onToggleVisibility(SafetyTip tip) {
                // Visibility toggle is not handled in this user-facing tips screen.
            }
        });
        // Display items as a vertical list.
        rvTips.setLayoutManager(new LinearLayoutManager(this));
        // Attach adapter to RecyclerView.
        rvTips.setAdapter(safetyTipAdapter);

        // Fetch and show tips from backend.
        loadTips();

        // Set Tips as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_tips);

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            // Read selected menu item id.
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Go to Home screen.
                startActivity(new Intent(SafetyTipsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                // Go to Contacts screen.
                startActivity(new Intent(SafetyTipsActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                // Go to SOS screen.
                startActivity(new Intent(SafetyTipsActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                return true;  // Already on Tips screen
            } else if (id == R.id.nav_profile) {
                // Go to Profile screen.
                startActivity(new Intent(SafetyTipsActivity.this, ProfileActivity.class));
                return true;
            }
            // Return false for any unknown item.
            return false;
        });
    }

    private void loadTips() {
        // Request all safety tips from Firestore.
        firestoreService.getSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                // Update adapter with latest data.
                safetyTipAdapter.setTips(tips);
            }

            @Override
            public void onError(String error) {
                // Show quick message if loading fails.
                android.widget.Toast.makeText(SafetyTipsActivity.this, "Failed to load tips: " + error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
