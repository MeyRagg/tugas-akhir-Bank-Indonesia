package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface KoleksiUiState {
    data class Success(val books: List<Book>, val isRefreshing: Boolean = false) : KoleksiUiState
    data class Error(val message: String) : KoleksiUiState
    object Loading : KoleksiUiState
}

@HiltViewModel
class KoleksiViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<KoleksiUiState>(KoleksiUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchKoleksi(isInitialLoad = true)
    }

    fun fetchKoleksi(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            if (isInitialLoad) {
                _uiState.value = KoleksiUiState.Loading
            } else {
                (_uiState.value as? KoleksiUiState.Success)?.let {
                    _uiState.value = it.copy(isRefreshing = true)
                }
            }

            try {
                val books = repository.getKoleksiBooks()
                _uiState.update { KoleksiUiState.Success(books, isRefreshing = false) }

            } catch (e: Exception) {
                _uiState.update { KoleksiUiState.Error("Gagal memuat koleksi: ${e.message}") }
            }
        }
    }
}