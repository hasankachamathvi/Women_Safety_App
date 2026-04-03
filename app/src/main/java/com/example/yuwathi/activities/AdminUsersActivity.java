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

        loadUsers();
    }

    private void loadUsers() {
        firestoreService.getAllUsers(new FirebaseFirestoreService.OnUsersListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                tvUsersCount.setText("Total users: " + users.size());
                renderUsers(users);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminUsersActivity.this, "Could not load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUsers(List<User> users) {
        usersContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (User user : users) {
            View card = inflater.inflate(R.layout.item_admin_user_card, usersContainer, false);
            TextView tvName = card.findViewById(R.id.tv_user_name);
            TextView tvEmail = card.findViewById(R.id.tv_user_email);
            TextView tvRole = card.findViewById(R.id.tv_user_role);

            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
            tvRole.setText("Role: " + (user.getRole() != null ? user.getRole() : "user"));
            usersContainer.addView(card);
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_users;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }
}
