package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the login screen layout
        setContentView(R.layout.activity_login);

        // Find all input fields and buttons from the layout
        EditText etUsername = findViewById(R.id.et_username);       // Username input field
        EditText etPassword = findViewById(R.id.et_password);       // Password input field
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);  // Sign In button
        TextView tvRegister = findViewById(R.id.tv_register_link);  // "Register" link text
        TextView tvForgot = findViewById(R.id.tv_forgot_password);  // "Forgot password" link text

        // Handle Sign In button click
        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Check if fields are empty
            if (username.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(LoginActivity.this, 
                    "Please enter username and password", 
                    android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for admin credentials
            // TODO: Replace with actual API authentication
            if (username.equals("admin") && password.equals("admin123")) {
                // Admin login - go to Admin Dashboard
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Regular user login - go to Home page
                // In production, validate credentials via API first
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Handle "Register" link click - go to Register page
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // Handle "Forgot password" link click
        tvForgot.setOnClickListener(v -> {
        });
    }
}
