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

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuthService authService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the register screen layout
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth Service
        authService = new FirebaseAuthService();

        // Find all input fields and buttons from the layout
        EditText etEmail = findViewById(R.id.et_contact);                 // Email input
        EditText etFullName = findViewById(R.id.et_full_name);              // Full name input
        EditText etPhone = findViewById(R.id.et_username);                // Phone input (repurposed)
        EditText etPassword = findViewById(R.id.et_password);               // Password input
        EditText etConfirmPassword = findViewById(R.id.et_confirm_password); // Confirm password input
        MaterialButton btnRegister = findViewById(R.id.btn_register);        // Register button
        TextView tvLoginLink = findViewById(R.id.tv_login_link);             // "Login" link text

        // Handle Register button click
        btnRegister.setOnClickListener(v -> performRegistration(
            etEmail.getText().toString().trim(),
            etFullName.getText().toString().trim(),
            etPhone.getText().toString().trim(),
            etPassword.getText().toString().trim(),
            etConfirmPassword.getText().toString().trim()
        ));

        // Handle "Login" link click - go back to Login page
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Perform user registration with Firebase
     */
    private void performRegistration(String email, String fullName, String phone,
                                     String password, String confirmPassword) {
        // Validate all fields
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading dialog
        showLoadingDialog("Creating account...");

        // Call Firebase registration
        authService.registerUser(email, password, fullName, phone,
            new FirebaseAuthService.OnAuthCallback() {
                @Override
                public void onSuccess(String message) {
                    dismissLoadingDialog();
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Go to home page after successful registration
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    dismissLoadingDialog();
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + error,
                        Toast.LENGTH_SHORT).show();
                }
            });
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
