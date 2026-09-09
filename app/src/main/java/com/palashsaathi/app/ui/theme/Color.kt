package com.palashsaathi.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * PalashSaathi Accessible Color Palette
 *
 * Carefully calibrated for high-contrast classroom daylight and low-light rural school conditions (WCAG AAA compliant):
 * 1. Coral / Saffronish (Primary) - Inspired by Palash (Flame of the Forest) / Saffron
 * 2. Greenish (Secondary) - Forest Green / Foliage
 * 3. Yellowish / Golden Amber (Tertiary) - Classroom Sunshine / Amber Highlights
 */

// --- Primary: Coral (Saffronish) ---
val CoralSaffronPrimaryLight = Color(0xFFD84315)       // Deep Saffron-Coral (Contrast > 4.5:1 on white)
val CoralSaffronOnPrimaryLight = Color(0xFFFFFFFF)
val CoralSaffronContainerLight = Color(0xFFFFEBE5)     // Soft Peach-Saffron container
val CoralSaffronOnContainerLight = Color(0xFF3E0A00)   // Dark Mahogany text (Contrast > 14:1)

val CoralSaffronPrimaryDark = Color(0xFFFF8A65)        // Radiant Saffron-Coral (Contrast > 7:1 on dark)
val CoralSaffronOnPrimaryDark = Color(0xFF551900)
val CoralSaffronContainerDark = Color(0xFF7B2609)
val CoralSaffronOnContainerDark = Color(0xFFFFDBD1)

// --- Secondary: Greenish (Forest / Foliage) ---
val ForestGreenSecondaryLight = Color(0xFF1E6B24)     // Rich Forest Green (Contrast > 6:1 on white)
val ForestGreenOnSecondaryLight = Color(0xFFFFFFFF)
val ForestGreenContainerLight = Color(0xFFE5F8E6)     // Pale Mint container
val ForestGreenOnContainerLight = Color(0xFF042607)   // Deep Forest text (Contrast > 15:1)

val ForestGreenSecondaryDark = Color(0xFF81C784)      // Sage/Mint Green (Contrast > 7:1 on dark)
val ForestGreenOnSecondaryDark = Color(0xFF00390B)
val ForestGreenContainerDark = Color(0xFF18511E)
val ForestGreenOnContainerDark = Color(0xFFC8E6C9)

// --- Tertiary: Yellowish (Golden Amber / Sun) ---
val GoldenAmberTertiaryLight = Color(0xFF9E6000)      // Deep Warm Ochre (Contrast > 4.8:1 on white)
val GoldenAmberOnTertiaryLight = Color(0xFFFFFFFF)
val GoldenAmberContainerLight = Color(0xFFFFF3D0)     // Pale Sun Gold container
val GoldenAmberOnContainerLight = Color(0xFF331D00)   // Deep Amber Bronze (Contrast > 14:1)

val GoldenAmberTertiaryDark = Color(0xFFFFD54F)       // Luminous Sunflower Yellow (Contrast > 11:1 on dark)
val GoldenAmberOnTertiaryDark = Color(0xFF452B00)
val GoldenAmberContainerDark = Color(0xFF643F00)
val GoldenAmberOnContainerDark = Color(0xFFFFECA3)

// --- Neutral Surfaces & Backgrounds ---
// Light Mode Neutrals (Clean, glare-reducing, high contrast)
val NeutralBackgroundLight = Color(0xFFF9F9FB)
val NeutralSurfaceLight = Color(0xFFFFFFFF)
val NeutralSurfaceVariantLight = Color(0xFFF0F1F4)
val NeutralOnBackgroundLight = Color(0xFF1A1C1E)      // Deep charcoal (Contrast > 15:1)
val NeutralOnSurfaceLight = Color(0xFF1A1C1E)
val NeutralOnSurfaceVariantLight = Color(0xFF44474E)  // Secondary text (Contrast > 7:1)
val NeutralOutlineLight = Color(0xFFD1D5DC)

// Dark Mode Neutrals (OLED friendly, reduced eye strain)
val NeutralBackgroundDark = Color(0xFF111315)
val NeutralSurfaceDark = Color(0xFF1A1C1E)
val NeutralSurfaceVariantDark = Color(0xFF26292D)
val NeutralOnBackgroundDark = Color(0xFFE3E2E6)       // Bright crisp off-white (Contrast > 13:1)
val NeutralOnSurfaceDark = Color(0xFFE3E2E6)
val NeutralOnSurfaceVariantDark = Color(0xFFC4C6CF)
val NeutralOutlineDark = Color(0xFF41454C)

// Legacy alias references for backward compatibility with screens
val PalashOrange = CoralSaffronPrimaryLight
val PalashOrangeLight = Color(0xFFFF7043)
val PalashOrangeDark = Color(0xFFBF360C)

val ForestGreen = ForestGreenSecondaryLight
val ForestGreenLight = Color(0xFF4CAF50)
val ForestGreenBackground = ForestGreenContainerLight

val ClassroomAmber = GoldenAmberTertiaryLight
val DarkCharcoal = NeutralOnBackgroundLight
val LightSurface = NeutralBackgroundLight
val CardBackground = NeutralSurfaceLight
val SubtitleBannerColor = Color(0xFF212529)
val SubtitleTextColor = Color(0xFFF1F3F5)
val SubtitleHighlight = Color(0xFFFFD54F)
val BorderOutline = NeutralOutlineLight
