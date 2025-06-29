package com.example.gallerypermissionapp

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.ContentResolver
import android.net.Uri
import java.io.InputStream
import java.util.UUID

object SupabaseManager {

    private const val SUPABASE_URL = "https://cmjjgicgyubhvdddvnsb.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNtampnaWNneXViaGV2ZGR2bnNiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA1NTAxMTgsImV4cCI6MjA2NjEyNjExOH0.xvVDVQ8ibEBD2pmcY5bPLiM0QiB1JbuMRYHMA82iUdU"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    @Serializable
    data class ImageRecord(
        val id: String? = null,
        val image_url: String,
        val uploaded_at: String,
        val user_email: String? = null,
        val file_name: String? = null
    )

    suspend fun uploadImageToStorage(contentResolver: ContentResolver, imageUri: Uri): String? {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val fileName = "image_${UUID.randomUUID()}.jpg"
                
                if (inputStream != null) {
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    
                    // رفع الصورة إلى Supabase Storage
                    client.storage.from("images").upload(
                        path = fileName,
                        data = bytes,
                        upsert = false
                    )
                    
                    // الحصول على رابط الصورة العامة
                    val publicUrl = client.storage.from("images").publicUrl(fileName)
                    publicUrl
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveImageUrl(imageUrl: String, userEmail: String? = null, fileName: String? = null): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val imageRecord = ImageRecord(
                    image_url = imageUrl,
                    uploaded_at = java.time.LocalDateTime.now().toString(),
                    user_email = userEmail,
                    file_name = fileName
                )
                
                client.postgrest["images"].insert(imageRecord)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadImage(contentResolver: ContentResolver, imageUri: Uri, userEmail: String? = null): Boolean {
        return try {
            val imageUrl = uploadImageToStorage(contentResolver, imageUri)
            if (imageUrl != null) {
                val fileName = "image_${UUID.randomUUID()}.jpg"
                saveImageUrl(imageUrl, userEmail, fileName)
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getImages(): List<ImageRecord> {
        return try {
            withContext(Dispatchers.IO) {
                client.postgrest["images"]
                    .select()
                    .order("uploaded_at", ascending = false)
                    .decodeList<ImageRecord>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                client.gotrue.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                client.gotrue.loginWith(Email) {
                    this.email = email
                    this.password = password
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun signOut() {
        try {
            withContext(Dispatchers.IO) {
                client.gotrue.logout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentUser() = client.gotrue.currentUserOrNull()
} 