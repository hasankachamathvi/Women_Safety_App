package com.example.yuwathi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yuwathi.R;
import com.example.yuwathi.utils.AdminSessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // Optional fallback admin credentials (same form as normal login)
    private static final String ADMIN_EMAIL = "admin@yuwathi.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etEmail = findViewById(R.id.et_user_email);
        EditText etPassword = findViewById(R.id.et_user_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_user_sign_in);
        TextView tvRegister = findViewById(R.id.tv_user_register_link);
        TextView tvForgot = findViewById(R.id.tv_user_forgot_password);

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
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
