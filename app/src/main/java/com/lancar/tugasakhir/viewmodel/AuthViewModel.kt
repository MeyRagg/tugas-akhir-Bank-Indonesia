package com.lancar.tugasakhir.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.lancar.tugasakhir.data.RegistrationHolder
import com.lancar.tugasakhir.data.UserPreferencesRepository
import com.lancar.tugasakhir.models.LoginRequest
import com.lancar.tugasakhir.models.RegisterRequest
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun updateRegistrationData(
        name: String, address: String, email: String,
        birthDate: String, phoneNumber: String, institution: String
    ) {
        RegistrationHolder.data = RegisterRequest(
            name, address, email, "", birthDate, phoneNumber, institution
        )
    }

    fun register(password: String) {
        val current = RegistrationHolder.data?.copy(pass = password)
        if (current == null) {
            _uiState.update { it.copy(errorMessage = "Data registrasi tidak lengkap.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                val regResp = repository.register(current)
                if (regResp.isSuccessful) {
                    login(current.email, current.pass)
                } else {
                    val msg = extractHttpMessage(regResp.errorBody()?.string())
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg ?: "Registrasi gagal.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Registrasi Gagal: ${e.message}") }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                val response = repository.login(LoginRequest(email, pass))

                if (response.isSuccessful) {
                    val loginData = response.body()
                    val token = loginData?.token // <-- SEKARANG INI AKAN DIKENALI

                    if (!token.isNullOrBlank()) {
                        prefsRepository.saveAuthToken(token)
                        sendFcmTokenToServer()
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Token kosong diterima dari server.") }
                    }
                } else {
                    val errorMsg = extractHttpMessage(response.errorBody()?.string())
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg ?: "Login gagal") }
                }
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    401 -> "Email atau password salah."
                    else -> "Login gagal (Kode: ${e.code()})."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Login Gagal: ${e.message ?: "Terjadi kesalahan"}") }
            }
        }
    }

    private suspend fun sendFcmTokenToServer() {
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            Log.d("FCM_TOKEN", "Token FCM: $fcmToken")
            repository.sendFcmToken(fcmToken)
        } catch (e: Exception) {
            Log.e("FCM_TOKEN", "Gagal kirim token: ${e.message}")
        }
    }

    private fun extractHttpMessage(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            val json = JSONObject(raw)
            json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optString("error").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            raw.take(100)
        }
    }

    fun consumeError() { _uiState.update { it.copy(errorMessage = null) } }
    fun resetSuccessState() { _uiState.update { it.copy(isSuccess = false) } }
}