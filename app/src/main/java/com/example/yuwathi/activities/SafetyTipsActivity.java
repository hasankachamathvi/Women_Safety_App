package com.example.yuwathi.activities;

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

    private SafetyTipAdapter safetyTipAdapter;
    private List<SafetyTip> safetyTipList;
    private FirebaseFirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        firestoreService = FirebaseFirestoreService.getInstance();

        RecyclerView rvTips = findViewById(R.id.rv_tips);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        safetyTipList = new ArrayList<>();

        safetyTipAdapter = new SafetyTipAdapter(this, safetyTipList,
                new SafetyTipAdapter.OnSafetyTipActionListener() {
                    @Override
                    public void onEdit(SafetyTip tip) {}

                    @Override
                    public void onDelete(SafetyTip tip) {}

                    @Override
                    public void onToggleVisibility(SafetyTip tip) {}
                });

        rvTips.setLayoutManager(new LinearLayoutManager(this));
        rvTips.setAdapter(safetyTipAdapter);

        loadTips();

        bottomNav.setSelectedItemId(R.id.nav_tips);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_contacts) {
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            } else if (id == R.id.nav_sos) {
                startActivity(new Intent(this, SosActivity.class));
                return true;
            } else if (id == R.id.nav_tips) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
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
                // Try fallback method
                firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
                    @Override
                    public void onSuccess(List<SafetyTip> tips) {
                        safetyTipAdapter.setTips(tips);
                    }

                    @Override
                    public void onError(String error) {
                        android.widget.Toast.makeText(
                                SafetyTipsActivity.this,
                                "Failed to load tips",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });
    }
}