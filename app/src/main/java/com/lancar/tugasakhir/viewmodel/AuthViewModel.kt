package com.lancar.tugasakhir.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
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
    private val prefsRepository: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    val registrationState = savedStateHandle.getStateFlow("registrationState", RegistrationState())

    fun updateRegistrationField(updateAction: (RegistrationState) -> RegistrationState) {
        val newState = updateAction(registrationState.value)
        savedStateHandle["registrationState"] = newState
    }

    fun register(password: String) {
        val currentData = registrationState.value

        val request = RegisterRequest(
            name = currentData.name.trim(),
            address = currentData.address.trim(),
            email = currentData.email.trim(),
            pass = password,
            birthDate = currentData.birthDate.trim(),
            phoneNumber = currentData.phoneNumber.trim(),
            institution = currentData.institution.trim()  // ✅ Safe karena both non-nullable
        )

        // Validasi tetap sama - semua field non-nullable
        when {
            request.name.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
                return
            }
            request.address.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Alamat tidak boleh kosong") }
                return
            }
            request.email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Email tidak boleh kosong") }
                return
            }
            request.pass.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Password tidak boleh kosong") }
                return
            }
            request.birthDate.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Tanggal lahir tidak boleh kosong") }
                return
            }
            request.phoneNumber.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Nomor HP tidak boleh kosong") }
                return
            }
            request.institution.isBlank() -> {  // ✅ No more null safety warning
                _uiState.update { it.copy(errorMessage = "Institusi tidak boleh kosong") }
                return
            }
        }

        // Rest of registration logic...
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                val regResp = repository.register(request)
                if (!regResp.isSuccessful) {
                    val msg = extractHttpMessage(regResp.errorBody()?.string())
                    throw Exception(msg ?: "Registrasi gagal.")
                }

                val loginResp = repository.login(LoginRequest(request.email, request.pass))
                if (loginResp.isSuccessful) {
                    val token = loginResp.body()?.token
                    if (!token.isNullOrBlank()) {
                        prefsRepository.saveAuthToken(token)
                        sendFcmTokenToServer()
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    } else {
                        throw Exception("Token kosong diterima dari server.")
                    }
                } else {
                    throw HttpException(loginResp)
                }

            } catch (e: HttpException) {
                val msg = "Gagal login setelah registrasi (Kode: ${e.code()})."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Proses Pendaftaran Gagal: ${e.message}") }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                val response = repository.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    val token = response.body()?.token
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