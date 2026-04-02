package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yuwathi.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etUsername = findViewById(R.id.et_username); // This serves as email input
        EditText etPassword = findViewById(R.id.et_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        TextView tvRegister = findViewById(R.id.tv_register_link);
        TextView tvForgot = findViewById(R.id.tv_forgot_password);

        btnSignIn.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Login
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        
                        // Fetch user role from Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String role = documentSnapshot.getString("role");
                                    if ("admin".equals(role)) {
                                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(LoginActivity.this, "Error fetching user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        tvForgot.setOnClickListener(v -> {
            // Implement password reset if needed
        });
    }
}
