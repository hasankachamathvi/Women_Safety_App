package com.example.yuwathi.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Admin screen for creating, updating, and deleting safety tips.
 * Controls safety content shown to end users.
 */
public class AdminSafetyTipsActivity extends BaseAdminActivity {

    // Firebase service instance for all Firestore operations
    private FirebaseFirestoreService firestoreService;

    // Container that holds all safety tip cards dynamically
    private LinearLayout tipsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout for safety tips admin screen
        setContentView(R.layout.activity_admin_safety_tips);

        // Initialize Firestore service
        firestoreService = FirebaseFirestoreService.getInstance();

        // Bind UI container for tips
        tipsContainer = findViewById(R.id.tips_container);

        // Button for adding new safety tip
        Button btnAddTip = findViewById(R.id.btn_add_tip);

        // Open dialog to create a new safety tip
        btnAddTip.setOnClickListener(v -> showAddTipDialog());

        // Load existing safety tips from database
        loadTips();
    }

    /**
     * Load all safety tips from Firestore
     * Keeps UI synced with backend data
     */
    private void loadTips() {

        firestoreService.getAllSafetyTips(new FirebaseFirestoreService.OnSafetyTipsListCallback() {

            @Override
            public void onSuccess(List<SafetyTip> tips) {

                // Render tips on screen
                renderTips(tips);
            }

            @Override
            public void onError(String error) {

                // Show error if loading fails
                Toast.makeText(AdminSafetyTipsActivity.this,
                        getString(R.string.admin_could_not_load_safety_tips),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Render safety tips dynamically as cards
     */
    private void renderTips(List<SafetyTip> tips) {

        // Clear old UI before re-rendering
        tipsContainer.removeAllViews();

        // Layout inflater to convert XML into View objects
        LayoutInflater inflater = LayoutInflater.from(this);

        // Loop through all safety tips
        for (SafetyTip tip : tips) {

            // Inflate safety tip card layout
            View card = inflater.inflate(R.layout.item_admin_tip_card, tipsContainer, false);

            // Bind UI elements inside card
            TextView tvTitle = card.findViewById(R.id.tv_tip_title);
            TextView tvDesc = card.findViewById(R.id.tv_tip_desc);
            TextView tvDate = card.findViewById(R.id.tv_tip_date);
            Button btnToggle = card.findViewById(R.id.btn_tip_toggle);
            Button btnDelete = card.findViewById(R.id.btn_tip_delete);

            // Set title with fallback
            tvTitle.setText(
                    tip.getTitle() != null
                            ? tip.getTitle()
                            : getString(R.string.admin_safety_tip_default)
            );

            // Set description + category + visibility info
            tvDesc.setText(getString(
                    R.string.admin_tip_details,
                    tip.getDescription() != null ? tip.getDescription() : "",
                    tip.getCategory() != null ? tip.getCategory() : getString(R.string.admin_general_category),
                    String.valueOf(tip.isVisible())
            ));

            // Show date only when the tip has a selected date.
            if (!TextUtils.isEmpty(tip.getDate())) {
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText("Date: " + tip.getDate());
            } else {
                tvDate.setVisibility(View.GONE);
            }

            // Toggle visibility button controls whether tip is shown in user app.
            btnToggle.setText(
                    tip.isVisible()
                            ? getString(R.string.admin_hide)
                            : getString(R.string.admin_show)
            );

            btnToggle.setOnClickListener(v -> {

                // Flip visibility state
                tip.setVisible(!tip.isVisible());

                // Update in Firestore
                firestoreService.updateSafetyTip(
                        tip.getId(),
                        tip,
                        new FirebaseFirestoreService.OnOperationCallback() {

                            @Override
                            public void onSuccess() {
                                // Reload list to reflect updated state
                                loadTips();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(AdminSafetyTipsActivity.this,
                                        getString(R.string.admin_could_not_update_tip),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            });

            // Delete safety tip permanently.
            btnDelete.setOnClickListener(v ->
                    firestoreService.deleteSafetyTip(
                            tip.getId(),
                            new FirebaseFirestoreService.OnOperationCallback() {

                                @Override
                                public void onSuccess() {
                                    // Refresh UI after deletion
                                    loadTips();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(AdminSafetyTipsActivity.this,
                                            getString(R.string.admin_could_not_delete_tip),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    )
            );

            // Add card to container
            tipsContainer.addView(card);
        }
    }

    /**
     * Dialog to add a new safety tip
     */
    private void showAddTipDialog() {

        // Inflate dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_tip, null);

        EditText etTitle = view.findViewById(R.id.et_tip_title);
        EditText etDesc = view.findViewById(R.id.et_tip_desc);
        EditText etCategory = view.findViewById(R.id.et_tip_category);
        EditText etDate = view.findViewById(R.id.et_schedule_time);
        Button btnPickDate = view.findViewById(R.id.btn_pick_date);

        // Calendar pop-up is available from both field and button taps.
        etDate.setOnClickListener(v -> showDatePicker(etDate));
        btnPickDate.setOnClickListener(v -> showDatePicker(etDate));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.admin_add_safety_tip)
                .setView(view)
                .setPositiveButton(R.string.admin_save, null)
                .setNegativeButton(R.string.admin_cancel, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            // Validate required fields
            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this,
                        getString(R.string.admin_fill_title_description),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (date.isEmpty()) {
                Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new SafetyTip object
            SafetyTip tip = new SafetyTip();
            tip.setTitle(title);
            tip.setDescription(desc);
            tip.setCategory(category.isEmpty()
                    ? getString(R.string.admin_general_category)
                    : category);
            tip.setDate(date);
            tip.setVisible(true);

            // Save to Firestore
            firestoreService.addSafetyTip(
                    tip,
                    new FirebaseFirestoreService.OnOperationCallback() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(AdminSafetyTipsActivity.this,
                                    getString(R.string.admin_tip_added),
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            // Reload list after adding
                            loadTips();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(AdminSafetyTipsActivity.this,
                                    getString(R.string.admin_could_not_load_safety_tips),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }));

        dialog.show();
    }

    private void showDatePicker(EditText targetField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(Calendar.YEAR, year);
                    selected.set(Calendar.MONTH, month);
                    selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    targetField.setText(formatter.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_safety_tips;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to dashboard
        navigateToDashboard();
    }
}