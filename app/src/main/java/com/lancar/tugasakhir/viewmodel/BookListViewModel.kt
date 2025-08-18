package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface BookListUiState {
    data class Success(val books: List<Book>) : BookListUiState
    data class Error(val message: String) : BookListUiState
    data object Loading : BookListUiState
}

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Loading)
    val uiState: StateFlow<BookListUiState> = _uiState

    fun fetchBooks(categoryName: String) {
        viewModelScope.launch {
            _uiState.value = BookListUiState.Loading
            try {
                // --- PERBAIKAN: Memanggil suspend function langsung ---
                val books = repository.getBooksByCategory(categoryName)
                if (books.isNotEmpty()) {
                    _uiState.value = BookListUiState.Success(books)
                } else {
                    // State Success dengan list kosong akan ditangani di UI
                    _uiState.value = BookListUiState.Success(emptyList())
                }
            } catch (e: Exception) {
                _uiState.value = BookListUiState.Error(
                    "Gagal memuat data. Periksa koneksi internet Anda."
                )
            }
        }
    }
}