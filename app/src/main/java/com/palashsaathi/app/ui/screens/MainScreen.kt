package com.palashsaathi.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.R
import com.palashsaathi.app.data.AppPreferencesRepository
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.engine.AudioSynthesisEngine
import com.palashsaathi.app.ui.theme.*
import kotlinx.coroutines.launch

enum class AppTab(
    val labelHindi: String,
    val labelEnglish: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    VOICE("बोलें", "Voice", Icons.Default.Mic),
    WORKSHEET("अभ्यास", "Worksheet", Icons.Default.Description),
    CARDS("फ्लैशकार्ड", "Cards", Icons.Default.Style),
    PHRASEBOOK("वाक्य", "Vocab", Icons.Default.MenuBook),
    SETTINGS("सेटिंग्स", "Settings", Icons.Default.Settings);

    val label: String get() = labelHindi

    fun getLabel(mode: com.palashsaathi.app.data.model.LanguagePairMode): String {
        return if (mode == com.palashsaathi.app.data.model.LanguagePairMode.ENGLISH_TO_SANTALI) labelEnglish else labelHindi
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    audioEngine: AudioSynthesisEngine,
    prefsRepository: AppPreferencesRepository,
    modifier: Modifier = Modifier
) {
    val currentThemeMode by prefsRepository.themeMode.collectAsState()
    val currentLanguageMode by prefsRepository.languageMode.collectAsState()
    val currentScript by prefsRepository.scriptType.collectAsState()
    val showLandingSynopsis by prefsRepository.showLandingSynopsis.collectAsState()

    var selectedTab by remember { mutableStateOf(AppTab.VOICE) }
    val coroutineScope = rememberCoroutineScope()

    val onSpeakSantaliAudio: (String, String) -> Unit = { phonetic, devanagari ->
        coroutineScope.launch {
            audioEngine.speakSantali(phonetic, devanagari)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Transparent,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PalashSaathi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(4.dp)
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
                                    text = if (currentLanguageMode == com.palashsaathi.app.data.model.LanguagePairMode.HINDI_TO_SANTALI)
                                        "हिन्दी → संथाली • 20K Corpus"
                                    else
                                        "English → Santali • 20K Corpus",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                )
                            }
                        }
                    },
                actions = {
                    // Script Switcher Button (Ol Chiki <-> Devanagari)
                    OutlinedButton(
                        onClick = {
                            val nextScript = if (currentScript == ScriptType.OL_CHIKI) {
                                ScriptType.DEVANAGARI
                            } else {
                                ScriptType.OL_CHIKI
                            }
                            prefsRepository.setScriptType(nextScript)
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
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.getLabel(currentLanguageMode)) },
                        label = {
                            Text(
                                text = tab.getLabel(currentLanguageMode),
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
                    languageMode = currentLanguageMode,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.WORKSHEET -> WorksheetScreen(
                    currentScript = currentScript,
                    languageMode = currentLanguageMode
                )
                AppTab.FLASHCARDS -> FlashcardsScreen(
                    currentScript = currentScript,
                    languageMode = currentLanguageMode,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.PHRASEBOOK -> PhrasebookScreen(
                    currentScript = currentScript,
                    languageMode = currentLanguageMode,
                    onSpeakSantaliAudio = onSpeakSantaliAudio
                )
                AppTab.SETTINGS -> SettingsScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = { prefsRepository.setThemeMode(it) },
                    currentLanguageMode = currentLanguageMode,
                    onLanguageModeChanged = { prefsRepository.setLanguageMode(it) }
                )
            }
        }
    }

        AnimatedVisibility(
            visible = showLandingSynopsis,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(600))
        ) {
            LandingSynopsisScreen(
                onDismiss = { prefsRepository.dismissLandingSynopsis() }
            )
        }
    }
}
