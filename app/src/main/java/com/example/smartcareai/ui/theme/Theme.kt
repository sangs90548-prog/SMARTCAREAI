package com.example.smartcareai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = SurfaceLightBlue,
    onPrimaryContainer = PrimaryBlueVariant,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = TealLight,
    onSecondaryContainer = SecondaryTeal,
    error = EmergencyRed,
    onError = Color.White,
    errorContainer = EmergencyRedLight,
    onErrorContainer = EmergencyRed,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLightBlue,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

@Composable
fun SMARTCAREAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
