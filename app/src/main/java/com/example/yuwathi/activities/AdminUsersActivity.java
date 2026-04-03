package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yuwathi.R;
import com.example.yuwathi.adapters.UserAdapter;
import com.example.yuwathi.models.User;
import com.example.yuwathi.services.FirebaseFirestoreService;
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

    private FirebaseFirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        // Initialize Firestore service
        firestoreService = FirebaseFirestoreService.getInstance();

        // Set up toolbar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Management");
        }

        setupAdminBackButton();
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
            Toast.makeText(this, "Add User - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                Toast.makeText(AdminUsersActivity.this, "Edit: " + user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(User user) {
                deleteUser(user.getId(), user.getName());
            }

            @Override
            public void onViewDetails(User user) {
                Toast.makeText(AdminUsersActivity.this, "Details: " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        firestoreService.getAllUsers(new FirebaseFirestoreService.OnUsersListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                userAdapter.setUsers(users);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminUsersActivity.this, "Failed to load users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(String userId, String userName) {
        firestoreService.deleteUser(userId, new FirebaseFirestoreService.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminUsersActivity.this, userName + " deleted successfully", Toast.LENGTH_SHORT).show();
                loadUsers();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminUsersActivity.this, "Failed to delete: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
        navigateToDashboard();
        return true;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }
}
