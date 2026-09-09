package com.palashsaathi.app.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.data.model.*
import com.palashsaathi.app.engine.WorksheetPdfGenerator
import com.palashsaathi.app.ui.theme.*

@Composable
fun WorksheetScreen(
    currentScript: ScriptType,
    languageMode: LanguagePairMode = LanguagePairMode.HINDI_TO_SANTALI,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGrade by remember { mutableStateOf(FLNGrade.GRADE_1) }
    var selectedCategory by remember { mutableStateOf(FLNCategory.NUMERACY) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedPdfPath by remember { mutableStateOf<String?>(null) }

    // Sample generated worksheet
    val currentWorksheet = remember(selectedGrade, selectedCategory) {
        when (selectedCategory) {
            FLNCategory.NUMERACY -> GeneratedWorksheet(
                id = "${selectedGrade.name}_NUMERACY",
                titleHindi = "संख्या पहचान और गिनती (1 से 5)",
                titleEnglish = "Number Identification & Counting (1 to 5)",
                titleSantaliOlChiki = "ᱮᱞ ᱪᱤᱱᱦᱟᱹᱣ ᱟᱨ ᱞᱮᱠᱷᱟ (᱑ ᱠᱷᱚᱱ ᱕)",
                titleSantaliDevanagari = "एल चिनहाव आर लेखा (१ खोन ५)",
                grade = selectedGrade,
                category = FLNCategory.NUMERACY,
                exercises = listOf(
                    WorksheetExercise("ex1", "गिनकर सही संख्या पर गोला लगाओ", "ᱞᱮᱠᱷᱟ ᱠᱟᱛᱮ ᱴᱷᱤᱠ ᱮᱞ ᱨᱮ ᱜᱩᱞᱟᱹᱭ ᱢᱮ", "लेखा काते ठीक एल रे गुलाय मे", "गिनती का अभ्यास", "ᱢᱤᱫ, ᱵᱟᱨ, ᱯᱮ...", Icons.Default.Filter3, listOf("1", "2", "3"), "3", questionEnglish = "Count and circle the correct number", hintEnglish = "Counting practice"),
                    WorksheetExercise("ex2", "सही संख्या की पहचान करो और लिखो", "ᱴᱷᱤᱠ ᱮᱞ ᱪᱤᱱᱦᱟᱹᱣ ᱠᱟᱛᱮ ᱚᱞ ᱢᱮ", "ठीक एल चिनहाव काते ओल मे", "संख्या पहचान", "ᱤᱯᱤᱞ ᱞᱮᱠᱷᱟᱭ ᱢᱮ", Icons.Default.Filter4, listOf("2", "4", "5"), "4", questionEnglish = "Identify and write the correct number", hintEnglish = "Number identification"),
                    WorksheetExercise("ex3", "एक हाथ में कितनी उंगलियाँ होती हैं?", "ᱢᱤᱫ ᱛᱤ ᱨᱮ ᱛᱤᱱᱟᱹᱜ ᱠᱟᱹᱴᱩᱵ ᱢᱮᱱᱟᱜᱼᱟ?", "मिद ती रे तिनाः काटुब मेनाःआ?", "उंगलियों की गिनती", "ᱢᱚᱬᱮ (पाँच)", Icons.Default.Filter5, listOf("4", "5", "6"), "5", questionEnglish = "How many fingers are on one hand?", hintEnglish = "Count fingers")
                )
            )
            FLNCategory.LITERACY -> GeneratedWorksheet(
                id = "${selectedGrade.name}_LITERACY",
                titleHindi = "प्रारंभिक शब्द और चित्र मिलान",
                titleEnglish = "Basic Word & Picture Matching",
                titleSantaliOlChiki = "ᱮᱛᱚᱦᱚᱵ ᱟᱹᱲᱟᱹ ᱟᱨ ᱪᱤᱛᱟᱹᱨ ᱡᱚᱲᱟᱣ",
                titleSantaliDevanagari = "एतोहोब आड़ा आर चितार जोड़ाव",
                grade = selectedGrade,
                category = FLNCategory.LITERACY,
                exercises = listOf(
                    WorksheetExercise("ex4", "'पानी' को संथाली में क्या कहते हैं?", "'ᱫᱟᱜ' ᱫᱚ ᱦᱤᱱᱫᱤ ᱛᱮ ᱪᱮᱫ ᱠᱚ ᱢᱮᱛᱟᱜᱼᱟ?", "'दाः' को क्या कहते हैं?", "पीने का पानी", "ᱫᱟᱜ (Dah)", Icons.Default.WaterDrop, listOf("ᱫᱟᱨᱮ", "ᱫᱟᱜ", "ᱥᱤᱧ"), "ᱫᱟᱜ", questionEnglish = "What is 'Water' called in Santali?", hintEnglish = "Drinking water"),
                    WorksheetExercise("ex5", "'गाय' के लिए सही शब्द चुनो", "'ᱜᱟᱹᱭ' ᱨᱮᱭᱟᱜ ᱪᱤᱛᱟᱹᱨ ᱥᱟᱞᱟᱜ ᱡᱚᱲᱟᱣ ᱢᱮ", "गाई के चित्र से मिलाओ", "घरेलू पशु", "ᱜᱟᱹᱭ (Gai)", Icons.Default.Pets, listOf("ᱪᱮᱬᱮ", "ᱜᱟᱹᱭ", "ᱠᱩᱞ"), "ᱜᱟᱹᱭ", questionEnglish = "Choose the correct Santali word for 'Cow'", hintEnglish = "Domestic animal")
                )
            )
            FLNCategory.CORPUS_READING -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CORPUS_READING",
                titleHindi = "20K कॉर्पस वाक्य पठन (Corpus Reading Comprehension)",
                titleEnglish = "20K Corpus Reading Comprehension",
                titleSantaliOlChiki = "᱒᱐K ᱠᱚᱨᱯᱟᱥ ᱟᱹᱭᱟᱹᱛ ᱯᱟᱲᱦᱟᱣ",
                titleSantaliDevanagari = "२०K कॉर्पस आयात पाढ़ाव",
                grade = selectedGrade,
                category = FLNCategory.CORPUS_READING,
                exercises = listOf(
                    WorksheetExercise("cr1", "पुस्तक (Book) का सही संथाली वाक्य चुनें: 'Many books specialise...'", "ᱟᱭᱢᱟ ᱯᱚᱛᱚᱵ ᱠᱚᱜᱮ ᱵᱤᱥᱟᱹᱥ ᱥᱚᱯᱷᱴᱳᱣᱮᱨ ᱨᱮᱭᱟᱜ...", "आयमा पोतोब कोगे बिसास सॉफ्टवेर...", "किताब / पोतोब", "ᱯᱚᱛᱚᱵ", Icons.Default.MenuBook, listOf("ᱯᱚᱛᱚᱵ (Book)", "ᱫᱟᱨᱮ (Tree)", "ᱫᱟᱜ (Water)"), "ᱯᱚᱛᱚᱵ (Book)", questionEnglish = "Find the Santali sentence for: 'Many books specialise...'", hintEnglish = "Book / Potob"),
                    WorksheetExercise("cr2", "शरीर के अंग: 'An arm is an upper limb of the body.'", "ᱮ ᱟᱨ ᱮᱢ ᱫᱚ ᱢᱤᱫᱴᱟᱝ ᱛᱤ ᱨᱮᱭᱟᱜ ᱢᱤᱫᱯᱟᱦᱚᱴᱟ ᱫᱚ ᱦᱩᱭᱩᱜ ᱠᱟᱱᱟ ᱦᱚᱲᱢᱚ...", "ए आर एम दो मिदटांग ती रेयाग मिदपाहटा...", "हाथ / शरीर", "ᱛᱤ ᱟᱨ ᱦᱚᱲᱢᱚ", Icons.Default.Person, listOf("ᱛᱤ (Hand)", "ᱜᱟᱰᱟ (River)", "ᱥᱤᱧ (Sun)"), "ᱛᱤ (Hand)", questionEnglish = "Body parts: 'An arm is an upper limb of the body.'", hintEnglish = "Hand / Body"),
                    WorksheetExercise("cr3", "नदी और स्वर्ण: 'Alluvial gold was discovered in and along the river in 1851.'", "᱑᱘᱕᱑ ᱥᱟᱞ ᱨᱮ ᱜᱟᱰᱟᱨᱮ ᱟᱨ ᱚᱱᱟ ᱥᱩᱨ ᱨᱮ ᱯᱚᱞᱤ ᱦᱟᱥᱟ ᱥᱟᱢᱟᱱᱚᱢ...", "१८५१ साल रे गाडारे आर सामानोम...", "नदी / सोना", "ᱜᱟᱰᱟ ᱟᱨ ᱥᱟᱢᱟᱱᱚᱢ", Icons.Default.Water, listOf("ᱜᱟᱰᱟ (River)", "ᱯᱚᱛᱚᱵ (Book)", "ᱪᱮᱬᱮ (Bird)"), "ᱜᱟᱰᱟ (River)", questionEnglish = "River & Gold: 'Alluvial gold was discovered in and along the river in 1851.'", hintEnglish = "River / Gold")
                )
            )
            FLNCategory.CORPUS_VOCAB -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CORPUS_VOCAB",
                titleHindi = "20K कॉर्पस प्रमुख शब्दावली मिलान (Corpus Vocabulary Practice)",
                titleEnglish = "20K Corpus Vocabulary Practice",
                titleSantaliOlChiki = "᱒᱐K ᱠᱚᱨᱯᱟᱥ ᱢᱩᱲᱩᱫ ᱟᱹᱲᱟᱹ ᱡᱚᱲᱟᱣ",
                titleSantaliDevanagari = "२०K कॉर्पस मुड़ुद आड़ा जोड़ाव",
                grade = selectedGrade,
                category = FLNCategory.CORPUS_VOCAB,
                exercises = listOf(
                    WorksheetExercise("cv1", "'लिखना' (To Write / Script) के लिए सही संथाली शब्द:", "'ᱚᱞ' (Ol) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "ओल (लिखना)", "लेखन", "ᱚᱞ (Ol)", Icons.Default.Edit, listOf("ᱚᱞ", "ᱥᱟᱠᱟᱢ", "ᱥᱮᱬᱟᱭᱟ"), "ᱚᱞ", questionEnglish = "What is the Santali word for 'To Write / Script'?", hintEnglish = "Writing"),
                    WorksheetExercise("cv2", "'सीखना / शिक्षा' (To Learn) के लिए सही संथाली शब्द:", "'ᱥᱮᱬᱟᱭᱟ' (Senaya) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "सेणाया (सीखना)", "शिक्षा", "ᱥᱮᱬᱟᱭᱟ (Senaya)", Icons.Default.School, listOf("ᱥᱮᱬᱟᱭᱟ", "ᱜᱟᱰᱟ", "ᱦᱚᱲᱢᱚ"), "ᱥᱮᱬᱟᱭᱟ", questionEnglish = "What is the Santali word for 'To Learn / Education'?", hintEnglish = "Education"),
                    WorksheetExercise("cv3", "'पन्ना / पृष्ठ' (Page) के लिए सही संथाली शब्द:", "'ᱥᱟᱠᱟᱢ' (Sakam) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "साकाम (पन्ना)", "पृष्ठ", "ᱥᱟᱠᱟᱢ (Sakam)", Icons.Default.Description, listOf("ᱥᱟᱠᱟᱢ", "ᱯᱚᱛᱚᱵ", "ᱥᱟᱢᱟᱱᱚᱢ"), "ᱥᱟᱠᱟᱢ", questionEnglish = "What is the Santali word for 'Page / Leaf'?", hintEnglish = "Page")
                )
            )
            FLNCategory.CLASSROOM_COMMANDS -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CLASSROOM_COMMANDS",
                titleHindi = "कक्षा निर्देश और अनुशासन (Classroom Prompts)",
                titleEnglish = "Classroom Commands & Prompts",
                titleSantaliOlChiki = "ᱠᱞᱟᱥ ᱨᱮᱭᱟᱜ ᱦᱩᱠᱩᱢ ᱟᱨ ᱪᱮᱛᱟᱣᱱᱤ",
                titleSantaliDevanagari = "क्लास रेयाग हुकुम आर चेतावनि",
                grade = selectedGrade,
                category = FLNCategory.CLASSROOM_COMMANDS,
                exercises = listOf(
                    WorksheetExercise(
                        id = "cc1",
                        questionHindi = "'बैठ जाओ' का सही संथाली अनुवाद क्या है?",
                        questionEnglish = "What is the Santali translation for 'Sit down'?",
                        questionSantaliOlChiki = "'ᱫᱩᱲᱩᱵᱽ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'दुड़ुब मे' रेयाग मेनेत्:",
                        hintHindi = "बैठना",
                        hintEnglish = "To sit",
                        hintSantali = "ᱫᱩᱲᱩᱵᱽ ᱢᱮ",
                        icon = Icons.Default.RecordVoiceOver,
                        options = listOf("ᱫᱩᱲᱩᱵᱽ ᱢᱮ", "ᱛᱤᱸᱜᱩᱱ ᱢᱮ", "ᱦᱤᱡᱩᱜ ᱢᱮ"),
                        correctAnswer = "ᱫᱩᱲᱩᱵᱽ ᱢᱮ"
                    ),
                    WorksheetExercise(
                        id = "cc2",
                        questionHindi = "'किताब खोलो' का सही संथाली अनुवाद क्या है?",
                        questionEnglish = "What is the Santali translation for 'Open your book'?",
                        questionSantaliOlChiki = "'ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'पोतोब झिज मे' रेयाग मेनेत्:",
                        hintHindi = "किताब खोलना",
                        hintEnglish = "Open book",
                        hintSantali = "ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ",
                        icon = Icons.Default.MenuBook,
                        options = listOf("ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ", "ᱠᱷᱟᱛᱟ ᱨᱮ ᱚᱞᱢᱮ", "ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ"),
                        correctAnswer = "ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ"
                    ),
                    WorksheetExercise(
                        id = "cc3",
                        questionHindi = "'शांत रहो' का सही संथाली अनुवाद क्या है?",
                        questionEnglish = "What is the Santali translation for 'Be quiet / Keep silent'?",
                        questionSantaliOlChiki = "'ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'थिर ताहेन मे' रेयाग मेनेत्:",
                        hintHindi = "शांति",
                        hintEnglish = "Quietness",
                        hintSantali = "ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ",
                        icon = Icons.Default.VolumeOff,
                        options = listOf("ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ", "ᱫᱩᱲᱩᱵᱽ ᱢᱮ", "ᱯᱟᱲᱦᱟᱣ ᱢᱮ"),
                        correctAnswer = "ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ"
                    )
                )
            )
            else -> GeneratedWorksheet(
                id = "${selectedGrade.name}_DEFAULT",
                titleHindi = "द्विभाषी संथाली अभ्यास",
                titleEnglish = "Bilingual Santali Practice",
                titleSantaliOlChiki = "ᱵᱟᱨ ᱯᱟᱹᱨᱥᱤ ᱥᱟᱱᱛᱟᱲᱤ ᱮᱠᱥᱟᱨᱥᱟᱭᱤᱡᱽ",
                titleSantaliDevanagari = "बार पारसी संथाली एक्सरसाइज",
                grade = selectedGrade,
                category = selectedCategory,
                exercises = listOf(
                    WorksheetExercise(
                        id = "def1",
                        questionHindi = "सही संथाली शब्द चुनें",
                        questionEnglish = "Choose the correct Santali word",
                        questionSantaliOlChiki = "ᱴᱷᱤᱠ ᱥᱟᱱᱛᱟᱲᱤ ᱟᱹᱲᱟᱹ ᱵᱟᱪᱷᱟᱣ ᱢᱮ",
                        questionSantaliDevanagari = "ठीक संथाली आड़ा बाछाव मे",
                        hintHindi = "शब्द पहचान",
                        hintEnglish = "Word matching",
                        hintSantali = "ᱟᱹᱲᱟᱹ",
                        icon = Icons.Default.MenuBook,
                        options = listOf("ᱯᱚᱛᱚᱵ", "ᱫᱟᱨᱮ", "ᱫᱟᱜ"),
                        correctAnswer = "ᱯᱚᱛᱚᱵ"
                    )
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Child & Teacher-friendly header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                        "Printable Worksheets"
                    else
                        "अभ्यास पत्र (Worksheets)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI)
                        "Create fun printable sheets for your students"
                    else
                        "छात्रों के लिए प्रिंट योग्य संथाली अभ्यास पत्र",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Friendly Grade Selector with Visual Big Number Cards
        Text(
            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Select Grade:" else "कक्षा चुनें (Grade):",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FLNGrade.values().forEach { grade ->
                val isSelected = selectedGrade == grade
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedGrade = grade }
                        .border(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (grade) {
                                FLNGrade.GRADE_1 -> "1"
                                FLNGrade.GRADE_2 -> "2"
                                FLNGrade.GRADE_3 -> "3"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) {
                                when (grade) {
                                    FLNGrade.GRADE_1 -> "Grade 1"
                                    FLNGrade.GRADE_2 -> "Grade 2"
                                    FLNGrade.GRADE_3 -> "Grade 3"
                                }
                            } else {
                                when (grade) {
                                    FLNGrade.GRADE_1 -> "कक्षा 1"
                                    FLNGrade.GRADE_2 -> "कक्षा 2"
                                    FLNGrade.GRADE_3 -> "कक्षा 3"
                                }
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Category Selector with Icons
        Text(
            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Topic:" else "विषय (Topic):",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(FLNCategory.NUMERACY, if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Numbers" else "संख्या (Numbers)", Icons.Default.Calculate),
                Triple(FLNCategory.LITERACY, if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Words" else "भाषा (Words)", Icons.Default.MenuBook),
                Triple(FLNCategory.CLASSROOM_COMMANDS, if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Commands" else "निर्देश (Commands)", Icons.Default.RecordVoiceOver),
                Triple(FLNCategory.CORPUS_READING, if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Reading" else "वाक्य (Reading)", Icons.Default.AutoStories),
                Triple(FLNCategory.CORPUS_VOCAB, if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "20K Vocab" else "20K शब्द (Vocab)", Icons.Default.Translate)
            ).forEach { (cat, label, icon) ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(label) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Classroom Notebook-style Worksheet Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header of preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "A4 Print Ready" else "प्रिंट-रेडी A4",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Palash Saathi • FLN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Student details line mockup
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Name: _______________" else "नाम: _______________",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Date: _________" else "दिनांक: _________",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentWorksheet.getTitle(languageMode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentScript == ScriptType.OL_CHIKI) currentWorksheet.titleSantaliOlChiki else currentWorksheet.titleSantaliDevanagari,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )

                // Exercises formatted as clear visual cards with multiple choice bubbles
                currentWorksheet.exercises.forEachIndexed { i, ex ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${i + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = ex.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ex.getQuestion(languageMode),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Santali subtitle
                        Text(
                            text = if (currentScript == ScriptType.OL_CHIKI) ex.questionSantaliOlChiki else ex.questionSantaliDevanagari,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                        )

                        // Option bubbles for interactive child worksheet feel
                        if (ex.options.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 32.dp, top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ex.options.forEach { opt ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = opt,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Generate and Export Button
        Button(
            onClick = {
                isGenerating = true
                try {
                    val pdfFile = WorksheetPdfGenerator.generatePdf(context, currentWorksheet, languageMode)
                    generatedPdfPath = pdfFile.absolutePath
                    Toast.makeText(
                        context,
                        if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "PDF Generated: ${pdfFile.name}" else "PDF तैयार हो गया: ${pdfFile.name}",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Error: ${e.message}" else "त्रुटि: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    isGenerating = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "Export PDF",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isGenerating) {
                    if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Generating PDF..." else "PDF तैयार हो रहा है..."
                } else {
                    if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Export Printable PDF" else "प्रिंट-रेडी PDF डाउनलोड करें"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (generatedPdfPath != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "Saved: " else "सहेजा गया: "}$generatedPdfPath",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
