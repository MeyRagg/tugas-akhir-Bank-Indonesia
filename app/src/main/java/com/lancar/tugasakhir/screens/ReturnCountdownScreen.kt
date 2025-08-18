// Di dalam file: screens/ReturnCountdownScreen.kt
package com.lancar.tugasakhir.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnCountdownScreen(navController: NavController, returnDateStr: String) {
    var timeLeft by remember { mutableStateOf("") }

    // LaunchedEffect untuk menghitung dan memperbarui waktu mundur setiap detik
    LaunchedEffect(key1 = returnDateStr) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val returnDate = dateFormat.parse(returnDateStr)

        while (true) {
            val now = System.currentTimeMillis()
            val diff = (returnDate?.time ?: now) - now

            if (diff > 0) {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
                timeLeft = String.format("%d Hari\n%02d:%02d:%02d", days, hours, minutes, seconds)
            } else {
                timeLeft = "Waktu Habis"
                break // Hentikan loop jika waktu sudah habis
            }
            delay(1000L) // Tunggu 1 detik
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batas Waktu Pengembalian") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sisa Waktu Peminjaman:",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = timeLeft,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Harap kembalikan buku sebelum waktu habis untuk menghindari denda.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}