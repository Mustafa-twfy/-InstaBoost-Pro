package com.example.gallerypermissionapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class GalleryViewerActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var tvProgress: TextView
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: ImageView
    
    private val imageAdapter = ImageAdapter()
    private val imageList = mutableListOf<ImageItem>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_viewer)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadImages()
    }
    
    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        loadingLayout = findViewById(R.id.loadingLayout)
        tvProgress = findViewById(R.id.tvProgress)
        tvImageCount = findViewById(R.id.tvImageCount)
        btnBack = findViewById(R.id.btnBack)
    }
    
    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = imageAdapter
    }
    
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadImages() {
        if (!checkPermissions()) {
            tvProgress.text = "لا يمكن تحليل المحتوى بدون الأذونات المطلوبة"
            return
        }
        
        loadingLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvProgress.text = "جاري تحليل المحتوى..."
        
        try {
            val imagePaths = loadAllImagePaths()
            
            imageList.clear()
            imageList.addAll(imagePaths.map { ImageItem(it) })
            
            imageAdapter.updateImages(imageList)
            loadingLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            tvImageCount.text = imageList.size.toString()
            
        } catch (e: Exception) {
            tvProgress.text = "حدث خطأ أثناء تحليل المحتوى: ${e.message}"
        }
    }
    
    private fun loadAllImagePaths(): List<String> {
        val images = mutableListOf<String>()
        
        try {
            val projection = arrayOf(
                android.provider.MediaStore.Images.Media.DATA
            )
            
            val selection = "${android.provider.MediaStore.Images.Media.MIME_TYPE} LIKE ?"
            val selectionArgs = arrayOf("image/%")
            val sortOrder = "${android.provider.MediaStore.Images.Media.DATE_ADDED} DESC"
            
            contentResolver.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val dataColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
                
                while (cursor.moveToNext()) {
                    val imagePath = cursor.getString(dataColumn)
                    images.add(imagePath)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return images
    }
    
    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    inner class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
        
        private val images = mutableListOf<ImageItem>()
        
        fun updateImages(newImages: List<ImageItem>) {
            images.clear()
            images.addAll(newImages)
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.bind(images[position])
        }
        
        override fun getItemCount(): Int = images.size
        
        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imgItem)
            private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
            private val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
            
            fun bind(imageItem: ImageItem) {
                Glide.with(itemView.context)
                    .load(imageItem.path)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(imageView)
                
                when (imageItem.status) {
                    ImageItem.UploadStatus.IDLE -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.GONE
                    }
                    ImageItem.UploadStatus.UPLOADING -> {
                        progressBar.visibility = View.VISIBLE
                        statusIndicator.visibility = View.VISIBLE
                    }
                    ImageItem.UploadStatus.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.GONE
                    }
                    ImageItem.UploadStatus.FAILED -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    
    data class ImageItem(
        val path: String,
        var status: UploadStatus = UploadStatus.IDLE
    ) {
        enum class UploadStatus {
            IDLE, UPLOADING, SUCCESS, FAILED
        }
    }
} 