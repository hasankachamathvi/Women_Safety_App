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
 */
public class AdminUsersActivity extends BaseAdminActivity {

    private FirebaseFirestoreService firestoreService;
    private LinearLayout usersContainer;
    private TextView tvUsersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        firestoreService = FirebaseFirestoreService.getInstance();
        usersContainer = findViewById(R.id.users_container);
        tvUsersCount = findViewById(R.id.tv_users_count);

        // Load the user list as soon as the screen opens.
        loadUsers();
    }

    private void loadUsers() {
        // Fetch all registered users from Firestore and refresh the list view.
        firestoreService.getAllUsers(new FirebaseFirestoreService.OnUsersListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                tvUsersCount.setText(getString(R.string.admin_total_users, users.size()));
                renderUsers(users);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminUsersActivity.this, getString(R.string.admin_could_not_load_users), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUsers(List<User> users) {
        // Rebuild the list so the UI always reflects the latest database state.
        usersContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (User user : users) {
            View card = inflater.inflate(R.layout.item_admin_user_card, usersContainer, false);
            TextView tvName = card.findViewById(R.id.tv_user_name);
            TextView tvEmail = card.findViewById(R.id.tv_user_email);
            TextView tvRole = card.findViewById(R.id.tv_user_role);

            // Render profile basics exactly as stored in Firestore (with safe fallbacks).
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : getString(R.string.admin_no_email));
            tvRole.setText(getString(R.string.admin_user_role, user.getRole() != null ? user.getRole() : getString(R.string.admin_user_default_role)));
            usersContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_users;
    }

    @Override
    public void onBackPressed() {
        // Admin users page returns to the dashboard instead of exiting.
        navigateToDashboard();
    }
}
