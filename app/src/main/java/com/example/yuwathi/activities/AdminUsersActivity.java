package com.example.yuwathi.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuwathi.R;
import com.example.yuwathi.models.User;
import com.example.yuwathi.services.FirebaseFirestoreService;

import java.util.List;

/**
 * Admin screen for viewing and managing users.
 * Displays all registered users from Firestore in a list format.
 */
public class AdminUsersActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private LinearLayout usersContainer;
    private TextView tvUsersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        // Initialize Firestore service instance for database operations.
        firestoreService = FirebaseFirestoreService.getInstance();

        // Bind UI components from XML layout.
        usersContainer = findViewById(R.id.users_container);
        tvUsersCount = findViewById(R.id.tv_users_count);

        // Load the user list as soon as the screen opens.
        loadUsers();
    }

    /**
     * Fetch all users from Firestore and display them in the UI.
     */
    private void loadUsers() {
        // Fetch all registered users from Firestore and refresh the list view.
        firestoreService.getAllUsers(new FirebaseFirestoreService.OnUsersListCallback() {
            @Override
            public void onSuccess(List<User> users) {

                // Update total user count on the screen.
                tvUsersCount.setText(getString(R.string.admin_total_users, users.size()));

                // Render user cards in the UI.
                renderUsers(users);
            }

            @Override
            public void onError(String error) {
                // Show error message if data cannot be loaded.
                Toast.makeText(AdminUsersActivity.this,
                        getString(R.string.admin_could_not_load_users),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Create and display user cards dynamically based on Firestore data.
     */
    private void renderUsers(List<User> users) {

        // Clear existing views before reloading updated data.
        usersContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        // Loop through each user and create a UI card.
        for (User user : users) {

            // Inflate user card layout.
            View card = inflater.inflate(R.layout.item_admin_user_card, usersContainer, false);

            // Bind UI elements inside the card.
            TextView tvName = card.findViewById(R.id.tv_user_name);
            TextView tvEmail = card.findViewById(R.id.tv_user_email);
            TextView tvRole = card.findViewById(R.id.tv_user_role);

            // Set user name.
            tvName.setText(user.getName());

            // Set user email with fallback if null.
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : getString(R.string.admin_no_email));

            // Set user role with fallback if not assigned.
            tvRole.setText(getString(
                    R.string.admin_user_role,
                    user.getRole() != null ? user.getRole() : getString(R.string.admin_user_default_role)
            ));

            // Add the user card to the container layout.
            usersContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_users;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to dashboard instead of exiting app.
        navigateToDashboard();
    }
}