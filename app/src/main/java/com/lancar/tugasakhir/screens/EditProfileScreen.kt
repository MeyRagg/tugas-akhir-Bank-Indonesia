package com.lancar.tugasakhir.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.lancar.tugasakhir.R
import com.lancar.tugasakhir.models.User
import com.lancar.tugasakhir.screens.common.ErrorView
import com.lancar.tugasakhir.screens.common.LoadingView
import com.lancar.tugasakhir.utils.PermissionsHelper
import com.lancar.tugasakhir.utils.ProfileImageUtils
import com.lancar.tugasakhir.utils.rememberPermissionsState
import com.lancar.tugasakhir.viewmodel.EditProfileUiState
import com.lancar.tugasakhir.viewmodel.EditProfileViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDisplayOrEditField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit = {},
    isLocked: Boolean = false,
    singleLine: Boolean = true,
    onClick: (() -> Unit)? = null,
    isError: Boolean = false,
    isRequired: Boolean = false,
    icon: ImageVector
) {
    val viewModifier = if (onClick != null && isEditing) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(viewModifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = when {
                isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isRequired && isEditing && !isLocked) {
                    Text(" *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isEditing && !isLocked) {
                    Icon(
                        imageVector = if (onClick != null) Icons.Default.CalendarToday else Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing && !isLocked && onClick == null) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = singleLine,
                    shape = RoundedCornerShape(12.dp),
                    isError = isError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    placeholder = {
                        Text(
                            text = "Masukkan ${label.lowercase()}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                )
            } else {
                Text(
                    text = if (value.isNotEmpty()) value else "Belum diisi",
                    fontSize = 16.sp,
                    fontWeight = if (value.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (value.isNotEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is EditProfileUiState.Loading -> LoadingView()
        is EditProfileUiState.Error -> ErrorView(
            message = state.message,
            onRetry = { viewModel.fetchUserProfile() }
        )
        is EditProfileUiState.Success -> {
            ProfileContent(
                initialProfile = state.profile,
                navController = navController,
                onSave = { updatedUser ->
                    viewModel.saveUserProfile(updatedUser) { isSuccess, message ->
                        Toast.makeText(navController.context, message, Toast.LENGTH_SHORT).show()
                        if (isSuccess) {
                            navController.popBackStack()
                        }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    initialProfile: User,
    navController: NavController,
    onSave: (User) -> Unit,
    viewModel: EditProfileViewModel
) {
    var namaLengkap by remember(initialProfile.name) { mutableStateOf(initialProfile.name ?: "") }
    var tanggalLahir by remember(initialProfile.birthDate) { mutableStateOf(initialProfile.birthDate ?: "") }
    var institusi by remember(initialProfile.institution) { mutableStateOf(initialProfile.institution ?: "") }
    var nomorHp by remember(initialProfile.phoneNumber) { mutableStateOf(initialProfile.phoneNumber ?: "") }
    var alamat by remember(initialProfile.address) { mutableStateOf(initialProfile.address ?: "") }
    val email = remember(initialProfile.email) { initialProfile.email ?: "" } // Email dari initialProfile

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isEditing by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Image cropper and launchers
    val imageCropper = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                scope.launch {
                    processAndUploadImage(uri, context, viewModel) { uploading, progress ->
                        isUploading = uploading
                        uploadProgress = progress
                    }
                }
            }
        }
    }

    fun launchImageCropper(uri: Uri) {
        val cropOptions = CropImageContractOptions(uri, CropImageOptions(
            guidelines = CropImageView.Guidelines.ON,
            aspectRatioX = 1, aspectRatioY = 1, fixAspectRatio = true
        ))
        imageCropper.launch(cropOptions)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { launchImageCropper(it) }
    }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { tempImageUri?.let { launchImageCropper(it) } }
    }

    val permissionsHandler = rememberPermissionsState { allGranted, _ ->
        if (!allGranted) Toast.makeText(context, "Izin diperlukan", Toast.LENGTH_SHORT).show()
    }

    fun launchCameraWithPermission() {
        if (PermissionsHelper.hasCameraPermission(context)) {
            val imageFile = File.createTempFile("camera_", ".jpg", context.cacheDir)
            tempImageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
            cameraLauncher.launch(tempImageUri!!)
        } else {
            permissionsHandler.requestPermissions(PermissionsHelper.getCameraPermissions())
        }
    }

    fun launchGalleryWithPermission() {
        if (PermissionsHelper.hasStoragePermission(context)) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            permissionsHandler.requestPermissions(PermissionsHelper.getStoragePermissions())
        }
    }

    val isProfileChanged = namaLengkap != (initialProfile.name ?: "") ||
            tanggalLahir != (initialProfile.birthDate ?: "") ||
            institusi != (initialProfile.institution ?: "") ||
            nomorHp != (initialProfile.phoneNumber ?: "") ||
            alamat != (initialProfile.address ?: "")

    fun validateAndSave() {
        when {
            namaLengkap.isBlank() -> Toast.makeText(context, "Nama lengkap tidak boleh kosong", Toast.LENGTH_LONG).show()
            alamat.isBlank() -> Toast.makeText(context, "Alamat tidak boleh kosong", Toast.LENGTH_LONG).show()
            institusi.isBlank() -> Toast.makeText(context, "Institusi tidak boleh kosong", Toast.LENGTH_LONG).show()
            else -> {
                val updatedUser = initialProfile.copy(
                    name = namaLengkap,
                    birthDate = tanggalLahir,
                    institution = institusi,
                    phoneNumber = nomorHp,
                    address = alamat
                )
                onSave(updatedUser)
                isEditing = false
            }
        }
    }

    BackHandler(enabled = isEditing) {
        if (isProfileChanged) {
            showConfirmDialog = true
        } else {
            isEditing = false
        }
    }

    // Dialogs
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        tanggalLahir = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Simpan Perubahan?") },
            text = { Text("Anda memiliki perubahan yang belum disimpan. Simpan?") },
            confirmButton = {
                Button(onClick = {
                    validateAndSave()
                    showConfirmDialog = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmDialog = false
                    isEditing = false
                    navController.popBackStack()
                }) { Text("Buang") }
            }
        )
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Pilih Foto Profil") },
            text = { Text("Pilih sumber foto profil") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        showImagePickerDialog = false
                        launchGalleryWithPermission()
                    }) {
                        Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Galeri")
                    }
                    Button(onClick = {
                        showImagePickerDialog = false
                        launchCameraWithPermission()
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kamera")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImagePickerDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Main UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isEditing && isProfileChanged) {
                                showConfirmDialog = true
                            } else if (isEditing) {
                                isEditing = false
                            } else {
                                navController.popBackStack()
                            }
                        },
                        enabled = !isUploading
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile photo section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(120.dp)) {
                    AsyncImage(
                        model = initialProfile.profileImageUrl ?: R.drawable.ic_profile,
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable(enabled = isEditing && !isUploading) { showImagePickerDialog = true }
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_profile),
                        error = painterResource(R.drawable.ic_profile)
                    )
                    if (isEditing && !isUploading) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Ganti Foto",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }

                if (uploadProgress.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uploadProgress,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isEditing) {
                    OutlinedButton(
                        onClick = {
                            if (isProfileChanged) {
                                showConfirmDialog = true
                            } else {
                                isEditing = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isUploading
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = { validateAndSave() },
                        modifier = Modifier.weight(1f),
                        enabled = !isUploading && isProfileChanged
                    ) {
                        Text("Simpan")
                    }
                } else {
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Profil")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile fields
            EnhancedDisplayOrEditField(
                label = "Nama Lengkap",
                value = namaLengkap,
                onValueChange = { namaLengkap = it },
                isEditing = isEditing,
                isRequired = true,
                isError = isEditing && namaLengkap.isBlank(),
                icon = Icons.Default.Badge
            )

            EnhancedDisplayOrEditField(
                label = "Tanggal Lahir",
                value = tanggalLahir,
                isEditing = isEditing,
                onClick = {
                    if (isEditing) {
                        showDatePicker = true
                    }
                },
                icon = Icons.Default.CalendarMonth
            )

            EnhancedDisplayOrEditField(
                label = "Email",
                value = email,
                isEditing = false,
                isLocked = true,
                icon = Icons.Default.Email
            )

            EnhancedDisplayOrEditField(
                label = "Institusi",
                value = institusi,
                onValueChange = { institusi = it },
                isEditing = isEditing,
                isRequired = true,
                isError = isEditing && institusi.isBlank(),
                icon = Icons.Default.School
            )

            EnhancedDisplayOrEditField(
                label = "Nomor Handphone",
                value = nomorHp,
                onValueChange = { nomorHp = it },
                isEditing = isEditing,
                icon = Icons.Default.Phone
            )

            EnhancedDisplayOrEditField(
                label = "Alamat",
                value = alamat,
                onValueChange = { alamat = it },
                isEditing = isEditing,
                singleLine = false,
                isRequired = true,
                isError = isEditing && alamat.isBlank(),
                icon = Icons.Default.Home
            )
        }
    }
}

suspend fun processAndUploadImage(
    uri: Uri,
    context: android.content.Context,
    viewModel: EditProfileViewModel,
    onProgress: (Boolean, String) -> Unit
) {
    onProgress(true, "Memproses gambar...")
    try {
        val processedFile = ProfileImageUtils.processCroppedImage(context, uri)
        if (processedFile != null) {
            onProgress(true, "Mengunggah foto...")
            val processedUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", processedFile)
            viewModel.uploadProfileImage(processedUri, context) { isSuccess, message ->
                onProgress(false, "")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                processedFile.delete()
            }
        } else {
            onProgress(false, "")
            Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        onProgress(false, "")
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}