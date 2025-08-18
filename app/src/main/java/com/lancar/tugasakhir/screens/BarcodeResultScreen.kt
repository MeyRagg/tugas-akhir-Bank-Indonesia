package com.lancar.tugasakhir.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lancar.tugasakhir.viewmodel.BarcodeResultViewModel

@Composable
fun BarcodeResultScreen(
    barcode: String,
    onResolved: (bookId: String?) -> Unit
) {
    val vm: BarcodeResultViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()

    LaunchedEffect(barcode) { vm.resolve(barcode, onResolved) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            ui.loading -> CircularProgressIndicator()
            ui.error != null -> Text(ui.error ?: "Gagal memuat")
            ui.notFound -> Text("Buku dengan barcode \"$barcode\" tidak ditemukan.")
        }
    }
}
