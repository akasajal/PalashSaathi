package com.palashsaathi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.SantaliCorpusRepository
import com.palashsaathi.app.data.model.LanguagePairMode
import com.palashsaathi.app.data.model.ScriptType

enum class PhrasebookMode {
    CLASSROOM_PROMPTS,
    CORPUS_EXPLORER
}

@Composable
fun PhrasebookScreen(
    currentScript: ScriptType,
    languageMode: LanguagePairMode = LanguagePairMode.HINDI_TO_SANTALI,
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
                it.sourceEnglish.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliDevanagari.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliPhonetic.contains(searchQuery, ignoreCase = true) ||
                it.targetSantaliOlChiki.contains(searchQuery)
            }
        }
    }

    // Filtered 20K corpus sentences
    val filteredCorpus = remember(selectedMode, searchQuery, isCorpusLoaded) {
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
        // Child & Teacher-friendly header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                        "Santali Phrasebook"
                    else
                        "संथाली शब्दावली (Phrasebook)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                        "Search classroom phrases & 20,000 sentences"
                    else
                        "कक्षा निर्देश और 20,000 संथाली वाक्यों का कोष",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Switcher Chips with Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedMode == PhrasebookMode.CLASSROOM_PROMPTS,
                onClick = { selectedMode = PhrasebookMode.CLASSROOM_PROMPTS },
                label = { Text(if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Classroom Prompts" else "कक्षा निर्देश") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = selectedMode == PhrasebookMode.CORPUS_EXPLORER,
                onClick = { selectedMode = PhrasebookMode.CORPUS_EXPLORER },
                label = {
                    Text(if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "20K Sentences" else "20K वाक्य कोष")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Capsule Search Bar (28dp rounded)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    if (selectedMode == PhrasebookMode.CLASSROOM_PROMPTS) {
                        if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                            "Search classroom prompts..."
                        else
                            "कक्षा निर्देश खोजें..."
                    } else {
                        if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                            "Search 20K corpus: water, school, tree..."
                        else
                            "20,000 वाक्य खोजें: water, school..."
                    },
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedMode == PhrasebookMode.CLASSROOM_PROMPTS) {
            // Classroom Prompts List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredClassroom) { phrase ->
                    val playAction = {
                        onSpeakSantaliAudio(phrase.targetSantaliPhonetic, phrase.targetSantaliDevanagari)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { playAction() }
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = phrase.getSource(languageMode),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val altText = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) phrase.sourceHindi else phrase.sourceEnglish
                                    if (altText.isNotBlank()) {
                                        Text(
                                            text = altText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (currentScript == ScriptType.OL_CHIKI) phrase.targetSantaliOlChiki else phrase.targetSantaliDevanagari,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    // Pronunciation pill
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Hearing,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = phrase.targetSantaliPhonetic,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // 44dp prominent audio button
                                FilledIconButton(
                                    onClick = playAction,
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Clean Dialect Pill at Bottom
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ho: ${phrase.subtitleHo}  •  Mundari: ${phrase.subtitleMundari}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Loading 20,000 Santali sentences..." else "20,000 संथाली वाक्यों का कोष लोड हो रहा है...",
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
                        val playAction = {
                            onSpeakSantaliAudio(sentence.santaliPhonetic, sentence.santaliDevanagari)
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { playAction() }
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
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
                                                text = sentence.santaliPhonetic,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    FilledIconButton(
                                        onClick = playAction,
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Speak",
                                            modifier = Modifier.size(20.dp)
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
