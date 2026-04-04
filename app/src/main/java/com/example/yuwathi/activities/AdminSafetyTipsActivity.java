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

/**
 * Admin screen for creating and managing safety tips.
 */
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
        // Entry point for creating new records in the `safety_tips` collection.
        btnAddTip.setOnClickListener(v -> showAddTipDialog());

        // Initial hydration: load existing tips so admin starts with current database state.
        loadTips();
    }

    /**
     * Reads all safety tips from Firestore and delegates rendering.
     * This is called on first open and after every mutating action (add/update/delete)
     * so the screen remains source-of-truth aligned with backend data.
     */
    private void loadTips() {
        // Pull all tips from Firestore before rendering the management cards.
        firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {
            @Override
            public void onSuccess(List<SafetyTip> tips) {
                renderTips(tips);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminSafetyTipsActivity.this, getString(R.string.admin_could_not_load_safety_tips), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Rebuilds the visual list from scratch based on the latest fetched tip list.
     * Full rebuild is simpler and safer here because list size is small and avoids
     * partial-state bugs after toggle/delete operations.
     */
    private void renderTips(List<SafetyTip> tips) {
        // Rebuild the list so visibility and delete actions stay in sync with the database.
        tipsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (SafetyTip tip : tips) {
            View card = inflater.inflate(R.layout.item_admin_tip_card, tipsContainer, false);
            TextView tvTitle = card.findViewById(R.id.tv_tip_title);
            TextView tvDesc = card.findViewById(R.id.tv_tip_desc);
            Button btnToggle = card.findViewById(R.id.btn_tip_toggle);
            Button btnDelete = card.findViewById(R.id.btn_tip_delete);

            // Display all operator-relevant fields so admins can decide action quickly.
            tvTitle.setText(tip.getTitle() != null ? tip.getTitle() : getString(R.string.admin_safety_tip_default));
            tvDesc.setText(getString(
                    R.string.admin_tip_details,
                    tip.getDescription() != null ? tip.getDescription() : "",
                    tip.getCategory() != null ? tip.getCategory() : getString(R.string.admin_general_category),
                    String.valueOf(tip.isVisible())));

            // Visibility switch controls whether this tip appears in the user app.
            btnToggle.setText(tip.isVisible() ? getString(R.string.admin_hide) : getString(R.string.admin_show));
            btnToggle.setOnClickListener(v -> {
                // Update local object first, then persist; final UI truth comes from `loadTips()`.
                tip.setVisible(!tip.isVisible());
                firestoreService.updateSafetyTip(tip.getId(), tip, new FirebaseFirestoreService.OnOperationCallback() {
                    @Override
                    public void onSuccess() {
                        // Requery instead of patching just one card to avoid stale mixed states.
                        loadTips();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminSafetyTipsActivity.this, getString(R.string.admin_could_not_update_tip), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Delete performs a hard remove in Firestore; no local cache is kept.
            btnDelete.setOnClickListener(v -> firestoreService.deleteSafetyTip(tip.getId(), new FirebaseFirestoreService.OnOperationCallback() {
                @Override
                public void onSuccess() {
                    // Refresh list to immediately remove deleted card from the UI.
                    loadTips();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminSafetyTipsActivity.this, getString(R.string.admin_could_not_delete_tip), Toast.LENGTH_SHORT).show();
                }
            }));

            tipsContainer.addView(card);
        }
    }

    /**
     * Shows add-tip dialog and writes a new document if validation passes.
     * New tips default to visible so user-facing safety content updates immediately.
     */
    private void showAddTipDialog() {
        // The dialog keeps add-tip fields focused in one place for quick entry.
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_tip, null);
        EditText etTitle = view.findViewById(R.id.et_tip_title);
        EditText etDesc = view.findViewById(R.id.et_tip_desc);
        EditText etCategory = view.findViewById(R.id.et_tip_category);

        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_add_safety_tip)
                .setView(view)
                .setPositiveButton(R.string.admin_save, (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    // Required fields prevent empty or unusable guidance being published.
                    if (title.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, getString(R.string.admin_fill_title_description), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SafetyTip tip = new SafetyTip();
                    tip.setTitle(title);
                    tip.setDescription(desc);
                    tip.setCategory(category.isEmpty() ? getString(R.string.admin_general_category) : category);
                    tip.setVisible(true);

                    // Persist new tip, then reload list so server-generated fields are reflected.
                    firestoreService.addSafetyTip(tip, new FirebaseFirestoreService.OnOperationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AdminSafetyTipsActivity.this, getString(R.string.admin_tip_added), Toast.LENGTH_SHORT).show();
                            loadTips();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AdminSafetyTipsActivity.this, getString(R.string.admin_could_not_load_safety_tips), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.admin_cancel, null)
                .show();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_safety_tips;
    }

    @Override
    public void onBackPressed() {
        // Keep admin navigation behavior consistent across all admin screens.
        navigateToDashboard();
    }
}
