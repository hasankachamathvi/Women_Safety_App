package com.example.yuwathi.activities;

// Android classes used by the login screen.
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import com.example.yuwathi.services.FirebaseAuthService;

/**
 * Authenticates users and routes them by role.
 */
public class LoginActivity extends AppCompatActivity {

    // Optional fallback admin credentials (same form as normal login)
    private static final String ADMIN_EMAIL = "admin@yuwathi.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start activity and load the login layout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create authentication service instance.
        authService = new FirebaseAuthService();

        // Connect XML views to Java variables.
        etUsername = findViewById(R.id.et_user_email);
        etPassword = findViewById(R.id.et_user_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_user_sign_in);
        TextView tvRegister = findViewById(R.id.tv_user_register_link);
        TextView tvForgot = findViewById(R.id.tv_user_forgot_password);

        // Try login when user taps Sign In.
        btnSignIn.setOnClickListener(v -> performLogin());

        // Open register screen.
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // Open forgot-password dialog.
        tvForgot.setOnClickListener(v -> showForgotPasswordDialog());
    }

    /**
     * Read input, validate it, then try login with Firebase.
     */
    private void performLogin() {
        // Get text entered by user and remove extra spaces.
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate basic input first.
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading while checking credentials.
        showLoadingDialog("Logging in...");

        // Request login from Firebase.
        authService.loginUser(email, password, new FirebaseAuthService.OnAuthCallback() {
            @Override
            public void onSuccess(String message) {
                // Hide loading and show success message.
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                // Temporary admin check using email text.
                // In production, check role/claims from backend.
                if (email.contains("admin")) {
                    // Admin user goes to admin dashboard.
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    // Clear back stack so user cannot return to login with back button.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Regular user goes to home screen.
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // Clear back stack so user cannot return to login with back button.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // Close login screen after navigation.
                finish();
            }

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

            // 1) Try Firebase login first (normal path for both user/admin accounts stored in Firebase)
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            AdminSessionManager.clearSession(LoginActivity.this);
                            String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                            if (uid == null) {
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        String role = documentSnapshot.exists() ? documentSnapshot.getString("role") : null;
                                        if ("admin".equalsIgnoreCase(role)) {
                                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                        } else {
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        }
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Could not read user role", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        // 2) Fallback: local hardcoded admin credentials
                        if (ADMIN_EMAIL.equalsIgnoreCase(email) && ADMIN_PASSWORD.equals(password)) {
                            AdminSessionManager.startAdminSession(LoginActivity.this);
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvForgot.setOnClickListener(v -> Toast.makeText(this, "Contact support for password reset", Toast.LENGTH_SHORT).show());
    }
}
