package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.SafetyTipAdapter;
import com.example.yuwathi.models.SafetyTip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Safety Tips Management Activity
 * Manage safety tips shown to users in the main app
 */
public class AdminSafetyTipsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SafetyTipAdapter safetyTipAdapter;
    private FloatingActionButton fabAdd;
    private List<SafetyTip> safetyTipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_safety_tips);

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Safety Tips Management");
        }

        initializeViews();
        setupRecyclerView();
        loadSafetyTips();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_safety_tips);
        fabAdd = findViewById(R.id.fab_add_tip);

        fabAdd.setOnClickListener(v -> {
            // TODO: Open dialog to add new safety tip
            Toast.makeText(this, "Add Safety Tip - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        safetyTipList = new ArrayList<>();
        safetyTipAdapter = new SafetyTipAdapter(this, safetyTipList, new SafetyTipAdapter.OnSafetyTipActionListener() {
            @Override
            public void onEdit(SafetyTip tip) {
                // TODO: Open edit dialog
                Toast.makeText(AdminSafetyTipsActivity.this, "Edit: " + tip.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(SafetyTip tip) {
                // TODO: Confirm and delete
                Toast.makeText(AdminSafetyTipsActivity.this, "Delete: " + tip.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggleVisibility(SafetyTip tip) {
                // TODO: Toggle tip visibility
                tip.setVisible(!tip.isVisible());
                safetyTipAdapter.notifyDataSetChanged();
                Toast.makeText(AdminSafetyTipsActivity.this, 
                    tip.isVisible() ? "Tip now visible" : "Tip now hidden", 
                    Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(safetyTipAdapter);
    }

    private void loadSafetyTips() {
        // Mock data - replace with actual API call
        safetyTipList.clear();
        safetyTipList.add(new SafetyTip("1", "Trust Your Instincts", "If something feels wrong, it probably is. Trust your gut feeling.", "general", true));
        safetyTipList.add(new SafetyTip("2", "Stay Alert", "Always be aware of your surroundings, especially in unfamiliar areas.", "awareness", true));
        safetyTipList.add(new SafetyTip("3", "Share Your Location", "Let trusted contacts know where you are, especially when traveling alone.", "technology", true));
        safetyTipList.add(new SafetyTip("4", "Emergency Contacts", "Keep emergency numbers on speed dial and memorize them.", "emergency", true));
        safetyTipList.add(new SafetyTip("5", "Avoid Isolated Areas", "Stay in well-lit, populated areas, especially at night.", "travel", true));
        safetyTipAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
