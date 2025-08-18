//package com.lancar.tugasakhir.utils
//
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.*
//
///**
// * Simple composable function to handle permissions
// */
//@Composable
//fun rememberPermissionsState(
//    onPermissionsResult: (allGranted: Boolean, deniedPermissions: List<String>) -> Unit
//): PermissionsState {
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        val deniedPermissions = permissions.filterValues { !it }.keys.toList()
//        val allGranted = deniedPermissions.isEmpty()
//        onPermissionsResult(allGranted, deniedPermissions)
//    }
//
//    return remember {
//        PermissionsState(permissionLauncher)
//    }
//}
//
///**
// * Simple permissions state class
// */
//class PermissionsState(
//    private val permissionLauncher: androidx.activity.result.ActivityResultLauncher<Map<String, Boolean>>
//) {
//
//    /**
//     * Request camera permission
//     */
//    fun requestCameraPermission() {
//        val permissions = PermissionsHelper.getCameraPermissions()
//        permissionLauncher.launch(permissions)
//    }
//
//    /**
//     * Request storage permission
//     */
//    fun requestStoragePermission() {
//        val permissions = PermissionsHelper.getStoragePermissions()
//        permissionLauncher.launch(permissions)
//    }
//
//    /**
//     * Request all image-related permissions
//     */
//    fun requestAllImagePermissions() {
//        val permissions = PermissionsHelper.getAllImagePermissions()
//        permissionLauncher.launch(permissions)
//    }
//
//    /**
//     * Request specific permissions
//     */
//    fun requestPermissions(permissions: Array<String>) {
//        permissionLauncher.launch(permissions)
//    }
//}