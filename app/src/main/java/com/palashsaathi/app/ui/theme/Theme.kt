package com.palashsaathi.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class AppThemeMode(val titleHindi: String, val titleEnglish: String) {
    SYSTEM("सिस्टम डिफ़ॉल्ट", "System Default"),
    LIGHT("लाइट मोड", "Light Mode"),
    DARK("डार्क मोड", "Dark Mode")
}

private val LightColorScheme = lightColorScheme(
    primary = CoralSaffronPrimaryLight,
    onPrimary = CoralSaffronOnPrimaryLight,
    primaryContainer = CoralSaffronContainerLight,
    onPrimaryContainer = CoralSaffronOnContainerLight,
    secondary = ForestGreenSecondaryLight,
    onSecondary = ForestGreenOnSecondaryLight,
    secondaryContainer = ForestGreenContainerLight,
    onSecondaryContainer = ForestGreenOnContainerLight,
    tertiary = GoldenAmberTertiaryLight,
    onTertiary = GoldenAmberOnTertiaryLight,
    tertiaryContainer = GoldenAmberContainerLight,
    onTertiaryContainer = GoldenAmberOnContainerLight,
    background = NeutralBackgroundLight,
    onBackground = NeutralOnBackgroundLight,
    surface = NeutralSurfaceLight,
    onSurface = NeutralOnSurfaceLight,
    surfaceVariant = NeutralSurfaceVariantLight,
    onSurfaceVariant = NeutralOnSurfaceVariantLight,
    outline = NeutralOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = CoralSaffronPrimaryDark,
    onPrimary = CoralSaffronOnPrimaryDark,
    primaryContainer = CoralSaffronContainerDark,
    onPrimaryContainer = CoralSaffronOnContainerDark,
    secondary = ForestGreenSecondaryDark,
    onSecondary = ForestGreenOnSecondaryDark,
    secondaryContainer = ForestGreenContainerDark,
    onSecondaryContainer = ForestGreenOnContainerDark,
    tertiary = GoldenAmberTertiaryDark,
    onTertiary = GoldenAmberOnTertiaryDark,
    tertiaryContainer = GoldenAmberContainerDark,
    onTertiaryContainer = GoldenAmberOnContainerDark,
    background = NeutralBackgroundDark,
    onBackground = NeutralOnBackgroundDark,
    surface = NeutralSurfaceDark,
    onSurface = NeutralOnSurfaceDark,
    surfaceVariant = NeutralSurfaceVariantDark,
    onSurfaceVariant = NeutralOnSurfaceVariantDark,
    outline = NeutralOutlineDark
)

@Composable
fun PalashSaathiTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
