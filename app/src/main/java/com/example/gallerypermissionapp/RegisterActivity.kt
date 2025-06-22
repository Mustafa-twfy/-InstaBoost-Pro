package com.example.gallerypermissionapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBack: android.widget.ImageView
    private lateinit var tvLogin: TextView
    private lateinit var btnShowPassword: android.widget.ImageView
    private lateinit var btnShowConfirmPassword: android.widget.ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)
        tvLogin = findViewById(R.id.tvBackToLogin)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        btnShowConfirmPassword = findViewById(R.id.btnShowConfirmPassword)
        
        // تأكد من أن جميع الأزرار قابلة للنقر
        btnBack.isClickable = true
        btnBack.isFocusable = true
        tvLogin.isClickable = true
        tvLogin.isFocusable = true
        btnShowPassword.isClickable = true
        btnShowPassword.isFocusable = true
        btnShowConfirmPassword.isClickable = true
        btnShowConfirmPassword.isFocusable = true
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            performRegistration()
        }

        btnBack.setOnClickListener {
            finish()
        }

        tvLogin.setOnClickListener {
            finish()
        }
        
        btnShowPassword.setOnClickListener {
            togglePasswordVisibility(etPassword, btnShowPassword)
        }
        
        btnShowConfirmPassword.setOnClickListener {
            togglePasswordVisibility(etConfirmPassword, btnShowConfirmPassword)
        }
    }
    
    private fun togglePasswordVisibility(editText: EditText, button: android.widget.ImageView) {
        if (editText.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT
            button.setImageResource(R.drawable.ic_eye_open)
        } else {
            editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.ic_eye_closed)
        }
        editText.setSelection(editText.text.length)
    }

    private fun performRegistration() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_email_invalid)
            return
        }

        if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_short)
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = getString(R.string.error_passwords_not_match)
            return
        }

        // Simplified registration for testing - always succeeds
        Toast.makeText(this@RegisterActivity, getString(R.string.success_register), Toast.LENGTH_LONG).show()
        
        // Go back to login screen after successful registration
        finish()
    }
} 