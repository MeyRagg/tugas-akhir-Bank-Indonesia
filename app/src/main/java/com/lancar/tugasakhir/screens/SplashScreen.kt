package com.lancar.tugasakhir.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.lancar.tugasakhir.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Efek ini akan berjalan sekali saat layar pertama kali dibuat
    LaunchedEffect(key1 = true) {
        delay(3000L) // Tunggu selama 3 detik
        // Pindah ke layar login dan hapus splash screen dari back stack
        navController.popBackStack()
        navController.navigate(Screen.Login.route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Perpustakaan App", color = Color.White, style = MaterialTheme.typography.headlineLarge)
    }
}
