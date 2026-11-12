
package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun KabTheme(
    darkMode: Boolean? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val isDark = darkMode ?: context
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
        .getBoolean("dark_mode", false)

    MaterialTheme(
        colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun WelcomeTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val isDark = context
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
        .getBoolean("dark_mode", false)

    val colors = if (isDark) {
        darkColorScheme(
            primary = Color(0xFF487B45),
            onPrimary = Color.White,
            secondary = Color(0xFF405A3B),
            onSecondary = Color.White,
            tertiary = Color(0xFF2C412E),
            onTertiary = Color(0xFFE3F2D9),
            background = Color(0xFF17251B),
            onBackground = Color(0xFFE3F2D9),
            surface = Color(0xFF17251B),
            onSurface = Color(0xFFF2F5EC),
            onSurfaceVariant = Color(0xFFB0C6A8)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4D8046),
            onPrimary = Color.White,
            secondary = Color(0xFF78966C),
            onSecondary = Color.White,
            tertiary = Color(0xFFD9E2D1),
            onTertiary = Color(0xFF365C35),
            background = Color(0xFFF3F5E9),
            onBackground = Color(0xFF365C35),
            surface = Color(0xFFF3F5E9),
            onSurface = Color(0xFF253B24),
            onSurfaceVariant = Color(0xFF71816B)
        )
    }

    MaterialTheme(colorScheme = colors) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colors.background
        ) {
            content()
        }
    }
}
