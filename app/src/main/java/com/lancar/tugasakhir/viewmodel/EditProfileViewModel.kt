package com.lancar.tugasakhir.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.User
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

sealed interface EditProfileUiState {
    data class Success(val profile: User) : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
    object Loading : EditProfileUiState
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { EditProfileUiState.Loading }
            try {
                val response = repository.getProfile()
                if (response.isSuccessful && response.body()?.data != null) {
                    _uiState.update { EditProfileUiState.Success(response.body()!!.data!!) }
                } else {
                    _uiState.update { EditProfileUiState.Error("Gagal memuat profil.") }
                }
            } catch (e: Exception) {
                _uiState.update { EditProfileUiState.Error("Gagal memuat data profil: ${e.message}") }
            }
        }
    }

    fun saveUserProfile(user: User, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(user)
                if (response.isSuccessful) {
                    onResult(true, "Profil berhasil diperbarui")
                    fetchUserProfile()
                } else {
                    onResult(false, "Gagal: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Gagal memperbarui profil: ${e.message}")
            }
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val fileBytes = inputStream.readBytes()
                    val requestFile = fileBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("profileImage", "profile.jpg", requestFile)

                    val response = repository.uploadProfileImage(body)
                    if (response.isSuccessful) {
                        onResult(true, "Foto profil berhasil diunggah")
                        fetchUserProfile() // Langsung muat ulang data dari server
                    } else {
                        onResult(false, "Upload Gagal: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }
}