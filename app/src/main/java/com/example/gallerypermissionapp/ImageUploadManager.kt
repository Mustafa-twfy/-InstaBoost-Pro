package com.example.gallerypermissionapp

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

object ImageUploadManager {

    private const val API_KEY = "2755d71a39e3f08ef78d46dcae2a30c0"
    private const val BASE_URL = "https://api.imgbb.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ImgBbApiService::class.java)

    suspend fun uploadImage(contentResolver: ContentResolver, imageUri: Uri): ImgBbResponse? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()

            if (imageBytes != null) {
                val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                apiService.uploadImage(API_KEY, base64Image)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

interface ImgBbApiService {
    @FormUrlEncoded
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Field("image") base64Image: String
    ): ImgBbResponse
}

data class ImgBbResponse(
    @SerializedName("data") val data: ImgBbData?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImgBbData(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("display_url") val displayUrl: String,
    @SerializedName("delete_url") val deleteUrl: String
) 