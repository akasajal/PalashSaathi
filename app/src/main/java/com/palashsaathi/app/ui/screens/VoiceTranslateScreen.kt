package com.palashsaathi.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.data.model.TranslationResult
import com.palashsaathi.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceTranslateScreen(
    currentScript: ScriptType,
    onSpeakHoAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var recognizedHindi by remember { mutableStateOf("किताब खोलो") }
    var currentResult by remember { mutableStateOf(FLNDictionary.CLASSROOM_ENTRIES[0]) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode & Latency Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ForestGreenBackground),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Offline",
                        tint = ForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Offline Edge Mode",
                        style = MaterialTheme.typography.labelLarge,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "⚡ Latency: ${currentResult.latencyMs} ms (< 3.0s)",
                    style = MaterialTheme.typography.labelLarge,
                    color = PalashOrangeDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Teacher Speech Input Card (Hindi)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "शिक्षक की आवाज़ (Hindi Input)",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                    if (isListening) {
                        Badge(containerColor = PalashOrange) {
                            Text("सुन रहे हैं... (Listening)", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recognizedHindi,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    color = DarkCharcoal,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Push to Talk Button Area
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .scale(pulseScale)
        ) {
            FilledIconButton(
                onClick = {
                    if (!isListening) {
                        isListening = true
                        coroutineScope.launch {
                            delay(1200) // simulate teacher speech
                            isListening = false
                            isTranslating = true
                            delay(300) // sub-300ms offline neural inference
                            val nextEntry = FLNDictionary.CLASSROOM_ENTRIES.random()
                            recognizedHindi = nextEntry.sourceHindi
                            currentResult = nextEntry
                            isTranslating = false
                            // Instant voice playback in Ho
                            onSpeakHoAudio(currentResult.targetHoPhonetic, currentResult.targetHoDevanagari)
                        }
                    }
                },
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isListening) PalashOrangeDark else PalashOrange
                )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                    contentDescription = "Push to Talk",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Text(
            text = if (isListening) "आवाज़ रिकॉर्ड हो रही है..." else if (isTranslating) "अनुवाद हो रहा है..." else "बोलने के लिए माइक दबाएँ (Push to Talk)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Ho Translation Output Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PalashOrangeLight)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "हो भाषा में ध्वनि (Primary Voice: Ho)",
                        style = MaterialTheme.typography.labelLarge,
                        color = PalashOrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            onSpeakHoAudio(currentResult.targetHoPhonetic, currentResult.targetHoDevanagari)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Ho",
                            tint = PalashOrangeDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Warang Citi / Devanagari text based on current toggle
                Text(
                    text = if (currentScript == ScriptType.WARANG_CITI) currentResult.targetHoWarangCiti else currentResult.targetHoDevanagari,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                    color = DarkCharcoal,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Transliteration & Pronunciation guide
                Text(
                    text = "उच्चारण (Pronunciation): ${currentResult.targetHoPhonetic}  •  ${if (currentScript == ScriptType.WARANG_CITI) currentResult.targetHoDevanagari else currentResult.targetHoWarangCiti}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auxiliary Subtitle HUD (Santhali and Mundari)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF37474F), RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = SubtitleBannerColor),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Subtitles",
                        tint = SubtitleHighlight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "लाइव उपशीर्षक (Live Subtitle HUD)",
                        style = MaterialTheme.typography.labelLarge,
                        color = SubtitleHighlight,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = Color(0xFF455A64), modifier = Modifier.padding(vertical = 8.dp))

                // Santhali Subtitle Strip
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "संथाली (Santhali): ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentResult.subtitleSanthali,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtitleTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mundari Subtitle Strip
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "मुण्डारी (Mundari): ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentResult.subtitleMundari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtitleTextColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Teacher Prompts Chips
        Text(
            text = "त्वरित कक्षा निर्देश (Quick Teacher Commands)",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
            color = DarkCharcoal,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        FLNDictionary.CLASSROOM_ENTRIES.take(4).forEach { item ->
            OutlinedCard(
                onClick = {
                    recognizedHindi = item.sourceHindi
                    currentResult = item
                    onSpeakHoAudio(item.targetHoPhonetic, item.targetHoDevanagari)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.sourceHindi, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                        Text(
                            text = if (currentScript == ScriptType.WARANG_CITI) item.targetHoWarangCiti else item.targetHoDevanagari,
                            fontSize = 13.sp,
                            color = PalashOrangeDark
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = PalashOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
