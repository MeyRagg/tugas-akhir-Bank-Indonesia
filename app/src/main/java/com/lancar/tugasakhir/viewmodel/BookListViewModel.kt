// File: tugasakhir/viewmodel/BookListViewModel.kt

package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BookListUiState {
    data class Success(val books: List<Book>) : BookListUiState
    data class Error(val message: String) : BookListUiState
    object Loading : BookListUiState
}

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Loading)
    val uiState: StateFlow<BookListUiState> = _uiState

    fun fetchBooks(listType: String) {
        viewModelScope.launch {
            _uiState.value = BookListUiState.Loading
            try {
                val books = when (listType) {
                    "favorites" -> repository.getKoleksiBooks()
                    "recommendations" -> repository.getRecommendationBooks()
                    "our_collection" -> repository.getOurCollectionBooks()
                    else -> repository.getBooksByCategory(listType) // 'else' menangani kategori
                }
                _uiState.value = BookListUiState.Success(books)
            } catch (e: Exception) {
                _uiState.value = BookListUiState.Error(
                    "Gagal memuat data. Periksa koneksi internet Anda."
                )
            }
        }
    }
}