package com.example.gallerypermissionapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var etEmail: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var tvBackToLogin: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
    }
    
    private fun setupClickListeners() {
        btnResetPassword.setOnClickListener {
            performPasswordReset()
        }
        
        tvBackToLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun performPasswordReset() {
        val email = etEmail.text.toString().trim()
        
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.error_email_required)
            return
        }
        
        if (!isValidEmail(email)) {
            etEmail.error = getString(R.string.error_email_invalid)
            return
        }
        
        // محاكاة إرسال رابط إعادة تعيين كلمة المرور
        Toast.makeText(this, getString(R.string.success_reset_password), Toast.LENGTH_LONG).show()
        
        // العودة لصفحة تسجيل الدخول
        finish()
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
} 