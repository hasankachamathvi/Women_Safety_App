package com.example.yuwathi.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yuwathi.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.ivShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.ivShowConfirmPassword.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }
    }

    private fun handleRegistration() {
        val contact = binding.etContact.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (contact.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6 || !password.any { it.isDigit() }) {
            binding.etPassword.error = "Must contain a number and least of 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return
        }

        // TODO: Implement actual registration logic (e.g., API call or Database)
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        
        // Navigate to Home
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.etPassword.transformationMethod = null
            binding.ivShowPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel) // Example icon
        } else {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivShowPassword.setImageResource(android.R.drawable.ic_menu_view)
        }
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        if (isConfirmPasswordVisible) {
            binding.etConfirmPassword.transformationMethod = null
            binding.ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            binding.etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view)
        }
        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
    }
}
