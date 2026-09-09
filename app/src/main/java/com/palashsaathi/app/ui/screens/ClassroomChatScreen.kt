package com.palashsaathi.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.palashsaathi.app.data.FLNDictionary
import com.palashsaathi.app.data.model.ClassroomChatMessage
import com.palashsaathi.app.data.model.LanguagePairMode
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.data.model.TranslationResult
import com.palashsaathi.app.engine.AcousticKeywordSpotter
import com.palashsaathi.app.engine.AudioRecordEngine
import com.palashsaathi.app.engine.DynamicTranslationEngine
import com.palashsaathi.app.engine.VoiceRecognitionEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * One-Way Classroom Communication Screen.
 *
 * Teacher speaks or types instructions in Hindi or English.
 * Students receive the translated message in tribal language (Santali in Ol Chiki and Devanagari),
 * accompanied by immediate audio broadcast, phonetics, and dialect notes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomChatScreen(
    currentScript: ScriptType,
    languageMode: LanguagePairMode,
    onSpeakSantaliAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val voiceRecognitionEngine = remember { VoiceRecognitionEngine(context) }
    val audioRecordEngine = remember { AudioRecordEngine() }

    var isListening by remember { mutableStateOf(false) }
    var liveAmplitude by remember { mutableStateOf(0f) }
    var isTranslating by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var autoSpeakEnabled by remember { mutableStateOf(true) }
    var showStudentBoard by remember { mutableStateOf(false) }
    var fallbackToAcousticForHindi by remember { mutableStateOf(false) }

    // Initial greeting message in classroom stream
    val initialMessage = remember(languageMode) {
        val defaultEntry = FLNDictionary.CLASSROOM_ENTRIES[0]
        ClassroomChatMessage(
            sourceText = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Hello students, open your book" else "नमस्ते बच्चों, किताब खोलो",
            sourceLanguage = languageMode,
            translation = defaultEntry
        )
    }

    val chatMessages = remember { mutableStateListOf(initialMessage) }

    DisposableEffect(Unit) {
        onDispose {
            voiceRecognitionEngine.destroy()
            audioRecordEngine.stopRecording(cancel = true)
        }
    }

    // Core message delivery: translates Hindi/English -> Santali and broadcasts to students
    fun deliverMessage(source: String) {
        val trimmed = source.trim()
        if (trimmed.isBlank()) return

        isTranslating = true
        coroutineScope.launch {
            val result = DynamicTranslationEngine.translate(trimmed, languageMode)
            val message = ClassroomChatMessage(
                sourceText = trimmed,
                sourceLanguage = languageMode,
                translation = result
            )
            chatMessages.add(message)
            isTranslating = false

            // Auto-scroll to newest message
            if (chatMessages.isNotEmpty()) {
                lazyListState.animateScrollToItem(chatMessages.size - 1)
            }

            // Auto-speak tribal speech if enabled
            if (autoSpeakEnabled) {
                onSpeakSantaliAudio(result.targetSantaliPhonetic, result.targetSantaliDevanagari)
            }
        }
    }

    // High-precision acoustic feature extraction fallback
    fun startAcousticRecording() {
        voiceRecognitionEngine.cancel()
        isListening = true
        liveAmplitude = 0f
        audioRecordEngine.startRecording(
            onAmplitude = { amp -> liveAmplitude = amp },
            onSpeechFinished = { pcmBytes, durationMs ->
                isListening = false
                liveAmplitude = 0f
                val candidate = AcousticKeywordSpotter.classifyPcmAudio(pcmBytes, durationMs, languageMode)
                deliverMessage(candidate)
            }
        )
    }

    // Voice recognition launcher with robust two-tier fallback
    fun startInAppSpeechRecognition() {
        if (isListening || audioRecordEngine.isRecording) {
            voiceRecognitionEngine.stopListening()
            audioRecordEngine.stopRecording(cancel = false)
            isListening = false
            liveAmplitude = 0f
            return
        }

        if (languageMode == LanguagePairMode.HINDI_TO_SANTALI && fallbackToAcousticForHindi) {
            startAcousticRecording()
            return
        }

        if (voiceRecognitionEngine.isRecognitionAvailable) {
            isListening = true
            liveAmplitude = 0f

            voiceRecognitionEngine.startListening(
                languageMode = languageMode,
                onAmplitude = { amp -> liveAmplitude = amp },
                onPartial = { /* Optional partial display */ },
                onResult = { spokenText ->
                    isListening = false
                    liveAmplitude = 0f
                    deliverMessage(spokenText)
                },
                onError = { errorCode, errorMsg ->
                    isListening = false
                    liveAmplitude = 0f
                    if (errorCode == 13 || errorCode == 12 || errorCode == 4 || errorCode == 5 || errorCode == 2) {
                        if (languageMode == LanguagePairMode.HINDI_TO_SANTALI) {
                            fallbackToAcousticForHindi = true
                            Toast.makeText(
                                context,
                                "ऑफ़लाइन ध्वनि पहचान सक्रिय की जा रही है...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Switching to on-device acoustic recognition...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        startAcousticRecording()
                    } else {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            startAcousticRecording()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startInAppSpeechRecognition()
        } else {
            Toast.makeText(
                context,
                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                    "Microphone permission required to speak"
                else
                    "बोलने के लिए माइक्रोफ़ोन की अनुमति चाहिए",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Classroom quick prompts bank for 1-tap teacher broadcast
    val quickPrompts = remember(languageMode) {
        if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) {
            listOf(
                "Open your book", "Listen carefully", "Be quiet", "Come here",
                "Sit down", "Stand up", "Look at blackboard", "Well done",
                "Tell your name", "Write in notebook", "Drink water", "Hello"
            )
        } else {
            listOf(
                "किताब खोलो", "ध्यान से सुनो", "शांत रहो", "यहाँ आओ",
                "बैठ जाओ", "खड़े हो जाओ", "ब्लैकबोर्ड पर देखो", "शाबाश बहुत अच्छा",
                "अपना नाम बताओ", "कॉपी में लिखो", "पानी पियो", "नमस्ते"
            )
        }
    }

    val liveMicScale = 1f + (liveAmplitude * 0.45f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. One-Way Classroom Mode Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                                "Teacher → Student Broadcast"
                            else
                                "एकतरफ़ा संवाद (शिक्षक → विद्यार्थी)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                            "Teacher speaks English • Student receives Tribal Santali"
                        else
                            "शिक्षक हिन्दी बोलेंगे • विद्यार्थी संथाली में प्राप्त करेंगे",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Auto-speak Audio Toggle
                    IconButton(
                        onClick = { autoSpeakEnabled = !autoSpeakEnabled },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (autoSpeakEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Auto-Audio",
                            tint = if (autoSpeakEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Student Board Fullscreen View
                    IconButton(
                        onClick = { showStudentBoard = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Student Board View",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Clear Chat History
                    IconButton(
                        onClick = {
                            chatMessages.clear()
                            chatMessages.add(initialMessage)
                            Toast.makeText(
                                context,
                                if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Chat cleared" else "वार्तालाप साफ़ किया गया",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Quick Action Prompts Horizontal Chips Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Prompts:" else "त्वरित निर्देश:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                quickPrompts.forEach { prompt ->
                    SuggestionChip(
                        onClick = { deliverMessage(prompt) },
                        label = {
                            Text(
                                text = prompt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // 3. Continuous One-Way Chat Feed
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                ClassroomMessageItem(
                    message = msg,
                    currentScript = currentScript,
                    onPlayAudio = { phonetic, devanagari ->
                        onSpeakSantaliAudio(phonetic, devanagari)
                    }
                )
            }

            if (isTranslating) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                                "Translating to tribal language..."
                            else
                                "संथाली भाषा में अनुवाद हो रहा है...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 4. Bottom Push-To-Talk Mic & Text Entry Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Listening status badge
                if (isListening || audioRecordEngine.isRecording) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                                        "Listening to teacher... Tap mic again to send"
                                    else
                                        "शिक्षक की आवाज़ सुन रहे हैं... भेजने के लिए माइक दोबारा दबाएँ",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text Input Field for teacher typing
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                                    "Type instruction or tap mic..."
                                else
                                    "निर्देश टाइप करें या माइक दबाएँ...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp, max = 100.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3,
                        trailingIcon = {
                            if (inputText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val toSend = inputText
                                        inputText = ""
                                        deliverMessage(toSend)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Large Push-To-Talk Mic Button
                    FilledIconButton(
                        onClick = {
                            if (isListening || audioRecordEngine.isRecording) {
                                startInAppSpeechRecognition()
                            } else {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    startInAppSpeechRecognition()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .scale(if (isListening || audioRecordEngine.isRecording) liveMicScale else 1f),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isListening || audioRecordEngine.isRecording)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isListening || audioRecordEngine.isRecording)
                                Icons.Default.GraphicEq
                            else
                                Icons.Default.Mic,
                            contentDescription = "Speak instruction",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }

    // 5. Fullscreen Student Board Modal (Large display for classroom presentation)
    if (showStudentBoard && chatMessages.isNotEmpty()) {
        val latestMsg = chatMessages.last()
        StudentBoardDialog(
            message = latestMsg,
            currentScript = currentScript,
            onDismiss = { showStudentBoard = false },
            onPlayAudio = { phonetic, devanagari ->
                onSpeakSantaliAudio(phonetic, devanagari)
            }
        )
    }
}

/**
 * Single Turn in the One-Way Communication:
 * Upper bubble: Teacher's spoken/typed command in Hindi/English.
 * Lower card: Student's received message in Santali (Ol Chiki + Devanagari + Audio).
 */
@Composable
private fun ClassroomMessageItem(
    message: ClassroomChatMessage,
    currentScript: ScriptType,
    onPlayAudio: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // A. Teacher's Prompt Bubble (Sender)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                tonalElevation = 1.dp,
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (message.sourceLanguage == LanguagePairMode.ENGLISH_TO_SANTALI) "Teacher" else "शिक्षक",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.sourceText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // B. Student's Received Message Card (Receiver - Tribal Language Delivery)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.widthIn(max = 340.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header: Student receiver badge & replay speaker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (message.sourceLanguage == LanguagePairMode.ENGLISH_TO_SANTALI)
                                    "Student Received (Santali)"
                                else
                                    "विद्यार्थी प्राप्त संदेश (संथाली)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        FilledIconButton(
                            onClick = {
                                onPlayAudio(
                                    message.translation.targetSantaliPhonetic,
                                    message.translation.targetSantaliDevanagari
                                )
                            },
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play tribal voice",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Primary Script Display (Ol Chiki or Devanagari)
                    val primaryText = if (currentScript == ScriptType.OL_CHIKI)
                        message.translation.targetSantaliOlChiki
                    else
                        message.translation.targetSantaliDevanagari

                    Text(
                        text = primaryText,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Phonetic pronunciation & secondary script surface
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hearing,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = message.translation.targetSantaliPhonetic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            val secondaryText = if (currentScript == ScriptType.OL_CHIKI)
                                message.translation.targetSantaliDevanagari
                            else
                                message.translation.targetSantaliOlChiki

                            if (secondaryText.isNotBlank() && secondaryText != message.translation.targetSantaliPhonetic) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentScript == ScriptType.OL_CHIKI) "देवनागरी: $secondaryText" else "ᱚᱞ ᱪᱤᱠᱤ: $secondaryText",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (message.translation.subtitleHo.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ho / Mundari: ${message.translation.subtitleHo.split("(").first().trim()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen Student Board Dialog for classroom presentation.
 * Displays the latest instruction in large high-contrast typography so students across the room can read and hear.
 */
@Composable
private fun StudentBoardDialog(
    message: ClassroomChatMessage,
    currentScript: ScriptType,
    onDismiss: () -> Unit,
    onPlayAudio: (String, String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Classroom Display (छात्र दृश्य)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Middle Hero Section: Big tribal message
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Teacher prompt banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "\"${message.sourceText}\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Giant Ol Chiki / Devanagari text
                    val primaryScript = if (currentScript == ScriptType.OL_CHIKI)
                        message.translation.targetSantaliOlChiki
                    else
                        message.translation.targetSantaliDevanagari

                    Text(
                        text = primaryScript,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 38.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pronunciation
                    Text(
                        text = message.translation.targetSantaliPhonetic,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val secondaryScript = if (currentScript == ScriptType.OL_CHIKI)
                        message.translation.targetSantaliDevanagari
                    else
                        message.translation.targetSantaliOlChiki

                    if (secondaryScript.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = secondaryScript,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bottom: Big Play Audio Button
                Button(
                    onClick = {
                        onPlayAudio(
                            message.translation.targetSantaliPhonetic,
                            message.translation.targetSantaliDevanagari
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Play Tribal Audio (आवाज़ सुनें)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
