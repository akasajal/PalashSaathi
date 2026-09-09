package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.palashsaathi.app.R
import com.palashsaathi.app.data.model.LanguagePairMode
import com.palashsaathi.app.ui.theme.AppThemeMode

@Composable
fun SettingsScreen(
    currentThemeMode: AppThemeMode,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    currentLanguageMode: LanguagePairMode,
    onLanguageModeChanged: (LanguagePairMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (currentLanguageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Settings" else "सेटिंग्स (Settings)",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Customize display appearance and application preferences.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance & Theme Section Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentLanguageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Appearance" else "दिखावट (Appearance)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Theme Options Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                SettingsOptionRow(
                    titleHindi = "सिस्टम डिफ़ॉल्ट",
                    titleEnglish = "System Default",
                    description = "Follows your tablet / phone OS system setting",
                    icon = Icons.Default.BrightnessAuto,
                    isSelected = currentThemeMode == AppThemeMode.SYSTEM,
                    onClick = { onThemeModeChanged(AppThemeMode.SYSTEM) },
                    languageMode = currentLanguageMode
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                SettingsOptionRow(
                    titleHindi = "लाइट मोड",
                    titleEnglish = "Light Mode",
                    description = "High-contrast daylight mode for classroom teaching",
                    icon = Icons.Default.LightMode,
                    isSelected = currentThemeMode == AppThemeMode.LIGHT,
                    onClick = { onThemeModeChanged(AppThemeMode.LIGHT) },
                    languageMode = currentLanguageMode
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                SettingsOptionRow(
                    titleHindi = "डार्क मोड",
                    titleEnglish = "Dark Mode",
                    description = "Low-glare dark surface for reduced eye strain",
                    icon = Icons.Default.DarkMode,
                    isSelected = currentThemeMode == AppThemeMode.DARK,
                    onClick = { onThemeModeChanged(AppThemeMode.DARK) },
                    languageMode = currentLanguageMode
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Translation Mode Section Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentLanguageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Translation Mode" else "अनुवाद भाषा मोड (Translation Mode)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Language Pair Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                SettingsOptionRow(
                    titleHindi = "हिन्दी से संथाली",
                    titleEnglish = "Hindi to Santali",
                    description = "Hindi speech & prompts translate into Santali",
                    icon = Icons.Default.Translate,
                    isSelected = currentLanguageMode == LanguagePairMode.HINDI_TO_SANTALI,
                    onClick = { onLanguageModeChanged(LanguagePairMode.HINDI_TO_SANTALI) },
                    languageMode = currentLanguageMode
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                SettingsOptionRow(
                    titleHindi = "अंग्रेज़ी से संथाली",
                    titleEnglish = "English to Santali",
                    description = "English speech & prompts match directly with 20K corpus",
                    icon = Icons.Default.Language,
                    isSelected = currentLanguageMode == LanguagePairMode.ENGLISH_TO_SANTALI,
                    onClick = { onLanguageModeChanged(LanguagePairMode.ENGLISH_TO_SANTALI) },
                    languageMode = currentLanguageMode
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About App Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PalashSaathi v1.0",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ᱯᱟᱞᱟᱥ ᱥᱟᱛᱷᱤ • Vernacular Pedagogy",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "20,000 Santali Parallel Sentences Indexed (Offline)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsOptionRow(
    titleHindi: String,
    titleEnglish: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    languageMode: LanguagePairMode = LanguagePairMode.HINDI_TO_SANTALI
) {
    val primaryTitle = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) titleEnglish else titleHindi
    val secondaryTitle = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "($titleHindi)" else "($titleEnglish)"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = titleEnglish,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = primaryTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = secondaryTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
