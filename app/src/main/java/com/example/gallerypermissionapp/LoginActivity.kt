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
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var btnShowPassword: android.widget.ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvCreateAccount)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        
        // تأكد من أن جميع الأزرار قابلة للنقر
        tvRegister.isClickable = true
        tvRegister.isFocusable = true
        tvForgotPassword.isClickable = true
        tvForgotPassword.isFocusable = true
        btnShowPassword.isClickable = true
        btnShowPassword.isFocusable = true
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }

        tvRegister.setOnClickListener {
            // الانتقال لصفحة إنشاء الحساب
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        
        btnShowPassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        if (etPassword.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT
            btnShowPassword.setImageResource(R.drawable.ic_eye_open)
        } else {
            etPassword.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnShowPassword.setImageResource(R.drawable.ic_eye_closed)
        }
        etPassword.setSelection(etPassword.text.length)
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_email_invalid)
            return
        }

        // Check for supervisor login
        if (email == "admin@instaboost.com" && password == "admin123") {
            Toast.makeText(this@LoginActivity, "مرحباً بك في لوحة المشرف", Toast.LENGTH_SHORT).show()
            startSupervisorActivity()
            return
        }

        // Show loading
        btnLogin.isEnabled = false
        btnLogin.text = "جاري تسجيل الدخول..."

        lifecycleScope.launch {
            try {
                val success = SupabaseManager.signIn(email, password)
                
                if (success) {
                    Toast.makeText(this@LoginActivity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                    startMainActivity()
                } else {
                    Toast.makeText(this@LoginActivity, "فشل تسجيل الدخول. تحقق من البريد الإلكتروني وكلمة المرور.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "خطأ في الاتصال: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnLogin.isEnabled = true
                btnLogin.text = getString(R.string.login)
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun startSupervisorActivity() {
        val intent = Intent(this, SupervisorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 