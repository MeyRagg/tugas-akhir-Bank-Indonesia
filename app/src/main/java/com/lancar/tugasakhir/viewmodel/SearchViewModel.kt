package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val searchResults: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val searchInitiated: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        searchQuery
            .debounce(300) // Menunggu 300ms setelah user berhenti mengetik
            .onEach { query ->
                // --- PERBAIKAN LOGIKA DI SINI ---
                if (query.length < 2) {
                    // Jika query pendek, reset state tapi JANGAN panggil API
                    _uiState.update { it.copy(isLoading = false, searchResults = emptyList(), searchInitiated = query.isNotEmpty()) }
                } else {
                    // Jika query cukup panjang, tampilkan loading dan panggil API
                    _uiState.update { it.copy(isLoading = true, searchInitiated = true) }
                    val results = repository.searchBooks(query)
                    _uiState.update { it.copy(isLoading = false, searchResults = results) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchQuery.value = newQuery
    }
}