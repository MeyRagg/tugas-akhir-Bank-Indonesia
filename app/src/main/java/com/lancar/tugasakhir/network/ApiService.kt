package com.lancar.tugasakhir.network

import com.google.gson.annotations.SerializedName
import com.lancar.tugasakhir.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Wrapper umum untuk semua respons API (KECUALI LOGIN)
    data class ApiEnvelope<T>(
        val success: Boolean,
        val message: String?,
        val data: T?
    )

    // DTO ini sekarang akan menjadi model respons LANGSUNG untuk login
    data class LoginData(
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: User
    )

    // ================== AUTH ==================
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<LoginData>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiEnvelope<Unit>>


    // ================== BOOKS ==================
    @GET("books")
    suspend fun getBooksByCategory(@Query("category") categoryName: String?): List<Book>

    @GET("books/recommendations")
    suspend fun getRecommendations(): List<Book>

    @GET("books/our-collection")
    suspend fun getOurCollection(): List<Book>

    @GET("books/{id}")
    suspend fun getBookDetail(@Path("id") bookId: String): Response<ApiEnvelope<Book>>

    @GET("books/barcode/{barcode}")
    suspend fun findBookByBarcode(@Path("barcode") barcode: String): Response<ApiEnvelope<Book>>


    // ================== KOLEKSI (USER COLLECTIONS) ==================
    @GET("user/collection")
    suspend fun getUserCollections(): ApiEnvelope<List<Book>>

    @POST("user/collection/add")
    suspend fun addToCollection(@Body body: Map<String, @JvmSuppressWildcards Any>): ApiEnvelope<Unit>

    @DELETE("user/collection/remove/{bookId}")
    suspend fun removeFromCollection(@Path("bookId") bookId: String): ApiEnvelope<Unit>


    // ================== CATEGORIES ==================
    @GET("categories")
    suspend fun getCategories(): List<LibraryCategory>

    @GET("books/search")
    suspend fun searchBooks(@Query("q") query: String): ApiEnvelope<List<Book>>


    // ================== BORROW / HISTORY ==================
    @POST("user/borrow")
    suspend fun requestBorrow(@Body body: Map<String, @JvmSuppressWildcards Any>): ApiEnvelope<Unit>

    @POST("user/borrow/cancel")
    suspend fun cancelBorrow(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<Unit>>

    @POST("user/return/request")
    suspend fun requestReturn(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<Unit>>

    @GET("user/history")
    suspend fun getBorrowingHistory(): ApiEnvelope<List<BorrowedBook>>


    // ================== PROFILE ==================
    @GET("user/profile")
    suspend fun getProfile(): Response<ApiEnvelope<User>>

    @PUT("user/profile")
    suspend fun updateProfile(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiEnvelope<Unit>>

    @Multipart
    @POST("upload/profile")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): Response<ApiEnvelope<String>>


    // ================== FCM TOKEN ==================
    @POST("user/fcm-token")
    suspend fun sendFcmToken(@Body body: Map<String, @JvmSuppressWildcards Any>): ApiEnvelope<Unit>
}