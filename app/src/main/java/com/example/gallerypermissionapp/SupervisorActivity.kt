package com.example.gallerypermissionapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class SupervisorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var tvProgress: TextView
    private lateinit var tvImageCount: TextView
    private lateinit var tvTotalSize: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnRefresh: TextView

    private val imageUrls = mutableListOf<String>()
    private lateinit var adapter: SupervisorImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor)

        initializeViews()
        setupRecyclerView()
        loadImagesFromSupabase()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        loadingLayout = findViewById(R.id.loadingLayout)
        tvProgress = findViewById(R.id.tvProgress)
        tvImageCount = findViewById(R.id.tvImageCount)
        tvTotalSize = findViewById(R.id.tvTotalSize)
        btnBack = findViewById(R.id.btnBack)
        btnRefresh = findViewById(R.id.btnRefresh)

        btnBack.setOnClickListener {
            finish()
        }

        btnRefresh.setOnClickListener {
            refreshImages()
        }
    }

    private fun setupRecyclerView() {
        adapter = SupervisorImageAdapter(imageUrls)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    private fun loadImagesFromSupabase() {
        showLoading("جاري تحميل الصور من الخادم...")

        // تحميل الصور الحقيقية من الجهاز
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                // قراءة مسارات الصور من SharedPreferences
                val sharedPrefs = getSharedPreferences("SupervisorData", MODE_PRIVATE)
                val imagePathsSet = sharedPrefs.getStringSet("uploaded_images", setOf())
                val uploadTime = sharedPrefs.getLong("upload_time", 0)
                
                if (imagePathsSet != null && imagePathsSet.isNotEmpty()) {
                    val imagePaths = imagePathsSet.toList()
                    
                    // تحويل مسارات الملفات إلى URIs للعرض
                    val imageUris = imagePaths.mapNotNull { path ->
                        try {
                            val file = File(path)
                            if (file.exists()) {
                                android.net.Uri.fromFile(file)
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (imageUris.isNotEmpty()) {
                        imageUrls.clear()
                        imageUrls.addAll(imageUris.map { it.toString() })
                        adapter.notifyDataSetChanged()
                        hideLoading()
                        updateImageCount(imageUris.size)
                        
                        // إظهار وقت الرفع
                        val uploadDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(uploadTime))
                        
                        Toast.makeText(this@SupervisorActivity, "تم تحميل ${imageUris.size} صورة من الجهاز (مرفوعة في: $uploadDate)", Toast.LENGTH_LONG).show()
                    } else {
                        showStatus("لم يتم العثور على صور صالحة في الجهاز.")
                        updateImageCount(0)
                    }
                } else {
                    showStatus("لا توجد صور مرفوعة حتى الآن.")
                    updateImageCount(0)
                }
                
            } catch (exception: Exception) {
                showError("فشل تحميل الصور: ${exception.message}")
                Toast.makeText(this@SupervisorActivity, "خطأ: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }, 2000) // تأخير لمحاكاة التحميل
    }

    private fun refreshImages() {
        loadImagesFromSupabase()
    }

    private fun updateImageCount(count: Int) {
        tvImageCount.text = count.toString()
        tvTotalSize.text = "${(count * 2.5).toInt()} MB"
    }

    private fun showLoading(message: String) {
        loadingLayout.visibility = View.VISIBLE
        tvProgress.text = message
        recyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showStatus(message: String) {
        loadingLayout.visibility = View.VISIBLE
        tvProgress.text = message
        recyclerView.visibility = View.GONE
    }

    private fun showError(message: String) {
        loadingLayout.visibility = View.VISIBLE
        tvProgress.text = message
        recyclerView.visibility = View.GONE
    }
}

class SupervisorImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<SupervisorImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_supervisor_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgItem)

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(imageView)
        }
    }
} 