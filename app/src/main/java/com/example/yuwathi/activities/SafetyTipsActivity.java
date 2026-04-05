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

/**
 * Displays safety tips to end users.
 */
public class SafetyTipsActivity extends AppCompatActivity {

    private SafetyTipAdapter safetyTipAdapter;
    private List<SafetyTip> safetyTipList;
    private FirebaseFirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the safety tips screen layout
        setContentView(R.layout.activity_tips);

        firestoreService = FirebaseFirestoreService.getInstance();

        // Find views from the layout
        RecyclerView rvTips = findViewById(R.id.rv_tips);            // List of safety tips
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav); // Bottom navigation bar

        safetyTipList = new ArrayList<>();
        safetyTipAdapter = new SafetyTipAdapter(this, safetyTipList, new SafetyTipAdapter.OnSafetyTipActionListener() {
            @Override
            public void onEdit(SafetyTip tip) { }

            @Override
            public void onDelete(SafetyTip tip) { }

            @Override
            public void onToggleVisibility(SafetyTip tip) { }
        });
        rvTips.setLayoutManager(new LinearLayoutManager(this));
        rvTips.setAdapter(safetyTipAdapter);

        loadTips();

        // Set Tips as the selected tab in bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_tips);

        // Handle bottom navigation bar item clicks
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SafetyTipsActivity.this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(SafetyTipsActivity.this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(SafetyTipsActivity.this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                return true;  // Already on Tips screen
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SafetyTipsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadTips() {
        firestoreService.getSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                safetyTipAdapter.setTips(tips);
            }

            @Override
            public void onError(String error) {
                // Fallback to all tips so admin-added tips still appear if visibility field differs.
                firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
                    @Override
                    public void onSuccess(List<SafetyTip> tips) {
                        safetyTipAdapter.setTips(tips);
                    }

                    @Override
                    public void onError(String error) {
                        android.widget.Toast.makeText(SafetyTipsActivity.this, "Failed to load tips", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
