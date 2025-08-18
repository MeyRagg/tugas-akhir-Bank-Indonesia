package com.lancar.tugasakhir.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lancar.tugasakhir.R
import com.lancar.tugasakhir.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel() // <-- TAMBAHKAN VIEWMODEL
) {
    val items = listOf(
        OnboardingItem("Selamat Datang!", "Akses ribuan referensi ekonomi & moneter.", R.drawable.onboarding_image_1),
        OnboardingItem("Pengetahuan di Ujung Jari", "Cari dan pinjam materi secara digital.", R.drawable.onboarding_image_2),
        OnboardingItem("Mulai Petualangan Anda", "Jelajahi dunia literasi ekonomi sekarang!", R.drawable.onboarding_image_3)
    )

    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    // --- FUNGSI BARU UNTUK MENANDAI ONBOARDING SELESAI ---
    val finishOnboarding = {
        viewModel.setOnboardingCompleted()
        onFinish()
    }
    // ----------------------------------------------------

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(item = items[page])
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            items.forEachIndexed { index, _ ->
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    if (pagerState.currentPage < items.size - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        finishOnboarding() // <-- GUNAKAN FUNGSI BARU
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            val buttonText = if (pagerState.currentPage < items.size - 1) "Lanjutkan" else "Mulai"
            Text(text = buttonText, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pagerState.currentPage < items.size - 1) {
            TextButton(onClick = { finishOnboarding() }) { // <-- GUNAKAN FUNGSI BARU
                Text("Lewati", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = item.description) {
        displayedText = ""
        item.description.forEach { char ->
            displayedText += char
            delay(50L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = displayedText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}