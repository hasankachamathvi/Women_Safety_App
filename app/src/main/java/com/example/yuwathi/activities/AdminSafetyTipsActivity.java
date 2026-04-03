package com.example.yuwathi.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.yuwathi.R;
import com.example.yuwathi.models.SafetyTip;
import com.example.yuwathi.services.FirebaseFirestoreService;

import java.util.List;

public class AdminSafetyTipsActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private LinearLayout tipsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_safety_tips);

        firestoreService = FirebaseFirestoreService.getInstance();
        tipsContainer = findViewById(R.id.tips_container);

        Button btnAddTip = findViewById(R.id.btn_add_tip);
        btnAddTip.setOnClickListener(v -> showAddTipDialog());

        loadTips();
    }

    private void loadTips() {
        firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                renderTips(tips);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminSafetyTipsActivity.this, "Could not load safety tips", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderTips(List<SafetyTip> tips) {
        tipsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (SafetyTip tip : tips) {
            View card = inflater.inflate(R.layout.item_admin_tip_card, tipsContainer, false);
            TextView tvTitle = card.findViewById(R.id.tv_tip_title);
            TextView tvDesc = card.findViewById(R.id.tv_tip_desc);
            Button btnToggle = card.findViewById(R.id.btn_tip_toggle);
            Button btnDelete = card.findViewById(R.id.btn_tip_delete);

            tvTitle.setText(tip.getTitle() != null ? tip.getTitle() : "Safety Tip");
            tvDesc.setText((tip.getDescription() != null ? tip.getDescription() : "")
                    + "\nCategory: " + (tip.getCategory() != null ? tip.getCategory() : "General")
                    + "\nVisible: " + tip.isVisible());

            btnToggle.setText(tip.isVisible() ? "Hide" : "Show");
            btnToggle.setOnClickListener(v -> {
                tip.setVisible(!tip.isVisible());
                firestoreService.updateSafetyTip(tip.getId(), tip, new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        loadTips();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminSafetyTipsActivity.this, "Could not update tip", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnDelete.setOnClickListener(v -> firestoreService.deleteSafetyTip(tip.getId(), new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    loadTips();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminSafetyTipsActivity.this, "Could not delete tip", Toast.LENGTH_SHORT).show();
                }
            }));

            tipsContainer.addView(card);
        }
    }

    private void showAddTipDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_tip, null);
        EditText etTitle = view.findViewById(R.id.et_tip_title);
        EditText etDesc = view.findViewById(R.id.et_tip_desc);
        EditText etCategory = view.findViewById(R.id.et_tip_category);

        new AlertDialog.Builder(this)
                .setTitle("Add Safety Tip")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    if (title.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, "Please fill title and description", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SafetyTip tip = new SafetyTip();
                    tip.setTitle(title);
                    tip.setDescription(desc);
                    tip.setCategory(category.isEmpty() ? "General" : category);
                    tip.setVisible(true);

                    firestoreService.addSafetyTip(tip, new FirebaseFirestoreService.OnOperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AdminSafetyTipsActivity.this, "Tip added", Toast.LENGTH_SHORT).show();
                            loadTips();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AdminSafetyTipsActivity.this, "Could not add tip", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_safety_tips;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }
}

