package com.palashsaathi.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.core.content.ContextCompat
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.model.LanguagePairMode
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.data.model.TranslationResult
import com.palashsaathi.app.engine.AcousticKeywordSpotter
import com.palashsaathi.app.engine.AudioRecordEngine
import com.palashsaathi.app.engine.DynamicTranslationEngine
import com.palashsaathi.app.engine.VoiceRecognitionEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceTranslateScreen(
    currentScript: ScriptType,
    languageMode: LanguagePairMode = LanguagePairMode.HINDI_TO_SANTALI,
    onSpeakSantaliAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val voiceRecognitionEngine = remember { VoiceRecognitionEngine(context) }
    val audioRecordEngine = remember { AudioRecordEngine() }

    var isListening by remember { mutableStateOf(false) }
    var liveAmplitude by remember { mutableStateOf(0f) }
    var isTranslating by remember { mutableStateOf(false) }
    var isEditingText by remember { mutableStateOf(false) }
    var customInputText by remember { mutableStateOf("") }
    var recognizedSourceText by remember(languageMode) {
        mutableStateOf(
            if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                FLNDictionary.CLASSROOM_ENTRIES[0].sourceEnglish
            else
                FLNDictionary.CLASSROOM_ENTRIES[0].sourceHindi
        )
    }
    var currentResult by remember(languageMode) { mutableStateOf(FLNDictionary.CLASSROOM_ENTRIES[0]) }
    var showDialects by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            voiceRecognitionEngine.destroy()
            audioRecordEngine.stopRecording(cancel = true)
        }
    }

    // In-App Speech Recognition Controller (Zero Google modal popups, true verbal word decoding)
    fun startInAppSpeechRecognition() {
        if (isListening) {
            voiceRecognitionEngine.stopListening()
            audioRecordEngine.stopRecording(cancel = false)
            isListening = false
            liveAmplitude = 0f
            return
        }

        isListening = true
        liveAmplitude = 0f

        if (voiceRecognitionEngine.isRecognitionAvailable) {
            voiceRecognitionEngine.startListening(
                languageMode = languageMode,
                onAmplitude = { amp ->
                    liveAmplitude = amp
                },
                onPartial = { interimText ->
                    recognizedSourceText = interimText
                },
                onResult = { spokenText ->
                    isListening = false
                    liveAmplitude = 0f
                    recognizedSourceText = spokenText
                    isTranslating = true

                    coroutineScope.launch {
                        val translation = DynamicTranslationEngine.translate(spokenText, languageMode)
                        currentResult = translation
                        isTranslating = false
                        onSpeakSantaliAudio(translation.targetSantaliPhonetic, translation.targetSantaliDevanagari)
                    }
                },
                onError = { errorMsg ->
                    isListening = false
                    liveAmplitude = 0f
                    Log.w("VoiceTranslateScreen", "Speech recognition error: $errorMsg")
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // High-precision acoustic feature extraction fallback using raw PCM AudioRecord & AcousticKeywordSpotter
            audioRecordEngine.startRecording(
                onAmplitude = { amp ->
                    liveAmplitude = amp
                },
                onSpeechFinished = { pcmBytes, durationMs ->
                    isListening = false
                    liveAmplitude = 0f
                    isTranslating = true

                    coroutineScope.launch {
                        val candidate = if (customInputText.isNotBlank() && isEditingText) {
                            customInputText.trim()
                        } else {
                            AcousticKeywordSpotter.classifyPcmAudio(pcmBytes, durationMs, languageMode)
                        }

                        recognizedSourceText = candidate
                        val translation = DynamicTranslationEngine.translate(candidate, languageMode)
                        currentResult = translation
                        isTranslating = false
                        onSpeakSantaliAudio(translation.targetSantaliPhonetic, translation.targetSantaliDevanagari)
                    }
                }
            )
        }
    }

    // Runtime Permission Launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startInAppSpeechRecognition()
        } else {
            Toast.makeText(
                context,
                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                    "Microphone permission is required to record voice"
                else
                    "आवाज़ रिकॉर्ड करने के लिए माइक्रोफ़ोन की अनुमति चाहिए",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Live acoustic wave scale computed directly from real-time microphone decibels
    val liveMicScale = 1f + (liveAmplitude * 0.45f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Teacher Speech Bubble (Conversational Card with Avatar & Live Edit/Type field)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Teacher says:" else "शिक्षक की आवाज़:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isListening) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(
                                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Listening..." else "सुन रहे हैं...",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        // Edit / Keyboard toggle button
                        IconButton(
                            onClick = {
                                isEditingText = !isEditingText
                                if (isEditingText && customInputText.isEmpty()) {
                                    customInputText = recognizedSourceText
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingText) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Type or edit phrase",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isEditingText) {
                    // Editable text input mode
                    OutlinedTextField(
                        value = customInputText,
                        onValueChange = { customInputText = it },
                        placeholder = {
                            Text(
                                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                                    "Type phrase (e.g. open book / drink water)..."
                                else
                                    "वाक्य टाइप करें (उदा. किताब खोलो / पानी पियो)...",
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                val toTranslate = customInputText.trim()
                                if (toTranslate.isNotBlank()) {
                                    recognizedSourceText = toTranslate
                                    isEditingText = false
                                    isTranslating = true
                                    coroutineScope.launch {
                                        val res = DynamicTranslationEngine.translate(toTranslate, languageMode)
                                        currentResult = res
                                        isTranslating = false
                                        onSpeakSantaliAudio(res.targetSantaliPhonetic, res.targetSantaliDevanagari)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Translate" else "अनुवाद करें",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Display recognized text
                    Text(
                        text = recognizedSourceText,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Large, Joyful Push to Talk Mic Area
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(116.dp)
                .scale(if (isListening) liveMicScale else 1f)
        ) {
            FilledIconButton(
                onClick = {
                    if (isListening) {
                        startInAppSpeechRecognition()
                    } else {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            startInAppSpeechRecognition()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isListening) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                    contentDescription = "Push to Talk",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        Text(
            text = if (isListening) {
                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Listening to speech..." else "आवाज़ रिकॉर्ड हो रही है..."
            } else if (isTranslating) {
                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Translating..." else "अनुवाद हो रहा है..."
            } else if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) {
                "Tap mic to speak"
            } else {
                "बोलने के लिए माइक दबाएँ"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Cheerful Santali Voice Output Card (Hero Card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Santali Voice" else "संथाली आवाज़",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    FilledIconButton(
                        onClick = {
                            onSpeakSantaliAudio(currentResult.targetSantaliPhonetic, currentResult.targetSantaliDevanagari)
                        },
                        modifier = Modifier.size(44.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Santali",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ol Chiki / Devanagari prominent text
                Text(
                    text = if (currentScript == ScriptType.OL_CHIKI) currentResult.targetSantaliOlChiki else currentResult.targetSantaliDevanagari,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Friendly pronunciation and dual-script block (clean column layout, zero vertical letter squeeze)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hearing,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentResult.targetSantaliPhonetic,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val altScript = if (currentScript == ScriptType.OL_CHIKI) {
                            currentResult.targetSantaliDevanagari
                        } else {
                            currentResult.targetSantaliOlChiki
                        }

                        if (altScript.isNotBlank() && altScript != currentResult.targetSantaliPhonetic) {
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                thickness = 0.8.dp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (currentScript == ScriptType.OL_CHIKI) "देवनागरी लिपि:" else "ᱚᱞ ᱪᱤᱠᱤ:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = altScript,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Optional Expandable Dialect Notes (Ho & Mundari) - clean and unobtrusive!
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDialects = !showDialects },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Related Dialects (Ho & Mundari)" else "संबंधित बोलियाँ (हो और मुण्डारी)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = if (showDialects) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Dialects",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (showDialects) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Ho: " else "हो: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentResult.subtitleHo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Mundari: " else "मुण्डारी: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentResult.subtitleMundari,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Quick Classroom Prompts (Horizontal Carousel with Playful Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Quick Classroom Phrases" else "त्वरित कक्षा निर्देश",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FLNDictionary.CLASSROOM_ENTRIES.take(6).forEach { item ->
                Card(
                    onClick = {
                        recognizedSourceText = item.getSource(languageMode)
                        currentResult = item
                        onSpeakSantaliAudio(item.targetSantaliPhonetic, item.targetSantaliDevanagari)
                    },
                    modifier = Modifier.width(170.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.getSource(languageMode),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentScript == ScriptType.OL_CHIKI) item.targetSantaliOlChiki else item.targetSantaliDevanagari,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 6. Section 2: 20K Dataset Sample Sentences (Clean Card List)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Dataset,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "20K Corpus Samples" else "20K वाक्य कोष उदाहरण",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        FLNDictionary.CORPUS_FEATURED_SENTENCES.take(3).forEach { sentence ->
            Card(
                onClick = {
                    recognizedSourceText = sentence.english
                    currentResult = TranslationResult(
                        sourceHindi = sentence.english,
                        sourceEnglish = sentence.english,
                        targetSantaliOlChiki = sentence.santaliOlChiki,
                        targetSantaliDevanagari = sentence.santaliDevanagari,
                        targetSantaliPhonetic = sentence.santaliPhonetic,
                        subtitleHo = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Ho: ${sentence.santaliPhonetic}" else "हो: ${sentence.santaliPhonetic}",
                        subtitleMundari = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Mundari: ${sentence.santaliPhonetic}" else "मुण्डारी: ${sentence.santaliPhonetic}",
                        latencyMs = 190L,
                        fromCorpus = true
                    )
                    onSpeakSantaliAudio(sentence.santaliPhonetic, sentence.santaliDevanagari)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sentence.english,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentScript == ScriptType.OL_CHIKI) sentence.santaliOlChiki else sentence.santaliDevanagari,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
