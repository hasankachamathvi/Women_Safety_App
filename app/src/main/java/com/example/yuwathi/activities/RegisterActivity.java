package com.example.yuwathi.activities;

// Import required Android classes
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the register screen layout
        setContentView(R.layout.activity_register);

        // Find all input fields and buttons from the layout
        EditText etContact = findViewById(R.id.et_contact);                 // Email or phone input
        EditText etFullName = findViewById(R.id.et_full_name);              // Full name input
        EditText etUsername = findViewById(R.id.et_username);                // Username input
        EditText etPassword = findViewById(R.id.et_password);               // Password input
        EditText etConfirmPassword = findViewById(R.id.et_confirm_password); // Confirm password input
        MaterialButton btnRegister = findViewById(R.id.btn_register);        // Register button
        TextView tvLoginLink = findViewById(R.id.tv_login_link);             // "Login" link text

        // Handle Register button click
        btnRegister.setOnClickListener(v -> {
            /*

            String contact = etContact.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Check if any field is empty
            if (contact.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                return;
            }
            */

            // For now, just go to Home page without validation
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close register page
        });

        // Handle "Login" link click - go back to Login page
        tvLoginLink.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}
