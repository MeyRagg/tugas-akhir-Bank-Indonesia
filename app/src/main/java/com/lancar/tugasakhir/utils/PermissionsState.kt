package com.lancar.tugasakhir.utils

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsState(
    private val onPermissionsResult: (allGranted: Boolean, deniedPermissions: List<String>) -> Unit
) {
    private var permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>? = null

    fun setLauncher(launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
        this.permissionLauncher = launcher
    }

    /**
     * Request camera permission
     */
    fun requestCameraPermission() {
        val permissions = PermissionsHelper.getCameraPermissions()
        permissionLauncher?.launch(permissions)
    }

    /**
     * Request storage permission
     */
    fun requestStoragePermission() {
        val permissions = PermissionsHelper.getStoragePermissions()
        permissionLauncher?.launch(permissions)
    }

    /**
     * Request all image-related permissions
     */
    fun requestAllImagePermissions() {
        val permissions = PermissionsHelper.getAllImagePermissions()
        permissionLauncher?.launch(permissions)
    }

    /**
     * Request specific permissions
     */
    fun requestPermissions(permissions: Array<String>) {
        permissionLauncher?.launch(permissions)
    }
}

/**
 * Remember permissions state for handling permission requests in Compose
 */
@Composable
fun rememberPermissionsState(
    onPermissionsResult: (allGranted: Boolean, deniedPermissions: List<String>) -> Unit
): PermissionsState {
    val context = LocalContext.current

    val permissionsState = remember {
        PermissionsState(onPermissionsResult)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filterValues { !it }.keys.toList()
        val allGranted = deniedPermissions.isEmpty()
        onPermissionsResult(allGranted, deniedPermissions)
    }

    LaunchedEffect(permissionLauncher) {
        permissionsState.setLauncher(permissionLauncher)
    }

    return permissionsState
}

/**
 * Alternative simpler version for single permission requests
 */
@Composable
fun rememberPermissionState(
    permission: String,
    onPermissionResult: (isGranted: Boolean) -> Unit
): PermissionState {
    val context = LocalContext.current

    val permissionState = remember {
        PermissionState(permission, context, onPermissionResult)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    LaunchedEffect(permissionLauncher) {
        permissionState.setLauncher(permissionLauncher)
    }

    return permissionState
}

class PermissionState(
    private val permission: String,
    private val context: android.content.Context,
    private val onPermissionResult: (isGranted: Boolean) -> Unit
) {
    private var permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>? = null

    fun setLauncher(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        this.permissionLauncher = launcher
    }

    /**
     * Check if permission is currently granted
     */
    val isGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Request the permission
     */
    fun requestPermission() {
        permissionLauncher?.launch(permission)
    }

    /**
     * Check if we should show rationale for this permission
     */
    fun shouldShowRationale(): Boolean {
        return if (context is ComponentActivity) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                permission
            )
        } else {
            false
        }
    }
}