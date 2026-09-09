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
    VOICE("बोलें (Voice)", Icons.Default.Mic),
    WORKSHEET("अभ्यास पत्र (Sheets)", Icons.Default.Description),
    FLASHCARDS("फ्लैशकार्ड (Cards)", Icons.Default.Style),
    PHRASEBOOK("शब्दावली (Phrases)", Icons.Default.MenuBook)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    audioEngine: AudioSynthesisEngine,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.VOICE) }
    var currentScript by remember { mutableStateOf(ScriptType.WARANG_CITI) }
    val coroutineScope = rememberCoroutineScope()

    val onSpeakHoAudio: (String, String) -> Unit = { phonetic, devanagari ->
        coroutineScope.launch {
            audioEngine.speakHo(phonetic, devanagari)
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
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = ForestGreenLight,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Offline",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "AI Vernacular Pedagogy • Ho (हो)",
                            fontSize = 12.sp,
                            color = Color(0xFFFFE0B2)
                        )
                    }
                },
                actions = {
                    // Script Switcher Button
                    OutlinedButton(
                        onClick = {
                            currentScript = if (currentScript == ScriptType.WARANG_CITI) {
                                ScriptType.DEVANAGARI
                            } else {
                                ScriptType.WARANG_CITI
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White)),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Switch Script",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentScript == ScriptType.WARANG_CITI) "𑣓𑣉𑣎𑣉𑣜" else "देवनागरी",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PalashOrange)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                tonalElevation = 8.dp
            ) {
                AppTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(text = tab.label, fontSize = 11.sp, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PalashOrangeDark,
                            selectedTextColor = PalashOrangeDark,
                            indicatorColor = Color(0xFFFFE0B2)
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
                    onSpeakHoAudio = onSpeakHoAudio
                )
                AppTab.WORKSHEET -> WorksheetScreen(
                    currentScript = currentScript
                )
                AppTab.FLASHCARDS -> FlashcardsScreen(
                    currentScript = currentScript,
                    onSpeakHoAudio = onSpeakHoAudio
                )
                AppTab.PHRASEBOOK -> PhrasebookScreen(
                    currentScript = currentScript,
                    onSpeakHoAudio = onSpeakHoAudio
                )
            }
        }
    }
}
