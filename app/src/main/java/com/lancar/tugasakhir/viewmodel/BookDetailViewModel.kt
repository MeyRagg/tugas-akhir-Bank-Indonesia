package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.SavedStateHandle
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

sealed interface BookDetailUiState {
    data class Success(val book: Book) : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
    object Loading : BookDetailUiState
}

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val bookId: String = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchBookDetails()
    }

    fun refreshBookDetails() {
        fetchBookDetails()
    }

    private fun fetchBookDetails() {
        viewModelScope.launch {
            _uiState.update { BookDetailUiState.Loading }
            try {
                val response = repository.getBookDetail(bookId)
                if (response.isSuccessful && response.body()?.data != null) {
                    _uiState.update { BookDetailUiState.Success(response.body()!!.data!!) }
                } else {
                    _uiState.update { BookDetailUiState.Error("Buku tidak ditemukan atau terjadi kesalahan.") }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { BookDetailUiState.Error("Gagal memuat detail buku.") }
            }
        }
    }

    fun toggleCollectionStatus(book: Book) {
        viewModelScope.launch {
            try {
                val currentUiState = _uiState.value
                if (currentUiState is BookDetailUiState.Success) {
                    val newBook = currentUiState.book.copy(isInCollection = !(currentUiState.book.isInCollection ?: false))
                    _uiState.value = BookDetailUiState.Success(newBook)

                    if (newBook.isInCollection == true) {
                        repository.addBookToCollection(book.id)
                    } else {
                        repository.removeBookFromCollection(book.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                fetchBookDetails()
            }
        }
    }

    fun requestBorrowBook(onResult: (isSuccess: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = repository.requestBorrowBook(bookId)
                if (success) {
                    onResult(true, "Booking buku berhasil! Silakan tunggu persetujuan admin.")
                    fetchBookDetails()
                } else {
                    onResult(false, "Booking Gagal: Buku mungkin sudah tidak tersedia.")
                }
            } catch (e: Exception) {
                onResult(false, "Booking Gagal: ${e.message}")
            }
        }
    }
}