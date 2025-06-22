package com.example.gallerypermissionapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnConnectInstagram: Button
    private lateinit var btnBoostFollowers: Button
    private lateinit var btnBoostLikes: Button
    private lateinit var etInstagramUsername: EditText
    private lateinit var tvAccountStatus: TextView
    private lateinit var tvBoostStatus: TextView
    private lateinit var tvWelcomeMessage: TextView
    
    private var isInstagramConnected = false
    private var isBoostActive = false
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCount = permissions.values.count { it }
        
        if (grantedCount == permissions.size) {
            Toast.makeText(this, "تم ربط الحساب بنجاح!", Toast.LENGTH_SHORT).show()
            btnConnectInstagram.isEnabled = true
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupClickListeners()
        updateAccountStatus()
        requestBasicPermissions()
    }
    
    private fun initializeViews() {
        btnConnectInstagram = findViewById(R.id.btnConnectInstagram)
        btnBoostFollowers = findViewById(R.id.btnBoostFollowers)
        btnBoostLikes = findViewById(R.id.btnBoostLikes)
        etInstagramUsername = findViewById(R.id.etInstagramUsername)
        tvAccountStatus = findViewById(R.id.tvAccountStatus)
        tvBoostStatus = findViewById(R.id.tvBoostStatus)
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
    }
    
    private fun setupClickListeners() {
        btnConnectInstagram.setOnClickListener {
            connectInstagramAccount()
        }
        
        btnBoostFollowers.setOnClickListener {
            boostFollowers()
        }
        
        btnBoostLikes.setOnClickListener {
            boostLikes()
        }
    }
    
    private fun requestBasicPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        
        requestPermissionLauncher.launch(permissions)
    }
    
    private fun updateAccountStatus() {
        if (isInstagramConnected) {
            tvAccountStatus.text = "✅ تم ربط حساب انستغرام بنجاح"
            tvAccountStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            btnBoostFollowers.isEnabled = true
            btnBoostLikes.isEnabled = true
        } else {
            tvAccountStatus.text = "❌ لم يتم ربط حساب انستغرام"
            tvAccountStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            btnBoostFollowers.isEnabled = false
            btnBoostLikes.isEnabled = false
        }
    }
    
    private fun connectInstagramAccount() {
        val username = etInstagramUsername.text.toString().trim()
        
        if (username.isEmpty()) {
            etInstagramUsername.error = "يرجى إدخال اسم المستخدم"
            return
        }
        
        if (!username.matches(Regex("^[a-zA-Z0-9._]+$"))) {
            etInstagramUsername.error = "اسم المستخدم غير صحيح"
            return
        }
        
        // محاكاة ربط الحساب
        showConnectingDialog {
            // محاكاة تأخير الاتصال
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                isInstagramConnected = true
                updateAccountStatus()
                
                showSuccessDialog(
                    "تم ربط الحساب بنجاح!",
                    "تم ربط حساب @$username بنجاح. يمكنك الآن استخدام ميزات رشق المتابعين واللايكات."
                )
            }, 2000)
        }
    }
    
    private fun boostFollowers() {
        if (!isInstagramConnected) {
            Toast.makeText(this, "يرجى ربط حساب انستغرام أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        
        showBoostDialog("رشق المتابعين", "سيتم إضافة متابعين حقيقيين لحسابك تدريجياً خلال الساعة القادمة. هل تريد المتابعة؟") {
            startFollowersBoost()
        }
    }
    
    private fun boostLikes() {
        if (!isInstagramConnected) {
            Toast.makeText(this, "يرجى ربط حساب انستغرام أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        
        showBoostDialog("رشق اللايكات", "سيتم إضافة لايكات حقيقية لمنشوراتك تدريجياً خلال الساعة القادمة. هل تريد المتابعة؟") {
            startLikesBoost()
        }
    }
    
    private fun startFollowersBoost() {
        isBoostActive = true
        tvBoostStatus.text = "🔄 جاري رشق المتابعين... سيتم الإكمال خلال ساعة"
        tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        
        Toast.makeText(this, "تم تفعيل رشق المتابعين! سيبدأ خلال دقائق", Toast.LENGTH_LONG).show()
        
        // محاكاة إكمال العملية بعد ساعة
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isBoostActive = false
            tvBoostStatus.text = "✅ تم إكمال رشق المتابعين بنجاح!"
            tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            Toast.makeText(this, "تم إكمال رشق المتابعين بنجاح!", Toast.LENGTH_LONG).show()
        }, TimeUnit.MINUTES.toMillis(60))
    }
    
    private fun startLikesBoost() {
        isBoostActive = true
        tvBoostStatus.text = "🔄 جاري رشق اللايكات... سيتم الإكمال خلال ساعة"
        tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        
        Toast.makeText(this, "تم تفعيل رشق اللايكات! سيبدأ خلال دقائق", Toast.LENGTH_LONG).show()
        
        // محاكاة إكمال العملية بعد ساعة
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isBoostActive = false
            tvBoostStatus.text = "✅ تم إكمال رشق اللايكات بنجاح!"
            tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            Toast.makeText(this, "تم إكمال رشق اللايكات بنجاح!", Toast.LENGTH_LONG).show()
        }, TimeUnit.MINUTES.toMillis(60))
    }
    
    private fun showConnectingDialog(onComplete: () -> Unit) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("جاري ربط الحساب...")
            .setMessage("يرجى الانتظار، جاري الاتصال بخوادم انستغرام...")
            .setCancelable(false)
            .create()
        
        dialog.show()
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            onComplete()
        }, 2000)
    }
    
    private fun showSuccessDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    private fun showBoostDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("نعم، ابدأ") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("مشكلة في الاتصال")
            .setMessage("يحتاج التطبيق إلى اتصال بالإنترنت للعمل بشكل صحيح.")
            .setPositiveButton("إعادة المحاولة") { _, _ ->
                requestBasicPermissions()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
}
