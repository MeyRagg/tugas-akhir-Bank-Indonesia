package com.lancar.tugasakhir.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lancar.tugasakhir.navigation.Screen
import com.lancar.tugasakhir.viewmodel.AuthViewModel
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var nomorHp by remember { mutableStateOf("") }
    // --- TAMBAHAN BARU ---
    var institusi by remember { mutableStateOf("") }

    // --- TAMBAHAN BARU: 'institusi' ditambahkan ke dalam validasi ---
    val isFormComplete by remember(nama, alamat, email, tanggalLahir, nomorHp, institusi) {
        mutableStateOf(
            nama.isNotBlank() && alamat.isNotBlank() && email.isNotBlank() &&
                    tanggalLahir.isNotBlank() && nomorHp.isNotBlank() && institusi.isNotBlank()
        )
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrasi Akun") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            tanggalLahir = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
            ) { DatePicker(state = datePickerState) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Buat Akun Baru", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nama, onValueChange = { nama = it },
                label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = alamat, onValueChange = { alamat = it },
                label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = tanggalLahir, onValueChange = { },
                label = { Text("Tanggal Lahir") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                readOnly = true, enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = nomorHp, onValueChange = { nomorHp = it },
                label = { Text("Nomor HP") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            // --- KOLOM ISIAN BARU UNTUK INSTITUSI ---
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = institusi, onValueChange = { institusi = it },
                label = { Text("Institusi") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            // ------------------------------------------

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.updateRegistrationData(
                        name = nama,
                        address = alamat,
                        email = email,
                        birthDate = tanggalLahir,
                        phoneNumber = nomorHp,
                        // --- TAMBAHAN BARU ---
                        institution = institusi
                    )
                    navController.navigate(Screen.CreatePassword.route)
                },
                enabled = isFormComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp)
            ) { Text("Lanjutkan", fontSize = 16.sp) }
        }
    }
}