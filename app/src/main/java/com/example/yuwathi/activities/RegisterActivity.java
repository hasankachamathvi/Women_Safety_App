//package com.example.yuwathi.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.method.PasswordTransformationMethod;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.yuwathi.R;
//import com.google.android.material.button.MaterialButton;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private boolean isPasswordVisible = false;
//    private boolean isConfirmPasswordVisible = false;
//
//    private EditText etFullName;
//    private EditText etContact;
//    private EditText etUsername;
//    private EditText etPassword;
//    private EditText etConfirmPassword;
//    private ImageView ivShowPassword;
//    private ImageView ivShowConfirmPassword;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        etFullName = findViewById(R.id.et_full_name);
//        etContact = findViewById(R.id.et_contact);
//        etUsername = findViewById(R.id.et_username);
//        etPassword = findViewById(R.id.et_password);
//        etConfirmPassword = findViewById(R.id.et_confirm_password);
//        ivShowPassword = findViewById(R.id.iv_show_password);
//        ivShowConfirmPassword = findViewById(R.id.iv_show_confirm_password);
//
//        MaterialButton btnRegister = findViewById(R.id.btn_register);
//        TextView tvLoginLink = findViewById(R.id.tv_login_link);
//
//        setupListeners(btnRegister, tvLoginLink);
//    }
//
//    private void setupListeners(MaterialButton btnRegister, TextView tvLoginLink) {
//        btnRegister.setOnClickListener(v -> handleRegistration());
//
//        tvLoginLink.setOnClickListener(v -> {
//            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//            finish();
//        });
//
//        ivShowPassword.setOnClickListener(v -> togglePasswordVisibility());
//
//        ivShowConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());
//    }
//
//    private void handleRegistration() {
//        String contact = etContact.getText().toString().trim();
//        String fullName = etFullName.getText().toString().trim();
//        String username = etUsername.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//        String confirmPassword = etConfirmPassword.getText().toString().trim();
//
//        if (contact.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (password.length() < 6 || !password.matches(".*\\d.*")) {
//            etPassword.setError("Must contain a number and least of 6 characters");
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            etConfirmPassword.setError("Passwords do not match");
//            return;
//        }
//
//        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void togglePasswordVisibility() {
//        isPasswordVisible = !isPasswordVisible;
//        if (isPasswordVisible) {
//            etPassword.setTransformationMethod(null);
//            ivShowPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//        } else {
//            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            ivShowPassword.setImageResource(android.R.drawable.ic_menu_view);
//        }
//        etPassword.setSelection(etPassword.getText().length());
//    }
//
//    private void toggleConfirmPasswordVisibility() {
//        isConfirmPasswordVisible = !isConfirmPasswordVisible;
//        if (isConfirmPasswordVisible) {
//            etConfirmPassword.setTransformationMethod(null);
//            ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//        } else {
//            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
//        }
//        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
//    }
//}
