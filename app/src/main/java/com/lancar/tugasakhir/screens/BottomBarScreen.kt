package com.lancar.tugasakhir.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomBarScreen("home", "Home", Icons.Default.Home)
    data object Riwayat : BottomBarScreen("riwayat", "Riwayat", Icons.Default.History)
    data object Scan : BottomBarScreen("scan", "Scan", Icons.Default.QrCodeScanner)
    data object Koleksi : BottomBarScreen("koleksi", "Koleksi", Icons.Default.CollectionsBookmark)
    data object Profile : BottomBarScreen("profile", "Profil", Icons.Default.Person)
}