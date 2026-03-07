package com.example.yuwathi.activities;

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
        setContentView(R.layout.activity_login);

        // Initialize views
        EditText etUsername = findViewById(R.id.et_username);
        EditText etPassword = findViewById(R.id.et_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        TextView tvRegister = findViewById(R.id.tv_register_link);
        TextView tvForgot = findViewById(R.id.tv_forgot_password);

        // Sign In logic commented out
        btnSignIn.setOnClickListener(v -> {
            /*
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                return;
            }
            */

            // Still keeping navigation so you can see the next UI
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Navigate to Register screen
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // Forgot password logic commented out
        tvForgot.setOnClickListener(v -> {
            // Logic for forgot password
        });
    }
}
