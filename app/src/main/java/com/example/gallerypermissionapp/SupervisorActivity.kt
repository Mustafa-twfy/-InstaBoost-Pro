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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

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
        showLoading("جاري تحميل الصور من قاعدة البيانات...")

        lifecycleScope.launch {
            try {
                val images = SupabaseManager.getImages()
                
                if (images.isNotEmpty()) {
                    imageUrls.clear()
                    imageUrls.addAll(images.map { it.image_url })
                    adapter.notifyDataSetChanged()
                    hideLoading()
                    updateImageCount(images.size)
                    
                    Toast.makeText(this@SupervisorActivity, "تم تحميل ${images.size} صورة من قاعدة البيانات", Toast.LENGTH_LONG).show()
                } else {
                    showStatus("لا توجد صور مرفوعة حتى الآن.")
                    updateImageCount(0)
                }
                
            } catch (exception: Exception) {
                showError("فشل تحميل الصور: ${exception.message}")
                Toast.makeText(this@SupervisorActivity, "خطأ: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
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