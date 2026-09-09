package com.palashsaathi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PalashOrange,
    onPrimary = Color.White,
    primaryContainer = PalashOrangeLight,
    onPrimaryContainer = DarkCharcoal,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = ForestGreenBackground,
    onSecondaryContainer = ForestGreen,
    tertiary = ClassroomAmber,
    background = LightSurface,
    onBackground = DarkCharcoal,
    surface = CardBackground,
    onSurface = DarkCharcoal,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = DarkCharcoal
)

@Composable
fun PalashSaathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // Light theme optimized for high-contrast classroom daylight
        typography = Typography,
        content = content
    )
}
