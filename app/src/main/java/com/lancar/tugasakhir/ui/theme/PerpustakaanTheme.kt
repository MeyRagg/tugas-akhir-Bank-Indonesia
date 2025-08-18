package com.lancar.tugasakhir.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Kustom untuk Tema Terang (Light Theme)
private val LightColorScheme = lightColorScheme(
    primary = UclaBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = IndigoDye1,
    secondary = AirForceBlue,
    onSecondary = Color.White,
    background = BackgroundBiruMuda,
    onBackground = PrussianBlue,
    surface = Color.White, // Menentukan warna Card, TextField, dll
    onSurface = PrussianBlue
)

// Skema Warna Kustom untuk Tema Gelap (Dark Theme)
private val DarkColorScheme = darkColorScheme(
    primary = AirSuperiorityBlue,
    onPrimary = PrussianBlue,
    primaryContainer = IndigoDye2,
    onPrimaryContainer = LightBlue,
    secondary = SkyBlue,
    onSecondary = IndigoDye1,
    background = PrussianBlue,
    onBackground = LightBlue,
    surface = IndigoDye1, // Menentukan warna Card, TextField, dll
    onSurface = LightBlue
)

@Composable
fun PerpustakaanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context) else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}