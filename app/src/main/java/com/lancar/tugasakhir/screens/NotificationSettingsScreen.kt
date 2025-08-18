//package com.lancar.tugasakhir.screens
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import com.lancar.tugasakhir.models.NotificationSettings
//import com.lancar.tugasakhir.screens.common.ErrorView
//import com.lancar.tugasakhir.screens.common.LoadingView
//import com.lancar.tugasakhir.viewmodel.NotificationSettingsUiState
//import com.lancar.tugasakhir.viewmodel.NotificationSettingsViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationSettingsScreen(
//    navController: NavController,
//    viewModel: NotificationSettingsViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Pengaturan Notifikasi") },
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
//                is NotificationSettingsUiState.Loading -> LoadingView()
//                is NotificationSettingsUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.fetchSettings() })
//                is NotificationSettingsUiState.Success -> {
//                    NotificationSettingsContent(
//                        settings = state.settings,
//                        onSettingsChanged = { updatedSettings ->
//                            viewModel.updateSettings(updatedSettings)
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun NotificationSettingsContent(
//    settings: NotificationSettings,
//    onSettingsChanged: (NotificationSettings) -> Unit
//) {
//    Column(modifier = Modifier.padding(16.dp)) {
//        SettingSwitchItem(
//            title = "Notifikasi Buku Baru",
//            description = "Info saat ada buku baru.",
//            checked = settings.newBookAlerts,
//            onCheckedChange = {
//                onSettingsChanged(settings.copy(newBookAlerts = it))
//            }
//        )
//        Divider()
//        SettingSwitchItem(
//            title = "Update Promosi",
//            description = "Info tentang promosi.",
//            checked = settings.promotionUpdates,
//            onCheckedChange = {
//                onSettingsChanged(settings.copy(promotionUpdates = it))
//            }
//        )
//        Divider()
//        SettingSwitchItem(
//            title = "Pengingat Pinjaman",
//            description = "Pengingat batas waktu.",
//            checked = settings.loanReminders,
//            onCheckedChange = {
//                onSettingsChanged(settings.copy(loanReminders = it))
//            }
//        )
//    }
//}
//
//@Composable
//private fun SettingSwitchItem(
//    title: String,
//    description: String,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit
//) {
//    val context = LocalContext.current
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Column(modifier = Modifier.weight(1f)) {
//            Text(text = title, style = MaterialTheme.typography.bodyLarge)
//            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//        }
//        Spacer(modifier = Modifier.width(16.dp))
//        Switch(
//            checked = checked,
//            onCheckedChange = {
//                onCheckedChange(it)
//                Toast.makeText(context, "Menyimpan...", Toast.LENGTH_SHORT).show()
//            }
//        )
//    }
//}