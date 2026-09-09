package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.model.FLNCategory
import com.palashsaathi.app.data.model.FlashcardItem
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.ui.theme.*

@Composable
fun FlashcardsScreen(
    currentScript: ScriptType,
    onSpeakHoAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<FLNCategory?>(null) }

    val filteredItems = remember(selectedFilter) {
        if (selectedFilter == null) {
            FLNDictionary.FLASHCARD_ITEMS
        } else {
            FLNDictionary.FLASHCARD_ITEMS.filter { it.category == selectedFilter }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightSurface)
            .padding(16.dp)
    ) {
        Text(
            text = "चित्र व शब्द फ्लैशकार्ड (Visual Flashcards)",
            style = MaterialTheme.typography.titleLarge,
            color = DarkCharcoal,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap the speaker icon to hear authentic Ho pronunciation offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips: All, Numeracy, Literacy
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                label = { Text("सभी (All)") }
            )
            FilterChip(
                selected = selectedFilter == FLNCategory.NUMERACY,
                onClick = { selectedFilter = FLNCategory.NUMERACY },
                label = { Text("संख्या (Numeracy)") }
            )
            FilterChip(
                selected = selectedFilter == FLNCategory.LITERACY,
                onClick = { selectedFilter = FLNCategory.LITERACY },
                label = { Text("भाषा (Literacy)") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredItems) { item ->
                FlashcardCard(
                    item = item,
                    currentScript = currentScript,
                    onSpeak = { onSpeakHoAudio(item.hoPhonetic, item.hoDevanagari) }
                )
            }
        }
    }
}

@Composable
fun FlashcardCard(
    item: FlashcardItem,
    currentScript: ScriptType,
    onSpeak: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderOutline, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illustration Icon / Emoji
            Text(
                text = item.emojiOrIcon,
                fontSize = 44.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Hindi Word
            Text(
                text = item.hindiWord,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Ho Word in Warang Citi or Devanagari
            Text(
                text = if (currentScript == ScriptType.WARANG_CITI) item.hoWarangCiti else item.hoDevanagari,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                fontWeight = FontWeight.ExtraBold,
                color = PalashOrangeDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = "उच्चारण: ${item.hoPhonetic}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle chips for Santhali & Mundari
            Surface(
                color = Color(0xFFECEFF1),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(
                        text = "संथाली: ${item.santhaliSubtitle}",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "मुण्डारी: ${item.mundariSubtitle}",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio button
            FilledIconButton(
                onClick = onSpeak,
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = ForestGreenBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Pronounce Ho",
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
