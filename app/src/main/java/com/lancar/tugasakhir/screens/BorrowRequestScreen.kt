//package com.lancar.tugasakhir.screens
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import com.lancar.tugasakhir.screens.common.ErrorView
//import com.lancar.tugasakhir.screens.common.LoadingView
//import com.lancar.tugasakhir.viewmodel.BookDetailUiState
//import com.lancar.tugasakhir.viewmodel.BookDetailViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BorrowRequestScreen(
//    navController: NavController,
//    bookId: String, // <-- PERUBAHAN 1: Tambahkan parameter bookId di sini
//    viewModel: BookDetailViewModel = hiltViewModel()
//) {
//    // PERUBAHAN 2: Gunakan `LaunchedEffect` untuk memuat detail buku saat layar dibuka
//    LaunchedEffect(key1 = bookId) {
//        viewModel.loadBookDetails(bookId)
//    }
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val context = LocalContext.current
//    var isLoading by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Booking Buku") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
//            when (val state = uiState) {
//                is BookDetailUiState.Loading -> LoadingView()
//                // PERUBAHAN 3: Perbaiki onRetry agar menggunakan bookId yang benar
//                is BookDetailUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadBookDetails(bookId) })
//                is BookDetailUiState.Success -> {
//                    Column(
//                        modifier = Modifier.fillMaxSize().padding(32.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Anda akan mem-booking buku:",
//                            style = MaterialTheme.typography.titleMedium,
//                            textAlign = TextAlign.Center
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = state.book.title,
//                            style = MaterialTheme.typography.headlineSmall,
//                            textAlign = TextAlign.Center
//                        )
//                        state.book.author?.let {
//                            Text(
//                                text = "oleh $it",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = Color.Gray
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(32.dp))
//                        Button(
//                            onClick = {
//                                isLoading = true
//                                viewModel.requestBorrowBook { isSuccess, message ->
//                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                                    isLoading = false
//                                    if (isSuccess) {
//                                        // Navigasi ke tab "Dipinjam" di riwayat setelah berhasil
//                                        navController.navigate("riwayat_screen/dipinjam") {
//                                            popUpTo("home_screen")
//                                        }
//                                    }
//                                }
//                            },
//                            modifier = Modifier.fillMaxWidth().height(50.dp),
//                            shape = RoundedCornerShape(12.dp),
//                            enabled = !isLoading
//                        ) {
//                            if (isLoading) {
//                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                            } else {
//                                Text("Konfirmasi Booking", fontSize = 16.sp)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}