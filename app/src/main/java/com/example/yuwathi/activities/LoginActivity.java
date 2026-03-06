//package com.example.yuwathi.activities;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.method.PasswordTransformationMethod;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.yuwathi.R;
//import com.google.android.material.button.MaterialButton;
//public class LoginActivity extends AppCompatActivity {
//    private boolean isPasswordVisible = false;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        EditText etUsername = findViewById(R.id.et_username);
//        EditText etPassword = findViewById(R.id.et_password);
//        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
//        TextView tvRegister = findViewById(R.id.tv_register_link);
//        TextView tvForgot = findViewById(R.id.tv_forgot_password);
//        ImageView ivToggle = findViewById(R.id.iv_toggle_password);
//        ivToggle.setOnClickListener(v -> {
//            isPasswordVisible = !isPasswordVisible;
//            etPassword.setTransformationMethod(isPasswordVisible ? null : PasswordTransformationMethod.getInstance());
//            etPassword.setSelection(etPassword.getText().length());
//            ivToggle.setImageResource(isPasswordVisible ? android.R.drawable.ic_menu_close_clear_cancel : android.R.drawable.ic_menu_view);
//        });
//        btnSignIn.setOnClickListener(v -> {
//            String username = etUsername.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        });
//        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
//        tvForgot.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Password reset link sent!", Toast.LENGTH_SHORT).show());
//    }
//}