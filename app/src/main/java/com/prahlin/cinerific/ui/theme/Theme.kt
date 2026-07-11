package com.prahlin.cinerific.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE7FF),
    onPrimaryContainer = Color(0xFF10285F),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DDFF),
    onSecondaryContainer = Color(0xFF30105D),
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF4B5563),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB7C8FF),
    onPrimary = Color(0xFF10285F),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDDE7FF),
    secondary = Color(0xFFD8B4FE),
    onSecondary = Color(0xFF30105D),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFE8DDFF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),
)

@Composable
fun CinerificTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
