//// Di dalam file baru: viewmodel/NotificationSettingsViewModel.kt
//package com.lancar.tugasakhir.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.lancar.tugasakhir.models.NotificationSettings
//import com.lancar.tugasakhir.repository.AppRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//sealed interface NotificationSettingsUiState {
//    data class Success(val settings: NotificationSettings) : NotificationSettingsUiState
//    data class Error(val message: String) : NotificationSettingsUiState
//    object Loading : NotificationSettingsUiState
//}
//
//@HiltViewModel
//class NotificationSettingsViewModel @Inject constructor(
//    private val repository: AppRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow<NotificationSettingsUiState>(NotificationSettingsUiState.Loading)
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        fetchSettings()
//    }
//
//    fun fetchSettings() {
//        viewModelScope.launch {
//            _uiState.update { NotificationSettingsUiState.Loading }
//            try {
//                val settings = repository.getNotificationSettings()
//                _uiState.update { NotificationSettingsUiState.Success(settings) }
//            } catch (e: Exception) {
//                _uiState.update { NotificationSettingsUiState.Error("Gagal memuat pengaturan.") }
//            }
//        }
//    }
//
//    fun updateSettings(newSettings: NotificationSettings) {
//        viewModelScope.launch {
//            try {
//                // Optimistic update: Langsung update UI
//                _uiState.update { NotificationSettingsUiState.Success(newSettings) }
//                // Kirim perubahan ke server
//                repository.updateNotificationSettings(newSettings)
//            } catch (e: Exception) {
//                // Jika gagal, kembalikan ke state sebelumnya dan tampilkan error
//                fetchSettings() // Ambil data terbaru dari server
//                // Di sini Anda bisa menambahkan state error khusus untuk update
//            }
//        }
//    }
//}