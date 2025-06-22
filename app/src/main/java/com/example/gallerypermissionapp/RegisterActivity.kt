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

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvBackToLogin)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            performRegistration()
        }

        tvLogin.setOnClickListener {
            // Finish current activity and go back to login
            finish()
        }
    }

    private fun performRegistration() {
        val fullName = findViewById<EditText>(R.id.etFullName).text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString().trim()

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
            findViewById<EditText>(R.id.etConfirmPassword).error = getString(R.string.error_passwords_not_match)
            return
        }

        // Simplified registration for testing - always succeeds
        Toast.makeText(this@RegisterActivity, getString(R.string.success_register), Toast.LENGTH_LONG).show()
        
        // Go back to login screen after successful registration
        finish()
    }
} 