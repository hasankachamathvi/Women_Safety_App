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
    // Handles sign in and password reset with Firebase.
    private FirebaseAuthService authService;
    // Small loading popup shown while waiting for Firebase response.
    private ProgressDialog progressDialog;
    // Email input field.
    private EditText etUsername;
    // Password input field.
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start activity setup.
        super.onCreate(savedInstanceState);
        // Set the login screen layout
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth Service
        authService = new FirebaseAuthService();

        // Link Java variables to views from XML layout.
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
     * Read input, validate it, then try login with Firebase.
     */
    private void performLogin() {
        // Get text entered by user and remove extra spaces.
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

        // Show loading while Firebase checks credentials.
        showLoadingDialog("Logging in...");

        // Call Firebase authentication
        authService.loginUser(email, password, new FirebaseAuthService.OnAuthCallback() {
            @Override
            public void onSuccess(String message) {
                // Hide loading and show success message.
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                // Temporary admin check based on email text.
                // A role field from database is safer for real apps.
                if (email.contains("admin")) {
                    // Admin login - go to Admin Dashboard
                    Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    // Clear back stack so user cannot return to login with back button.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Regular user login - go to Home page
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // Clear back stack so user cannot return to login with back button.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                // Close login screen after navigation.
                finish();
            }

            @Override
            public void onError(String error) {
                // Hide loading and show reason if login fails.
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ask for email and send reset-password link.
     */
    private void showForgotPasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email to receive password reset link");

        // Input field inside the dialog.
        final EditText input = new EditText(this);
        builder.setView(input);

        // Send reset email when user taps Send.
        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                showLoadingDialog("Sending reset email...");
                authService.resetPassword(email, new FirebaseAuthService.OnAuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // Reset email sent successfully.
                        dismissLoadingDialog();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        // Failed to send reset email.
                        dismissLoadingDialog();
                        Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Prompt user to enter email before sending.
                Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
            }
        });

        // Close dialog without action.
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Show non-cancelable loading popup with a message.
     */
    private void showLoadingDialog(String message) {
        if (progressDialog == null) {
            // Create dialog once, then reuse it.
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * Hide loading popup if currently visible.
     */
    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
