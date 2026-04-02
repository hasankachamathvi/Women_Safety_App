package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import com.example.yuwathi.services.FirebaseAuthService;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuthService authService;
    private ProgressDialog progressDialog;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the login screen layout
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth Service
        authService = new FirebaseAuthService();

        // Find all input fields and buttons from the layout
        etUsername = findViewById(R.id.et_username);       // Email input field
        etPassword = findViewById(R.id.et_password);       // Password input field
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);  // Sign In button
        TextView tvRegister = findViewById(R.id.tv_register_link);  // "Register" link text
        TextView tvForgot = findViewById(R.id.tv_forgot_password);  // "Forgot password" link text

        // Handle Sign In button click
        btnSignIn.setOnClickListener(v -> performLogin());

        // Handle "Register" link click - go to Register page
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // Handle "Forgot password" link click
        tvForgot.setOnClickListener(v -> showForgotPasswordDialog());
    }

    /**
     * Perform login with Firebase authentication
     */
    private void performLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
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

        // Show loading dialog
        showLoadingDialog("Logging in...");

        // Call Firebase authentication
        authService.loginUser(email, password, new FirebaseAuthService.OnAuthCallback() {
            @Override
            public void onSuccess(String message) {
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                // Check if admin login (using specific email/role check from Firestore would be better)
                if (email.contains("admin")) {
                    // Admin login - go to Admin Dashboard
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Regular user login - go to Home page
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onError(String error) {
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show forgot password dialog
     */
    private void showForgotPasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email to receive password reset link");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                showLoadingDialog("Sending reset email...");
                authService.resetPassword(email, new FirebaseAuthService.OnAuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        dismissLoadingDialog();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        dismissLoadingDialog();
                        Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Show loading dialog
     */
    private void showLoadingDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * Dismiss loading dialog
     */
    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
