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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
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
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        btnShowPassword = findViewById(R.id.btnShowPassword)
        btnShowConfirmPassword = findViewById(R.id.btnShowConfirmPassword)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            performRegistration()
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
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "البريد الإلكتروني غير صحيح"
            return
        }

        if (password.length < 6) {
            etPassword.error = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "كلمة المرور غير متطابقة"
            return
        }

        // Show loading
        btnRegister.isEnabled = false
        btnRegister.text = "جاري إنشاء الحساب..."

        lifecycleScope.launch {
            try {
                val success = SupabaseManager.signUp(email, password)
                
                if (success) {
                    Toast.makeText(this@RegisterActivity, "تم إنشاء الحساب بنجاح! يمكنك الآن تسجيل الدخول.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "فشل إنشاء الحساب. قد يكون البريد الإلكتروني مستخدم بالفعل.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "خطأ في الاتصال: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnRegister.isEnabled = true
                btnRegister.text = getString(R.string.register)
            }
        }
    }
} 