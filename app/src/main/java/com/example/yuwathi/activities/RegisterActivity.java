package com.example.yuwathi.activities;

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
        setContentView(R.layout.activity_register);

        // Initialize views
        EditText etContact = findViewById(R.id.et_contact);
        EditText etFullName = findViewById(R.id.et_full_name);
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        EditText etConfirmPassword = findViewById(R.id.et_confirm_password);
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        TextView tvLoginLink = findViewById(R.id.tv_login_link);

        // Register logic commented out
        btnRegister.setOnClickListener(v -> {
            /*
            String contact = etContact.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (contact.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                return;
            }

            if (!password.equals(confirmPassword)) {
                return;
            }
            */

            // Navigation kept for UI flow
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Navigate to Login screen
        tvLoginLink.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}
