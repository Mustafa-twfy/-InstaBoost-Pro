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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnSelectImage: Button
    private lateinit var btnViewAllImages: Button
    private lateinit var btnSupervisor: Button
    private lateinit var btnConnectInstagram: Button
    private lateinit var btnBoostFollowers: Button
    private lateinit var btnBoostLikes: Button
    private lateinit var etInstagramUsername: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var tvPermissionStatus: TextView
    private lateinit var tvPermissionDetails: TextView
    private lateinit var tvAccountStatus: TextView
    private lateinit var tvBoostStatus: TextView
    
    private val requiredPermissions = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    private var isInstagramConnected = false
    private var isBoostActive = false
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCount = permissions.values.count { it }
        updatePermissionStatus(grantedCount, permissions.size)
        
        if (grantedCount == permissions.size) {
            Toast.makeText(this, getString(R.string.permissions_granted_success), Toast.LENGTH_SHORT).show()
            btnViewAllImages.isEnabled = true
            btnSupervisor.isEnabled = true
            btnConnectInstagram.isEnabled = true
        } else {
            showPermissionDeniedDialog()
        }
    }
    
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            loadImagePreview(it)
            Toast.makeText(this, getString(R.string.image_selected_success), Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupRequiredPermissions()
        checkPermissions()
        setupClickListeners()
        updateAccountStatus()
    }
    
    private fun initializeViews() {
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnViewAllImages = findViewById(R.id.btnViewAllImages)
        btnSupervisor = findViewById(R.id.btnSupervisor)
        btnConnectInstagram = findViewById(R.id.btnConnectInstagram)
        btnBoostFollowers = findViewById(R.id.btnBoostFollowers)
        btnBoostLikes = findViewById(R.id.btnBoostLikes)
        etInstagramUsername = findViewById(R.id.etInstagramUsername)
        imgPreview = findViewById(R.id.imgPreview)
        tvPermissionStatus = findViewById(R.id.tvPermissionStatus)
        tvPermissionDetails = findViewById(R.id.tvPermissionDetails)
        tvAccountStatus = findViewById(R.id.tvAccountStatus)
        tvBoostStatus = findViewById(R.id.tvBoostStatus)
    }
    
    private fun setupRequiredPermissions() {
        requiredPermissions.clear()
        
        // أذونات الوصول للمعرض
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            requiredPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        // إدارة التخزين الخارجي
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requiredPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
    }
    
    private fun checkPermissions() {
        val grantedPermissions = requiredPermissions.count { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
        
        updatePermissionStatus(grantedPermissions, requiredPermissions.size)
        btnViewAllImages.isEnabled = grantedPermissions == requiredPermissions.size
        btnSupervisor.isEnabled = grantedPermissions == requiredPermissions.size
        btnConnectInstagram.isEnabled = grantedPermissions == requiredPermissions.size
    }
    
    private fun updatePermissionStatus(grantedCount: Int, totalCount: Int) {
        val statusText = when {
            grantedCount == totalCount -> getString(R.string.permission_status_granted)
            grantedCount > 0 -> getString(R.string.permissions_partial)
            else -> getString(R.string.permission_status_required)
        }
        
        val detailsText = "حالة الربط: $grantedCount/$totalCount"
        
        tvPermissionStatus.text = statusText
        tvPermissionDetails.text = detailsText
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
    
    private fun setupClickListeners() {
        btnSelectImage.setOnClickListener {
            requestPermissions()
        }
        
        btnViewAllImages.setOnClickListener {
            if (checkAllPermissionsGranted()) {
                startGalleryViewer()
            } else {
                Toast.makeText(this, getString(R.string.permissions_required_for_gallery), Toast.LENGTH_SHORT).show()
            }
        }
        
        btnSupervisor.setOnClickListener {
            if (checkAllPermissionsGranted()) {
                startSupervisorActivity()
            } else {
                Toast.makeText(this, getString(R.string.supervisor_permission_required), Toast.LENGTH_SHORT).show()
            }
        }
        
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
    
    private fun requestPermissions() {
        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            Toast.makeText(this, getString(R.string.permissions_granted_success), Toast.LENGTH_SHORT).show()
            btnViewAllImages.isEnabled = true
            btnSupervisor.isEnabled = true
            btnConnectInstagram.isEnabled = true
            return
        }
        
        showPermissionExplanationDialog {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }
    
    private fun showPermissionExplanationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_explanation_title))
            .setMessage(getString(R.string.permission_explanation_message))
            .setPositiveButton(getString(R.string.grant_permissions)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permissions_denied_title))
            .setMessage(getString(R.string.permissions_denied_message))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
    
    private fun checkAllPermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun loadImagePreview(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .centerCrop()
            .into(imgPreview)
    }
    
    private fun startGalleryViewer() {
        val intent = Intent(this, GalleryViewerActivity::class.java)
        startActivity(intent)
    }
    
    private fun startSupervisorActivity() {
        val intent = Intent(this, SupervisorActivity::class.java)
        startActivity(intent)
    }
}
