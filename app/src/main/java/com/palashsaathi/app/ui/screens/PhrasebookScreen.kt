package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.SantaliCorpusRepository
import com.palashsaathi.app.data.model.ScriptType

enum class PhrasebookMode {
    CLASSROOM_PROMPTS,
    CORPUS_EXPLORER
}

@Composable
fun PhrasebookScreen(
    currentScript: ScriptType,
    onSpeakSantaliAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(PhrasebookMode.CLASSROOM_PROMPTS) }
    var searchQuery by remember { mutableStateOf("") }
    val isCorpusLoaded by SantaliCorpusRepository.isLoaded.collectAsState()

    // Filtered classroom entries
    val filteredClassroom = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            FLNDictionary.CLASSROOM_ENTRIES
        } else {
            FLNDictionary.CLASSROOM_ENTRIES.filter {
                it.sourceHindi.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliDevanagari.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliPhonetic.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliOlChiki.contains(searchQuery)
            }
        }
    }

    // Filtered 20K corpus sentences
    val filteredCorpus = remember(searchQuery, isCorpusLoaded) {
        if (selectedMode == PhrasebookMode.CORPUS_EXPLORER) {
            SantaliCorpusRepository.search(searchQuery, limit = 40)
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "संथाली शब्दावली व वाक्य कोष (Santali Phrasebook & Corpus)",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Essential classroom prompts and 20,000 offline Santali sentences from the training corpus.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Switcher: Classroom Prompts vs 20K Corpus Explorer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedMode == PhrasebookMode.CLASSROOM_PROMPTS,
                onClick = { selectedMode = PhrasebookMode.CLASSROOM_PROMPTS },
                label = { Text("कक्षा निर्देश (Classroom)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            FilterChip(
                selected = selectedMode == PhrasebookMode.CORPUS_EXPLORER,
                onClick = { selectedMode = PhrasebookMode.CORPUS_EXPLORER },
                label = {
                    Text("20K वाक्य कोष (Corpus Explorer)")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    if (selectedMode == PhrasebookMode.CLASSROOM_PROMPTS)
                        "खोजें (Search Hindi / Santali)..."
                    else
                        "खोजें (Search 20,000 sentences: school, book, tree...)"
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMode == PhrasebookMode.CLASSROOM_PROMPTS) {
            // Classroom Prompts List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredClassroom) { phrase ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (currentScript == ScriptType.OL_CHIKI) phrase.targetSantaliOlChiki else phrase.targetSantaliDevanagari,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "उच्चारण: ${phrase.targetSantaliPhonetic}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onSpeakSantaliAudio(phrase.targetSantaliPhonetic, phrase.targetSantaliDevanagari)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "हो: ${phrase.subtitleHo}  •  मुण्डारी: ${phrase.subtitleMundari}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // 20K Corpus Explorer List
            if (!isCorpusLoaded) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "20,000 संथाली वाक्यों का कोष लोड हो रहा है...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCorpus) { sentence ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                            text = sentence.english,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (currentScript == ScriptType.OL_CHIKI) sentence.santaliOlChiki else sentence.santaliDevanagari,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (sentence.santaliPhonetic.isNotBlank()) {
                                            Text(
                                                text = "Phonetic: ${sentence.santaliPhonetic}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            onSpeakSantaliAudio(sentence.santaliPhonetic, sentence.santaliDevanagari)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Speak",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
