package com.example.yuwathi.activities;

// Android classes used by the login screen.
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

/**
 * Authenticates users and routes them by role.
 */
public class LoginActivity extends AppCompatActivity {
    // Service for login and password-reset operations.
    private FirebaseAuthService authService;
    // Loading dialog shown during network operations.
    private ProgressDialog progressDialog;
    // Email input field.
    private EditText etUsername;
    // Password input field.
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start activity and load the login layout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create authentication service instance.
        authService = new FirebaseAuthService();

        // Connect XML views to Java variables.
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        TextView tvRegister = findViewById(R.id.tv_register_link);
        TextView tvForgot = findViewById(R.id.tv_forgot_password);

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

            @Override
            public void onError(String error) {
                // Hide loading and show reason if login fails.
                dismissLoadingDialog();
                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show dialog to collect email and send reset link.
     */
    private void showForgotPasswordDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email to receive password reset link");

        // Add a simple input box to the dialog.
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
                                // Reset email sent.
                        dismissLoadingDialog();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                                // Reset email failed.
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
     * Show loading dialog with a custom message.
     */
    private void showLoadingDialog(String message) {
        if (progressDialog == null) {
            // Create once and reuse for later calls.
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
