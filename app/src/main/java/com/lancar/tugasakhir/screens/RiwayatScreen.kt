package com.lancar.tugasakhir.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lancar.tugasakhir.R
import com.lancar.tugasakhir.models.BorrowedBook
import com.lancar.tugasakhir.screens.common.ErrorView
import com.lancar.tugasakhir.screens.common.SplashScreenLoadingView
import com.lancar.tugasakhir.viewmodel.RiwayatUiState
import com.lancar.tugasakhir.viewmodel.RiwayatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    navController: NavController,
    viewModel: RiwayatViewModel = hiltViewModel(),
    initialTab: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.fetchHistory()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is RiwayatUiState.Success && !(uiState as RiwayatUiState.Success).isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Riwayat Peminjaman",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when (val state = uiState) {
                is RiwayatUiState.Loading -> SplashScreenLoadingView()
                is RiwayatUiState.Error -> ErrorView(
                    message = state.message,
                    onRetry = { viewModel.fetchHistory(isInitialLoad = true) })
                is RiwayatUiState.Success -> RiwayatContent(
                    state = state,
                    navController = navController,
                    viewModel = viewModel,
                    initialTab = initialTab
                )
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RiwayatContent(
    state: RiwayatUiState.Success,
    navController: NavController,
    viewModel: RiwayatViewModel,
    initialTab: String?
) {
    val tabData = listOf(
        TabItem("Dipinjam", Icons.Default.MenuBook, Color(0xFFE57373)),
        TabItem("Proses", Icons.Default.HourglassEmpty, Color(0xFFFFB74D)),
        TabItem("Selesai", Icons.Default.CheckCircle, Color(0xFF81C784))
    )

    val initialPageIndex = when (initialTab) {
        "dipinjam" -> 0
        "proses" -> 1
        "selesai" -> 2
        else -> 0
    }
    val pagerState = rememberPagerState(initialPage = initialPageIndex, pageCount = { tabData.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        // Enhanced Tab Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { /* No divider */ }
            ) {
                tabData.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = null,
                                tint = if (pagerState.currentPage == index)
                                    tabItem.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tabItem.title,
                                fontWeight = if (pagerState.currentPage == index)
                                    FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (pagerState.currentPage == index)
                                    tabItem.color else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val filteredList = when (page) {
                0 -> state.history.filter { it.status.equals("DIPINJAM", ignoreCase = true) }
                1 -> state.history.filter {
                    it.status.equals("PENDING", true) ||
                            it.status.equals("SIAP_DIAMBIL", true) ||
                            it.status.equals("MENUNGGU_PENGEMBALIAN", true)
                }
                2 -> state.history.filter { it.status.equals("DIKEMBALIKAN", true) }
                else -> emptyList()
            }

            if (filteredList.isEmpty()) {
                EmptyState(page)
            } else {
                HistoryList(books = filteredList, viewModel = viewModel)
            }
        }
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HistoryList(books: List<BorrowedBook>, viewModel: RiwayatViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            HistoryItemCard(book = book, viewModel = viewModel)
        }
    }
}

