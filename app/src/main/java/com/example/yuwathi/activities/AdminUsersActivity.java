package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.UserAdapter;
import com.example.yuwathi.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Users Management Activity
 * View, search, and manage app users
 */
public class AdminUsersActivity extends BaseAdminActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private SearchView searchView;
    private FloatingActionButton fabAdd;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        // Set up toolbar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Management");
        }

        initializeViews();
        setupRecyclerView();
        loadUsers();
        setupSearch();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_users;
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_users);
        searchView = findViewById(R.id.search_users);
        fabAdd = findViewById(R.id.fab_add_user);

        fabAdd.setOnClickListener(v -> {
            // TODO: Open dialog or new activity to add user
            Toast.makeText(this, "Add User - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                // TODO: Open edit dialog
                Toast.makeText(AdminUsersActivity.this, "Edit: " + user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(User user) {
                // TODO: Confirm and delete user
                Toast.makeText(AdminUsersActivity.this, "Delete: " + user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewDetails(User user) {
                // TODO: Show user details
                Toast.makeText(AdminUsersActivity.this, "Details: " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        // Mock data - replace with actual API call
        userList.clear();
        userList.add(new User("1", "Amaya Perera", "amaya@example.com", "077-1234567", "Active"));
        userList.add(new User("2", "Sanduni Silva", "sanduni@example.com", "076-9876543", "Active"));
        userList.add(new User("3", "Dilini Fernando", "dilini@example.com", "071-5556789", "Inactive"));
        userList.add(new User("4", "Nimasha Jayasinghe", "nimasha@example.com", "072-4443332", "Active"));
        userAdapter.notifyDataSetChanged();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
