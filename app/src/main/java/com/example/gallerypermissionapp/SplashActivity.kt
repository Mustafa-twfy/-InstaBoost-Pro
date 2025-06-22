package com.example.gallerypermissionapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var ivLogo: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var tvTagline: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        initializeViews()
        setupAnimations()
        navigateToLogin()
    }
    
    private fun initializeViews() {
        ivLogo = findViewById(R.id.ivLogo)
        tvAppName = findViewById(R.id.tvAppName)
        tvTagline = findViewById(R.id.tvTagline)
    }
    
    private fun setupAnimations() {
        // تحميل الرسوم المتحركة
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
        
        // تطبيق الرسوم المتحركة
        ivLogo.startAnimation(fadeIn)
        tvAppName.startAnimation(slideUp)
        tvTagline.startAnimation(slideUp)
        
        // تطبيق نبض على اللوقو
        Handler(Looper.getMainLooper()).postDelayed({
            ivLogo.startAnimation(pulse)
        }, 500)
    }
    
    private fun navigateToLogin() {
        // الانتقال لصفحة تسجيل الدخول بعد ثانيتين
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // ثانيتين
    }
} 