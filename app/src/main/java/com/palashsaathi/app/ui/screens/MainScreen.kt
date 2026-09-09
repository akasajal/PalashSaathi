package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.engine.AudioSynthesisEngine
import com.palashsaathi.app.ui.theme.*
import kotlinx.coroutines.launch

enum class AppTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    VOICE("बोलें", Icons.Default.Mic),
    WORKSHEET("अभ्यास", Icons.Default.Description),
    FLASHCARDS("कार्ड", Icons.Default.Style),
    PHRASEBOOK("शब्दावली", Icons.Default.MenuBook),
    SETTINGS("सेटिंग्स", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    audioEngine: AudioSynthesisEngine,
    currentThemeMode: AppThemeMode,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.VOICE) }
    var currentScript by remember { mutableStateOf(ScriptType.OL_CHIKI) }
    val coroutineScope = rememberCoroutineScope()

    val onSpeakSantaliAudio: (String, String) -> Unit = { phonetic, devanagari ->
        coroutineScope.launch {
            audioEngine.speakSantali(phonetic, devanagari)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PalashSaathi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Offline",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "AI Vernacular Pedagogy • Santali (ᱥᱟᱱᱛᱟᱲᱤ)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    // Script Switcher Button (Ol Chiki <-> Devanagari)
                    OutlinedButton(
                        onClick = {
                            currentScript = if (currentScript == ScriptType.OL_CHIKI) {
                                ScriptType.DEVANAGARI
                            } else {
                                ScriptType.OL_CHIKI
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onPrimary)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Switch Script",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentScript == ScriptType.OL_CHIKI) "ᱚᱞ ᱪᱤᱠᱤ" else "देवनागरी",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.VOICE -> VoiceTranslateScreen(
                    currentScript = currentScript,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.WORKSHEET -> WorksheetScreen(
                    currentScript = currentScript
                )
                AppTab.FLASHCARDS -> FlashcardsScreen(
                    currentScript = currentScript,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.PHRASEBOOK -> PhrasebookScreen(
                    currentScript = currentScript,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.SETTINGS -> SettingsScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = onThemeModeChanged
                )
            }
        }
    }
}