@Composable
fun EmptyState(pageIndex: Int) {
    val (message, icon, color) = when(pageIndex) {
        0 -> Triple(
            "Riwayat peminjaman Anda masih kosong.\nMulai jelajahi koleksi kami!",
            Icons.Default.MenuBook,
            Color(0xFFE57373)
        )
        1 -> Triple(
            "Riwayat peminjaman Anda masih kosong.\nRiwayat proses akan muncul di sini.",
            Icons.Default.HourglassEmpty,
            Color(0xFFFFB74D)
        )
        2 -> Triple(
            "Riwayat peminjaman Anda masih kosong.\nSelesaikan peminjaman untuk melihat riwayat.",
            Icons.Default.CheckCircle,
            Color(0xFF81C784)
        )
        else -> Triple("Riwayat peminjaman Anda masih kosong.", Icons.Default.Info, Color.Gray)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun EmptyCollectionState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF64B5F6).copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryBooks,
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier
                        .size(64.dp)
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Koleksi Anda Masih Kosong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Simpan buku yang Anda\nminati dengan menekan\ntombol \"Koleksi\" pada\nhalaman detail buku.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun HistoryItemCard(book: BorrowedBook, viewModel: RiwayatViewModel) {
    val context = LocalContext.current
    var showReturnDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Konfirmasi Pengembalian") },
            text = { Text("Anda yakin ingin mengajukan pengembalian untuk buku \"${book.title}\"?") },
            confirmButton = {
                Button(onClick = {
                    // Panggil fungsi ViewModel saat dikonfirmasi
                    viewModel.requestReturnBook(book.id) { isSuccess, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    showReturnDialog = false // Tutup dialog setelah aksi
                }) {
                    Text("Ya, Ajukan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReturnDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Konfirmasi Pembatalan") },
            text = { Text("Anda yakin ingin membatalkan booking untuk buku \"${book.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBorrowRequest(book.id) { isSuccess, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Batalkan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Enhanced Book Cover
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = book.title,
                        modifier = Modifier
                            .size(width = 90.dp, height = 120.dp),
                        placeholder = painterResource(id = R.drawable.ic_placeholder_book),
                        error = painterResource(id = R.drawable.ic_placeholder_book),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Book Title and Author
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date Information with Icons
                    when (book.status.uppercase()) {
                        "PENDING", "SIAP_DIAMBIL" -> {
                            InfoRow(
                                icon = Icons.Default.Schedule,
                                label = "Tgl. Request",
                                value = formatDate(book.requestDate)
                            )
                        }
                        "DIPINJAM", "MENUNGGU_PENGEMBALIAN" -> {
                            InfoRow(
                                icon = Icons.Default.CalendarToday,
                                label = "Pinjam",
                                value = formatDate(book.borrowDate)
                            )
                            InfoRow(
                                icon = Icons.Default.Event,
                                label = "Batas Kembali",
                                value = formatDate(book.returnDate)
                            )
                        }
                        "DIKEMBALIKAN" -> {
                            InfoRow(
                                icon = Icons.Default.CheckCircle,
                                label = "Dikembalikan",
                                value = formatDate(book.actualReturnDate)
                            )
                        }
                    }
                }

                // Status Chip positioned at top right
                StatusChip(status = book.status)
            }

            // Countdown and Action Section
            when (book.status.uppercase()) {
                "DIPINJAM" -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    book.returnDate?.let {
                        ReturnCountdownTimer(deadline = it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showReturnDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.AssignmentReturn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Ajukan Pengembalian",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                "PENDING", "SIAP_DIAMBIL" -> {
                    if (book.status.uppercase() == "SIAP_DIAMBIL") {
                        Spacer(modifier = Modifier.height(16.dp))
                        book.pickupDeadline?.let { PickupCountdownTimer(it) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (book.status.uppercase() == "PENDING")
                                "Batalkan Booking"
                            else
                                "Batalkan Pengambilan",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 1.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PickupCountdownTimer(deadline: String) {
    var timeLeft by remember { mutableStateOf("Menghitung...") }
    var isExpired by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = deadline) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val deadlineMillis = try { sdf.parse(deadline)?.time ?: 0L } catch (e: Exception) { 0L }

        while (true) {
            val diff = deadlineMillis - System.currentTimeMillis()
            if (diff > 0) {
                if (TimeUnit.MILLISECONDS.toHours(diff) >= 24) {
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                    timeLeft = String.format("%d Hari : %02d Jam : %02d Menit", days, hours, minutes)
                } else {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
                    timeLeft = String.format("%02d jam : %02d menit : %02d detik", hours, minutes, seconds)
                }
                isExpired = false
            } else {
                timeLeft = "Batas waktu habis!"
                isExpired = true
                break
            }
            delay(1000L)
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isExpired)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Batas Waktu Pengambilan:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isExpired)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeLeft,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isExpired)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ReturnCountdownTimer(deadline: String) {
    var timeLeft by remember { mutableStateOf("Menghitung...") }
    var isExpired by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = deadline) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(deadline) ?: Date()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
        } catch (e: Exception) { /* biarkan waktu saat ini jika gagal parse */ }

        val deadlineMillis = calendar.timeInMillis

        while (true) {
            val diff = deadlineMillis - System.currentTimeMillis()
            if (diff > 0) {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                timeLeft = String.format("%d Hari, %02d Jam, %02d Menit", days, hours, minutes)
                isExpired = false
            } else {
                timeLeft = "Batas waktu pengembalian habis!"
                isExpired = true
                break
            }
            delay(1000L)
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = if (isExpired)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Sisa Waktu Pengembalian:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isExpired)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeLeft,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isExpired)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString == null) return "-"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(parser.parse(dateString)!!)
    } catch (e: Exception) { dateString }
}

@Composable
fun StatusChip(status: String) {
    val (text, containerColor, textColor) = when (status.uppercase()) {
        "PENDING" -> Triple("Proses Pinjam", Color(0xFFE3F2FD), Color(0xFF1976D2))
        "SIAP_DIAMBIL" -> Triple("Siap Diambil", Color(0xFFE8F5E8), Color(0xFF2E7D32))
        "DIPINJAM" -> Triple("Dipinjam", Color(0xFFFFEBEE), Color(0xFFC62828))
        "MENUNGGU_PENGEMBALIAN" -> Triple("Proses Kembali", Color(0xFFFFF3E0), Color(0xFFEF6C00))
        "DIKEMBALIKAN" -> Triple("Selesai", Color(0xFFE8F5E8), Color(0xFF388E3C))
        else -> Triple(status, Color(0xFFF5F5F5), Color(0xFF616161))
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        shadowElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}