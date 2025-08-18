package com.lancar.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lancar.tugasakhir.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarcodeUi(
    val loading: Boolean = false,
    val error: String? = null,
    val notFound: Boolean = false
)

@HiltViewModel
class BarcodeResultViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(BarcodeUi())
    val ui: StateFlow<BarcodeUi> = _ui

    fun resolve(barcode: String, onResolved: (String?) -> Unit) {
        viewModelScope.launch {
            _ui.value = BarcodeUi(loading = true)

            val local = repo.findBookByBarcodeLocal(barcode)
            if (local != null) {
                _ui.value = BarcodeUi()
                onResolved(local.id)
                return@launch
            }

            val remote = repo.findBookByBarcodeRemote(barcode)
            if (remote != null) {
                _ui.value = BarcodeUi()
                onResolved(remote.id)
            } else {
                _ui.value = BarcodeUi(notFound = true)
                onResolved(null)
            }
        }
    }
}