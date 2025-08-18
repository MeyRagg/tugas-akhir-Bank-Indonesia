package com.lancar.tugasakhir.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Rute sebelum login
    data object Onboarding : Screen("onboarding_screen")
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
    data object CreatePassword : Screen("create_password_screen")

    // Rute untuk layar di dalam Bottom Navigation Bar
    data object Home : Screen("home_screen", "Home", Icons.Default.Home)
    data object Riwayat : Screen("riwayat_screen", "Riwayat", Icons.Default.History)
    data object Scan : Screen("scan_screen", "Scan", Icons.Default.QrCodeScanner)
    data object Koleksi : Screen("koleksi_screen", "Koleksi", Icons.Default.CollectionsBookmark)
    data object Profile : Screen("profile_screen", "Profil", Icons.Default.Person)

    // Rute detail (tidak ada di Bottom Bar)
    data object EditProfile : Screen("edit_profile_screen", "Edit Profil")
    data object NotificationHistory : Screen("notification_history_screen", "Notifikasi")
    data object BookDetail : Screen("book_detail_screen/{bookId}") {
        fun createRoute(bookId: String) = "book_detail_screen/$bookId"
    }
    data object BarcodeResult : Screen("barcode_result/{barcode}") {
        fun createRoute(barcode: String) = "barcode_result/${Uri.encode(barcode)}"
    }
    data object ReturnCountdown : Screen("return_countdown_screen/{returnDate}") {
        fun createRoute(returnDate: String) = "return_countdown_screen/$returnDate"
    }
    data object Review : Screen("review_screen/{bookTitle}") {
        fun createRoute(bookTitle: String) = "review_screen/$bookTitle"
    }
//    data object BorrowRequest : Screen("borrow_request_screen/{bookId}") {
//        fun createRoute(bookId: String) = "borrow_request_screen/$bookId"
//    }
//    data object NotificationSettings : Screen("notification_settings_screen", "Notifikasi")
    data object CategoryList : Screen("category_list_screen", "Semua Kategori")
    data object BookList : Screen("book_list_screen/{category}", "Daftar Buku") {
        const val ARG = "category"
        fun createRoute(category: String): String =
            "book_list_screen/${Uri.encode(category.trim())}"
    }
}