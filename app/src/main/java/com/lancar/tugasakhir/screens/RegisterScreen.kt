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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lancar.tugasakhir.navigation.Screen
import com.lancar.tugasakhir.viewmodel.AuthViewModel
import com.lancar.tugasakhir.viewmodel.RegistrationState
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // HAPUS reset state agar data tidak hilang saat back dari screen password

    LaunchedEffect(Unit) {
        authViewModel.updateRegistrationField { RegistrationState() } // Mengosongkan form
    }

    val regState by authViewModel.registrationState.collectAsStateWithLifecycle()

    val isFormComplete by remember(regState) {
        derivedStateOf {
            regState.name.isNotBlank() &&
                    regState.address.isNotBlank() &&
                    regState.email.isNotBlank() &&
                    regState.birthDate.isNotBlank() &&
                    regState.phoneNumber.isNotBlank() &&
                    regState.institution.isNotBlank()
        }
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
                            val selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                .format(Date(it))
                            authViewModel.updateRegistrationField { state ->
                                state.copy(birthDate = selectedDate)
                            }
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
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
                value = regState.name,
                onValueChange = { newValue ->
                    authViewModel.updateRegistrationField { it.copy(name = newValue) }
                },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = regState.address,
                onValueChange = { newValue ->
                    authViewModel.updateRegistrationField { it.copy(address = newValue) }
                },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = regState.email,
                onValueChange = { newValue ->
                    authViewModel.updateRegistrationField { it.copy(email = newValue) }
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tanggal: readOnly + clickable (tidak disabled agar bisa ditekan)
            OutlinedTextField(
                value = regState.birthDate,
                onValueChange = { /* no-op */ },
                label = { Text("Tanggal Lahir") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pilih Tanggal"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = regState.phoneNumber,
                onValueChange = { newValue ->
                    authViewModel.updateRegistrationField { it.copy(phoneNumber = newValue) }
                },
                label = { Text("Nomor HP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = regState.institution,
                onValueChange = { newValue ->
                    authViewModel.updateRegistrationField { it.copy(institution = newValue) }
                },
                label = { Text("Institusi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Screen.CreatePassword.route) },
                enabled = isFormComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Lanjutkan", fontSize = 16.sp)
            }
        }
    }
}
