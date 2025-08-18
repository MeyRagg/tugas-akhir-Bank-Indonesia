package com.lancar.tugasakhir.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionsHelper {

    /**
     * Check if camera permission is granted
     */
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if storage permission is granted
     * For Android 13+ (API 33+), we use READ_MEDIA_IMAGES
     * For older versions, we use READ_EXTERNAL_STORAGE
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses granular media permissions
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Older Android versions
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if write external storage permission is granted
     * Note: This is not needed for Android 10+ when using scoped storage
     */
    fun hasWriteStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need write permission for app-specific directories
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get required camera permissions
     */
    fun getCameraPermissions(): Array<String> {
        return arrayOf(Manifest.permission.CAMERA)
    }

    /**
     * Get required storage permissions based on Android version
     */
    fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Android 9 and below
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Get all permissions needed for image operations (camera + storage)
     */
    fun getAllImagePermissions(): Array<String> {
        val cameraPermissions = getCameraPermissions()
        val storagePermissions = getStoragePermissions()
        return cameraPermissions + storagePermissions
    }

    /**
     * Check if all image permissions are granted
     */
    fun hasAllImagePermissions(context: Context): Boolean {
        return hasCameraPermission(context) && hasStoragePermission(context)
    }

    /**
     * Get list of denied permissions from a list of permissions
     */
    fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if we should show rationale for any of the permissions
     */
    fun shouldShowRationaleForAnyPermission(
        activity: androidx.activity.ComponentActivity,
        permissions: Array<String>
    ): Boolean {
        return permissions.any { permission ->
            androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }
}