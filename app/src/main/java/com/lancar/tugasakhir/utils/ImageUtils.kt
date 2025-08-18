package com.lancar.tugasakhir.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

object ProfileImageUtils {

    /**
     * Mengompres dan memproses gambar yang sudah di-crop dari URI.
     * Tujuannya adalah untuk mendapatkan file di bawah maxSizeInBytes.
     *
     * @param context Context aplikasi.
     * @param imageUri URI dari gambar yang akan diproses.
     * @param maxSizeInBytes Ukuran file maksimum yang diinginkan dalam byte.
     * @return File yang telah diproses dan dikompres, atau null jika terjadi kegagalan.
     */
    suspend fun processCroppedImage(
        context: Context,
        imageUri: Uri,
        maxSizeInBytes: Long = 5 * 1024 * 1024 // 5MB
    ): File? = withContext(Dispatchers.IO) {
        try {
            val originalBitmap = loadBitmapFromUri(context, imageUri) ?: return@withContext null

            // Buat file output sementara
            val outputFile = File.createTempFile(
                "profile_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir
            )

            // Kompres bitmap secara dinamis untuk memenuhi target ukuran
            var quality = 95
            var outputStream: ByteArrayOutputStream
            do {
                outputStream = ByteArrayOutputStream()
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                // Kurangi kualitas untuk iterasi berikutnya jika ukurannya masih terlalu besar
                quality -= 5
            } while (outputStream.size() > maxSizeInBytes && quality > 40)

            // Tulis hasil kompresi terbaik ke file
            FileOutputStream(outputFile).use { it.write(outputStream.toByteArray()) }

            // Bersihkan bitmap
            originalBitmap.recycle()

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load bitmap from URI
     */
    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resize bitmap to fit within maxSize while maintaining aspect ratio
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // If already smaller than max size, return original
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        // Calculate new dimensions
        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / aspectRatio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Crop bitmap to circle
     */
    private fun cropToCircle(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        // First crop to square
        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)

        // Create circular bitmap
        val circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circleBitmap)

        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Clean up
        if (squaredBitmap != bitmap) {
            squaredBitmap.recycle()
        }

        return circleBitmap
    }

    /**
     * Get image dimensions without loading full bitmap
     */
    fun getImageDimensions(context: Context, uri: Uri): Pair<Int, Int>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                Pair(options.outWidth, options.outHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate sample size for efficient loading
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Load bitmap efficiently with sample size
     */
    fun loadEfficientBitmap(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            // First pass - get dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            // Second pass - load bitmap with sample size
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}