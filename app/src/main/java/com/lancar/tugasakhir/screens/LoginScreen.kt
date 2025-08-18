package com.lancar.tugasakhir.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lancar.tugasakhir.R
import com.lancar.tugasakhir.navigation.Screen
import com.lancar.tugasakhir.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // --- State input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    // --- Context (ambil SEKALI di dalam composable, lalu dipakai di onClick)
    val context = LocalContext.current

    // --- State dari ViewModel
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    // --- Theme alias (biar pendek)
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    // --- Animasi tombol
    val buttonScale by animateFloatAsState(
        targetValue = if (authState.isLoading) 0.95f else 1f,
        animationSpec = tween(200),
        label = "buttonScale"
    )

    // --- Tampilkan error dari backend (email/password tidak cocok)
    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.consumeError()
        }
    }

    // --- Navigasi ketika login sukses
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
            authViewModel.resetSuccessState()
        }
    }

    // --- Aturan validasi (muncul setelah tombol ditekan)
    val emailRegex = remember { Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$") }
    val showEmailRequired = submitted && email.isBlank()
    val showPasswordRequired = submitted && password.isBlank()
    val showEmailFormatInvalid = submitted && email.isNotBlank() && !emailRegex.matches(email)
    val showPasswordTooShort = submitted && password.isNotBlank() && password.length < 1

    // ===================== UI =====================
    Box(modifier = Modifier.fillMaxSize()) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(cs.primary.copy(alpha = 0.1f), cs.surface)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_welcomelogin1),
                contentDescription = "Logo Aplikasi",
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Selamat Datang!",
                style = ty.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = cs.onSurface
            )

            Spacer(Modifier.height(32.dp))

            // ===== Email =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Card(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = cs.primary.copy(alpha = 0.1f))
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_email),
                                    contentDescription = "Ikon Email",
                                    modifier = Modifier.size(16.dp),
                                    tint = cs.primary
                                )
                            }
                        }
                    }
                )
            }
            // Pesan validasi TERPISAH
            if (showEmailRequired) {
                Spacer(Modifier.height(6.dp))
                Text("Email wajib diisi", color = cs.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            } else if (showEmailFormatInvalid) {
                Spacer(Modifier.height(6.dp))
                Text("Format email tidak valid", color = cs.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(16.dp))

            // ===== Password =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Card(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = cs.primary.copy(alpha = 0.1f))
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_password),
                                    contentDescription = "Ikon Password",
                                    modifier = Modifier.size(16.dp),
                                    tint = cs.primary
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        Card(
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = cs.primary.copy(alpha = 0.1f))
                        ) {
                            IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    painter = if (passwordVisible)
                                        painterResource(id = R.drawable.ic_revealpass)
                                    else painterResource(id = R.drawable.ic_hidepass),
                                    contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                    modifier = Modifier.size(16.dp),
                                    tint = cs.primary
                                )
                            }
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
            }
            // Pesan validasi TERPISAH
            if (showPasswordRequired) {
                Spacer(Modifier.height(6.dp))
                Text("Password wajib diisi", color = cs.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            } else if (showPasswordTooShort) {
                Spacer(Modifier.height(6.dp))
                Text("Minimal 6 karakter", color = cs.error, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(32.dp))

            // ===== Tombol Login =====
            Button(
                onClick = {
                    submitted = true

                    // Validasi saat tombol ditekan
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Periksa kembali input Anda", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!emailRegex.matches(email)) {
                        Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.length < 1) {
                        Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Lanjut cek kecocokan ke backend
                    authViewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(16.dp),
                enabled = !authState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = cs.primary,
                    disabledContainerColor = cs.surfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (authState.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = cs.onPrimary
                        )
                        Text("Masuk...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun?", color = cs.onSurfaceVariant, fontWeight = FontWeight.Medium)
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("Daftar di sini", fontWeight = FontWeight.Bold, color = cs.primary)
                }
            }
        }

        // Overlay loading tengah layar
        if (authState.isLoading) {
            Card(
                modifier = Modifier.align(Alignment.Center),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = cs.surface.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(24.dp),
                    color = cs.primary
                )
            }
        }
    }
}
