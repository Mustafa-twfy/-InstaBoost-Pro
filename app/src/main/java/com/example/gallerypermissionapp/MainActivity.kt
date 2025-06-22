package com.example.gallerypermissionapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnSelectImage: Button
    private lateinit var btnViewAllImages: Button
    private lateinit var btnSupervisor: Button
    private lateinit var imgPreview: ImageView
    private lateinit var tvPermissionStatus: TextView
    private lateinit var tvPermissionDetails: TextView
    
    private val requiredPermissions = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCount = permissions.values.count { it }
        updatePermissionStatus(grantedCount, permissions.size)
        
        if (grantedCount == permissions.size) {
            Toast.makeText(this, getString(R.string.permissions_granted_success), Toast.LENGTH_SHORT).show()
            btnViewAllImages.isEnabled = true
            btnSupervisor.isEnabled = true
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
    }
    
    private fun initializeViews() {
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnViewAllImages = findViewById(R.id.btnViewAllImages)
        btnSupervisor = findViewById(R.id.btnSupervisor)
        imgPreview = findViewById(R.id.imgPreview)
        tvPermissionStatus = findViewById(R.id.tvPermissionStatus)
        tvPermissionDetails = findViewById(R.id.tvPermissionDetails)
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
    }
    
    private fun requestPermissions() {
        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isEmpty()) {
            Toast.makeText(this, getString(R.string.permissions_granted_success), Toast.LENGTH_SHORT).show()
            btnViewAllImages.isEnabled = true
            btnSupervisor.isEnabled = true
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
