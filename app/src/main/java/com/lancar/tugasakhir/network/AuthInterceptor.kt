package com.lancar.tugasakhir.network

import com.lancar.tugasakhir.data.SessionExpiredNotifier
import com.lancar.tugasakhir.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sessionExpiredNotifier: SessionExpiredNotifier
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Cek jika path request adalah untuk login atau register
        val path = originalRequest.url.encodedPath
        if (path.endsWith("/auth/login") || path.endsWith("/auth/register")) {
            // Jika ya, JANGAN tambahkan token dan langsung lanjutkan request asli
            return chain.proceed(originalRequest)
        }

        // Untuk semua request lainnya, ambil token
        val token = runBlocking {
            userPreferencesRepository.authToken.first()
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Cek jika sesi berakhir (kode 401 Unauthorized)
        if (response.code == 401) {
            runBlocking {
                userPreferencesRepository.clearAuthToken()
                sessionExpiredNotifier.notifySessionExpired()
            }
        }

        return response
    }
}