package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.SafetyTipAdapter;
import com.example.yuwathi.models.SafetyTip;
import com.example.yuwathi.services.FirebaseFirestoreService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Safety Tips Management Activity
 * Manage safety tips shown to users in the main app
 */
public class AdminSafetyTipsActivity extends BaseAdminActivity {

    private RecyclerView recyclerView;
    private SafetyTipAdapter safetyTipAdapter;
    private FloatingActionButton fabAdd;
    private List<SafetyTip> safetyTipList;
    private FirebaseFirestoreService firestoreService;

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_safety_tips;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_safety_tips);

        firestoreService = FirebaseFirestoreService.getInstance();

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

        fabAdd.setOnClickListener(v -> showTipDialog(null));
    }

    private void setupRecyclerView() {
        safetyTipList = new ArrayList<>();
        safetyTipAdapter = new SafetyTipAdapter(this, safetyTipList, new SafetyTipAdapter.OnSafetyTipActionListener() {
            @Override
            public void onEdit(SafetyTip tip) {
                showTipDialog(tip);
            }

            @Override
            public void onDelete(SafetyTip tip) {
                new AlertDialog.Builder(AdminSafetyTipsActivity.this)
                        .setTitle("Delete Safety Tip")
                        .setMessage("Delete this safety tip permanently?")
                        .setPositiveButton("Delete", (dialog, which) -> firestoreService.deleteSafetyTip(tip.getId(), new FirebaseFirestoreService.OnOperationCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AdminSafetyTipsActivity.this, "Tip deleted", Toast.LENGTH_SHORT).show();
                                loadSafetyTips();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AdminSafetyTipsActivity.this, "Delete failed: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onToggleVisibility(SafetyTip tip) {
                firestoreService.updateSafetyTip(tip.getId(), tip, new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AdminSafetyTipsActivity.this, tip.isVisible() ? "Tip now visible" : "Tip now hidden", Toast.LENGTH_SHORT).show();
                        loadSafetyTips();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminSafetyTipsActivity.this, "Update failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(safetyTipAdapter);
    }

    private void loadSafetyTips() {
        firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                safetyTipAdapter.setTips(tips);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminSafetyTipsActivity.this, "Failed to load tips: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTipDialog(SafetyTip existingTip) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etTitle = new EditText(this);
        etTitle.setHint("Title");
        layout.addView(etTitle);

        EditText etDescription = new EditText(this);
        etDescription.setHint("Description");
        layout.addView(etDescription);

        EditText etCategory = new EditText(this);
        etCategory.setHint("Category");
        layout.addView(etCategory);

        CheckBox cbVisible = new CheckBox(this);
        cbVisible.setText("Visible");
        cbVisible.setChecked(existingTip == null || existingTip.isVisible());
        layout.addView(cbVisible);

        if (existingTip != null) {
            etTitle.setText(existingTip.getTitle());
            etDescription.setText(existingTip.getDescription());
            etCategory.setText(existingTip.getCategory());
        }

        new AlertDialog.Builder(this)
                .setTitle(existingTip == null ? "Add Safety Tip" : "Edit Safety Tip")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    SafetyTip tip = existingTip == null ? new SafetyTip() : existingTip;
                    tip.setTitle(etTitle.getText().toString().trim());
                    tip.setDescription(etDescription.getText().toString().trim());
                    tip.setCategory(etCategory.getText().toString().trim());
                    tip.setVisible(cbVisible.isChecked());

                    FirebaseFirestoreService.OnOperationCallback callback = new FirebaseFirestoreService.OnOperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AdminSafetyTipsActivity.this, existingTip == null ? "Tip added" : "Tip updated", Toast.LENGTH_SHORT).show();
                            loadSafetyTips();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AdminSafetyTipsActivity.this, "Save failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    };

                    if (existingTip == null) {
                        firestoreService.addSafetyTip(tip, callback);
                    } else {
                        firestoreService.updateSafetyTip(existingTip.getId(), tip, callback);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
