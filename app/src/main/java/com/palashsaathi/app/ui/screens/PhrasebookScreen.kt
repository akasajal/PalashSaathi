package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.ui.theme.*

@Composable
fun PhrasebookScreen(
    currentScript: ScriptType,
    onSpeakHoAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredPhrases = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            FLNDictionary.CLASSROOM_ENTRIES
        } else {
            FLNDictionary.CLASSROOM_ENTRIES.filter {
                it.sourceHindi.contains(searchQuery, ignoreCase = true) ||
                it.targetHoDevanagari.contains(searchQuery, ignoreCase = true) ||
                it.targetHoPhonetic.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightSurface)
            .padding(16.dp)
    ) {
        Text(
            text = "कक्षा निर्देश शब्दावली (Classroom Phrasebook)",
            style = MaterialTheme.typography.titleLarge,
            color = DarkCharcoal,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Essential phrases for non-native teachers to guide students in Ho.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("खोजें (Search Hindi / Ho phrases)...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredPhrases) { phrase ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderOutline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = phrase.sourceHindi,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkCharcoal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (currentScript == ScriptType.WARANG_CITI) phrase.targetHoWarangCiti else phrase.targetHoDevanagari,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                                    color = PalashOrangeDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "उच्चारण: ${phrase.targetHoPhonetic}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = {
                                    onSpeakHoAudio(phrase.targetHoPhonetic, phrase.targetHoDevanagari)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = PalashOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderOutline)

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "संथाली: ${phrase.subtitleSanthali}  •  मुण्डारी: ${phrase.subtitleMundari}",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
