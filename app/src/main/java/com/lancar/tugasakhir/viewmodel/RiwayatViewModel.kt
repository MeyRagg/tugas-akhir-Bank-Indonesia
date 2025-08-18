package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.BorrowedBook
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RiwayatUiState {
    // Pastikan properti isRefreshing ada di sini
    data class Success(val history: List<BorrowedBook>, val isRefreshing: Boolean = false) : RiwayatUiState
    data class Error(val message: String) : RiwayatUiState
    object Loading : RiwayatUiState
}

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RiwayatUiState>(RiwayatUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchHistory(isInitialLoad = true)
    }

    fun fetchHistory(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = RiwayatUiState.Loading
            } else {
                // Set isRefreshing menjadi true saat proses refresh dimulai
                (_uiState.value as? RiwayatUiState.Success)?.let {
                    _uiState.value = it.copy(isRefreshing = true)
                }
            }

            try {
                val history = repository.getBorrowingHistory()
                _uiState.update {
                    if (history.isEmpty()) {
                        RiwayatUiState.Error("Anda belum memiliki riwayat peminjaman.")
                    } else {
                        // Set isRefreshing menjadi false setelah data berhasil didapat
                        RiwayatUiState.Success(history, isRefreshing = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { RiwayatUiState.Error("Gagal memuat riwayat: ${e.message}") }
            } finally {
                // BLOK PENTING: Pastikan isRefreshing selalu false di akhir,
                // bahkan jika terjadi error.
                (_uiState.value as? RiwayatUiState.Success)?.let {
                    if (it.isRefreshing) {
                        _uiState.value = it.copy(isRefreshing = false)
                    }
                }
            }
        }
    }

    fun cancelBorrowRequest(historyId: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.cancelBorrowRequest(historyId)
                if (response.isSuccessful) {
                    onResult(true, "Booking berhasil dibatalkan.")
                    fetchHistory() // Muat ulang riwayat untuk memperbarui daftar
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Permintaan gagal"
                    onResult(false, "Gagal: $errorBody")
                }
            } catch (e: Exception) {
                onResult(false, "Gagal: ${e.message}")
            }
        }
    }

    fun requestReturnBook(historyId: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.requestReturnBook(historyId)
                if (response.isSuccessful) {
                    onResult(true, "Pengajuan pengembalian berhasil.")
                    // Muat ulang riwayat untuk memperbarui status
                    fetchHistory()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Permintaan gagal"
                    onResult(false, "Gagal: $errorBody")
                }
            } catch (e: Exception) {
                onResult(false, "Gagal: ${e.message}")
            }
        }
    }
}