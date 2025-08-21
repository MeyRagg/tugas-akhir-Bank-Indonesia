package com.lancar.tugasakhir.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.models.LibraryCategory
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val categories: List<LibraryCategory> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val recommendationBooks: List<Book> = emptyList(),
    val ourCollectionBooks: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val userName: String = "",
    val userProfileImageUrl: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchHomeScreenData()
    }

    fun fetchHomeScreenData() {
        viewModelScope.launch {
            if (_uiState.value.categories.isEmpty()) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isSyncing = true) }
            }

            try {
                val userResponse = appRepository.getProfile()
                val userData = if (userResponse.isSuccessful) userResponse.body()?.data else null
                val userName = userData?.name ?: "Pengguna"
                val imageUrl = userData?.fullProfileImageUrl

                val favoriteBooks = appRepository.getKoleksiBooks()
                val categories = appRepository.getCategories()
                val recBooks = appRepository.getRecommendationBooks()
                val colBooks = appRepository.getOurCollectionBooks()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSyncing = false,
                        userName = userName,
                        userProfileImageUrl = imageUrl,
                        categories = categories,
                        favoriteBooks = favoriteBooks,
                        recommendationBooks = recBooks,
                        ourCollectionBooks = colBooks,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSyncing = false,
                        errorMessage = "Tidak ada koneksi internet."
                    )
                }
                Log.e("HomeViewModel", "Gagal fetch data: ${e.message}")
            }
        }
    }
}