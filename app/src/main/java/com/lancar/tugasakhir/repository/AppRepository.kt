package com.lancar.tugasakhir.repository

import com.lancar.tugasakhir.data.BookDao
import com.lancar.tugasakhir.data.NotificationHistoryDao
import com.lancar.tugasakhir.models.*
import com.lancar.tugasakhir.network.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val api: ApiService,
    private val bookDao: BookDao,
    private val notificationHistoryDao: NotificationHistoryDao
) {

    suspend fun login(req: LoginRequest): Response<ApiService.LoginData> {
        val body = mapOf("email" to req.email, "pass" to req.pass)
        return api.login(body)
    }

    suspend fun register(req: RegisterRequest): Response<ApiService.ApiEnvelope<Unit>> {
        val body = mapOf(
            "name" to req.name,
            "email" to req.email,
            "pass" to req.pass,
            "address" to req.address,
            "birthDate" to req.birthDate,
            "phoneNumber" to req.phoneNumber,
            "institution" to req.institution  // ✅ No null safety issues
        )
        return api.register(body)
    }

    // ====================== PROFILE ======================
    suspend fun getProfile(): Response<ApiService.ApiEnvelope<User>> {
        return api.getProfile()
    }

    suspend fun updateProfile(user: User): Response<ApiService.ApiEnvelope<Unit>> {
        val body = mapOf(
            "name" to (user.name ?: ""),
            "address" to (user.address ?: ""),
            "birthDate" to (user.birthDate ?: ""),
            "phoneNumber" to (user.phoneNumber ?: ""),
            "institution" to (user.institution ?: "")  // ✅ Handle User nullable fields
        )
        return api.updateProfile(body)
    }

    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val response = api.searchBooks(query)
            response.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ====================== BOOKS ======================
    suspend fun getBookDetail(bookId: String): Response<ApiService.ApiEnvelope<Book>> {
        return api.getBookDetail(bookId)
    }
    suspend fun getBooksByCategory(categoryName: String): List<Book> {
        return try { api.getBooksByCategory(categoryName) } catch (e: Exception) { emptyList() }
    }
    suspend fun getRecommendationBooks(): List<Book> {
        return try { api.getRecommendations() } catch (e: Exception) { emptyList() }
    }
    suspend fun getOurCollectionBooks(): List<Book> {
        return try { api.getOurCollection() } catch (e: Exception) { emptyList() }
    }
    suspend fun getKoleksiBooks(): List<Book> {
        return try { api.getUserCollections().data ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    suspend fun addBookToCollection(bookId: String) {
        try { api.addToCollection(mapOf("bookId" to bookId)) } catch (_: Exception) {}
    }
    suspend fun removeBookFromCollection(bookId: String) {
        try { api.removeFromCollection(bookId) } catch (_: Exception) {}
    }
    suspend fun findBookByBarcodeRemote(barcode: String): Book? {
        return try {
            val response = api.findBookByBarcode(barcode)
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) { null }
    }
    suspend fun findBookByBarcodeLocal(barcode: String): Book? {
        return bookDao.findByBarcode(barcode)
    }

    // ====================== CATEGORIES ======================
    suspend fun getCategories(): List<LibraryCategory> {
        return try { api.getCategories() } catch (e: Exception) { emptyList() }
    }

    // ====================== BORROW/HISTORY ======================
    suspend fun requestBorrowBook(bookId: String): Boolean {
        return try { api.requestBorrow(mapOf("bookId" to bookId)).success } catch (e: Exception) { false }
    }
    suspend fun getBorrowingHistory(): List<BorrowedBook> {
        return try { api.getBorrowingHistory().data ?: emptyList() } catch (e: Exception) { emptyList() }
    }
    suspend fun cancelBorrowRequest(historyId: String): Response<ApiService.ApiEnvelope<Unit>> {
        return api.cancelBorrow(mapOf("historyId" to historyId))
    }
    suspend fun requestReturnBook(historyId: String): Response<ApiService.ApiEnvelope<Unit>> {
        return api.requestReturn(mapOf("historyId" to historyId))
    }

    suspend fun uploadProfileImage(imagePart: MultipartBody.Part): Response<ApiService.ApiEnvelope<String>> {
        return api.uploadProfileImage(imagePart)
    }
    suspend fun sendFcmToken(token: String) {
        try { api.sendFcmToken(mapOf("token" to token)) } catch (_: Exception) { }
    }

    // ====================== NOTIFICATION HISTORY (ROOM) ======================
    suspend fun insertNotification(item: NotificationHistory) {
        notificationHistoryDao.insert(item)
    }
    fun getAllNotifications(): Flow<List<NotificationHistory>> {
        return notificationHistoryDao.getAll()
    }
    suspend fun markAllNotificationsAsRead() {
        notificationHistoryDao.markAllAsRead()
    }
}