package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.data.UserPreferencesRepository
import com.lancar.tugasakhir.models.User
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    object Loading : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            try {
                val response = repository.getProfile()
                if (response.isSuccessful && response.body()?.data != null) {
                    _uiState.update { ProfileUiState.Success(response.body()!!.data!!) }
                } else {
                    _uiState.update { ProfileUiState.Error("Gagal memuat profil: ${response.message()}") }
                }
            } catch (e: Exception) {
                _uiState.update { ProfileUiState.Error("Gagal memuat profil: ${e.message}") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefsRepository.clearAuthToken()
        }
    }
}