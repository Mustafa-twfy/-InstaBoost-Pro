package com.example.gallerypermissionapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.ContentResolver
import android.net.Uri

object SupabaseManager {

    data class ImageRecord(
        val id: String? = null,
        val image_url: String,
        val uploaded_at: String,
        val user_email: String? = null,
        val file_name: String? = null
    )

    suspend fun uploadImage(contentResolver: ContentResolver, imageUri: Uri, userEmail: String? = null): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // محاكاة رفع الصورة بنجاح
                Thread.sleep(1000) // محاكاة تأخير الشبكة
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getImages(): List<ImageRecord> {
        return try {
            withContext(Dispatchers.IO) {
                // محاكاة جلب الصور
                Thread.sleep(500) // محاكاة تأخير الشبكة
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // محاكاة إنشاء الحساب
                Thread.sleep(1000) // محاكاة تأخير الشبكة
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
                // محاكاة تسجيل الدخول
                Thread.sleep(1000) // محاكاة تأخير الشبكة
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
                // محاكاة تسجيل الخروج
                Thread.sleep(500) // محاكاة تأخير الشبكة
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentUser() = null
}
