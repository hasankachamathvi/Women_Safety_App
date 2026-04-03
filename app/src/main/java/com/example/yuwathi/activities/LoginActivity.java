package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Dual Login System - Separate Admin and User Authentication
 * Admin: Hardcoded credentials
 * Users: Firebase authentication
 */
public class LoginActivity extends AppCompatActivity {

    // Hardcoded Admin Credentials
    private static final String ADMIN_EMAIL = "admin@yuwathi.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout containerUserLogin;
    private LinearLayout containerAdminLogin;
    private MaterialButton btnTabUser;
    private MaterialButton btnTabAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupTabListeners();
        setupUserLogin();
        setupAdminLogin();
    }

    private void initializeViews() {
        containerUserLogin = findViewById(R.id.container_user_login);
        containerAdminLogin = findViewById(R.id.container_admin_login);
        btnTabUser = findViewById(R.id.btn_tab_user);
        btnTabAdmin = findViewById(R.id.btn_tab_admin);
    }

    private void setupTabListeners() {
        btnTabUser.setOnClickListener(v -> switchToUserLogin());
        btnTabAdmin.setOnClickListener(v -> switchToAdminLogin());
    }

    private void switchToUserLogin() {
        containerUserLogin.setVisibility(LinearLayout.VISIBLE);
        containerAdminLogin.setVisibility(LinearLayout.GONE);
        btnTabUser.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        btnTabUser.setTextColor(getResources().getColor(android.R.color.white));
        btnTabAdmin.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnTabAdmin.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void switchToAdminLogin() {
        containerUserLogin.setVisibility(LinearLayout.GONE);
        containerAdminLogin.setVisibility(LinearLayout.VISIBLE);
        btnTabAdmin.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        btnTabAdmin.setTextColor(getResources().getColor(android.R.color.white));
        btnTabUser.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnTabUser.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void setupUserLogin() {
        EditText etUserEmail = findViewById(R.id.et_user_email);
        EditText etUserPassword = findViewById(R.id.et_user_password);
        MaterialButton btnUserSignIn = findViewById(R.id.btn_user_sign_in);
        TextView tvUserRegisterLink = findViewById(R.id.tv_user_register_link);
        TextView tvUserForgotPassword = findViewById(R.id.tv_user_forgot_password);

        btnUserSignIn.setOnClickListener(v -> {
            String email = etUserEmail.getText().toString().trim();
            String password = etUserPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Login for Users
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        
                        // Fetch user role from Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String role = documentSnapshot.getString("role");

                                    // Ensure user doesn't access admin panel
                                    if ("admin".equalsIgnoreCase(role)) {
                                        Toast.makeText(LoginActivity.this, "Use Admin Login", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        return;
                                    }

                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        tvUserRegisterLink.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        tvUserForgotPassword.setOnClickListener(v -> {
            // Implement password reset if needed
            Toast.makeText(this, "Contact support for password reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAdminLogin() {
        EditText etAdminEmail = findViewById(R.id.et_admin_email);
        EditText etAdminPassword = findViewById(R.id.et_admin_password);
        MaterialButton btnAdminSignIn = findViewById(R.id.btn_admin_sign_in);

        btnAdminSignIn.setOnClickListener(v -> {
            String email = etAdminEmail.getText().toString().trim();
            String password = etAdminPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter admin email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify hardcoded admin credentials
            if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
                // Admin credentials correct - proceed to admin dashboard
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


