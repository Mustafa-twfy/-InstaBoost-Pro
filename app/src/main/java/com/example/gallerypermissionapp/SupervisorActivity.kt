package com.example.gallerypermissionapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SupervisorActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

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
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
    }

    private fun setupRecyclerView() {
        adapter = SupervisorImageAdapter(imageUrls)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    private fun loadImagesFromSupabase() {
        showLoading("جاري تحميل الصور من الخادم...")

        lifecycleScope.launch {
            try {
                val result = SupabaseManager.client.postgrest["uploaded_images"]
                    .select {
                        order("created_at", Order.DESCENDING)
                    }.decodeList<UploadedImageDto>()

                if (result.isEmpty()) {
                    showStatus("لا توجد صور مرفوعة حتى الآن.")
                } else {
                    imageUrls.clear()
                    imageUrls.addAll(result.map { it.imageUrl })
                    adapter.notifyDataSetChanged()
                    hideLoading()
                }
            } catch (exception: Exception) {
                showError("فشل تحميل الصور: ${exception.message}")
                Toast.makeText(this@SupervisorActivity, "خطأ: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(message: String) {
        progressBar.visibility = View.VISIBLE
        tvStatus.visibility = View.VISIBLE
        tvStatus.text = message
        recyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        tvStatus.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showStatus(message: String) {
        progressBar.visibility = View.GONE
        tvStatus.visibility = View.VISIBLE
        tvStatus.text = message
        recyclerView.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        tvStatus.visibility = View.VISIBLE
        tvStatus.text = message
        recyclerView.visibility = View.GONE
    }
}

@Serializable
data class UploadedImageDto(
    @SerializedName("image_url") val imageUrl: String
)

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