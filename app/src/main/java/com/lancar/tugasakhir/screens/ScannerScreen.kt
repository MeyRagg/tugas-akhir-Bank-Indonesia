package com.lancar.tugasakhir.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.lancar.tugasakhir.PortraitCaptureActivity
import com.lancar.tugasakhir.navigation.Screen
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var lastScanned by remember { mutableStateOf<String?>(null) }

    // Kamera (ZXing JourneyApps)
    val cameraScanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            if (result.contents == null) {
                Toast.makeText(context, "Pemindaian dibatalkan", Toast.LENGTH_SHORT).show()
            } else {
                val scannedData = result.contents
                lastScanned = scannedData
                // --- PERUBAHAN LOGIKA ---
                // Gunakan handleScannedData untuk parsing yang lebih canggih
                handleScannedData(scannedData, context, navController)
            }
        }
    )

    // Galeri (ambil gambar lalu decode QR)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                val scanned = decodeQrCodeFromUri(context, uri)
                if (scanned != null) {
                    lastScanned = scanned
                    // --- PERUBAHAN LOGIKA ---
                    // Gunakan handleScannedData untuk parsing yang lebih canggih
                    handleScannedData(scanned, context, navController)
                } else {
                    Toast.makeText(context, "QR Code/Barcode tidak ditemukan di gambar", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    // Otomatis panggil kamera saat halaman muncul
    LaunchedEffect(Unit) {
        val options = ScanOptions().apply {
            setPrompt("")
            setBeepEnabled(true)
            setOrientationLocked(false)
            captureActivity = PortraitCaptureActivity::class.java
        }
        cameraScanLauncher.launch(options)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pindai QR Code",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
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
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.20f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.QrCode2,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                "Siap Memindai!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Arahkan kamera ke QR pada buku untuk melihat detail atau meminjam.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                Column(Modifier.fillMaxWidth()) {
                    QuickActionButton(
                        title = "Pindai Ulang (Kamera)",
                        subtitle = "Buka kamera lagi untuk memindai QR Code",
                        icon = Icons.Filled.CameraAlt,
                        onClick = {
                            val options = ScanOptions().apply {
                                setPrompt("Arahkan kamera ke QR Code")
                                setBeepEnabled(true)
                                setOrientationLocked(false)
                                captureActivity = PortraitCaptureActivity::class.java
                            }
                            cameraScanLauncher.launch(options)
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    QuickActionButton(
                        title = "Pilih dari Galeri",
                        subtitle = "Deteksi QR dari gambar yang sudah ada",
                        icon = Icons.Filled.PhotoLibrary,
                        onClick = { galleryLauncher.launch("image/*") }
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Tampilkan hasil scan terakhir jika ada
            if (lastScanned != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Hasil Pindai Terakhir",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                lastScanned ?: "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Jika pemindaian gagal, pastikan QR jelas, tidak silau, dan dalam fokus.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/* ======================= Utils ======================= */

private fun handleScannedData(data: String, context: Context, navController: NavController) {
    // 1) JSON {"id":"123"} atau {"bookId":"123"}
    try {
        val obj = JSONObject(data)
        val bookId = obj.optString("bookId", obj.optString("id", ""))
        if (bookId.isNotBlank()) {
            navController.navigate(Screen.BookDetail.createRoute(bookId))
            return
        }
    } catch (_: Exception) { /* not json */ }

    // 2) Deep link satuperpustakaanku://book/<id>
    if (data.startsWith("satuperpustakaanku://")) {
        val id = runCatching { Uri.parse(data).lastPathSegment }.getOrNull()
        if (!id.isNullOrBlank()) {
            navController.navigate(Screen.BookDetail.createRoute(id))
            return
        }
    }

    // 3) URL yang memuat /books/<id>
    if (data.startsWith("http", ignoreCase = true)) {
        val uri = runCatching { Uri.parse(data) }.getOrNull()
        val segs = uri?.pathSegments ?: emptyList()
        val idx = segs.indexOf("books")
        if (idx != -1 && idx + 1 < segs.size) {
            val id = segs[idx + 1]
            if (id.isNotBlank()) {
                navController.navigate(Screen.BookDetail.createRoute(id))
                return
            }
        }
    }

    // 4) Hanya angka:
    //    - Panjang >= 8: anggap barcode, arahkan ke BarcodeResult untuk pencarian
    //    - Lainnya: anggap sebagai ID buku langsung
    val cleaned = data.trim()
    if (cleaned.all { it.isDigit() }) {
        if (cleaned.length >= 8) {
            // Arahkan ke BarcodeResult untuk pencarian barcode
            navController.navigate(Screen.BarcodeResult.createRoute(cleaned))
            return
        } else {
            navController.navigate(Screen.BookDetail.createRoute(cleaned))
            return
        }
    }

    // 5) Default: arahkan ke BarcodeResult untuk pencarian umum
    navController.navigate(Screen.BarcodeResult.createRoute(data))
}

private fun decodeQrCodeFromUri(context: Context, uri: Uri): String? {
    return try {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src)
        }

        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()
        reader.decode(binaryBitmap).text
    } catch (e: Exception) {
        null
    }
}