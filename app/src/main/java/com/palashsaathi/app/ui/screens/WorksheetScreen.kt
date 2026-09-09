package com.palashsaathi.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                titleSantaliOlChiki = "ᱮᱞ ᱪᱤᱱᱦᱟᱹᱣ ᱟᱨ ᱞᱮᱠᱷᱟ (᱑ ᱠᱷᱚᱱ ᱕)",
                titleSantaliDevanagari = "एल चिनहाव आर लेखा (१ खोन ५)",
                grade = selectedGrade,
                category = FLNCategory.NUMERACY,
                exercises = listOf(
                    WorksheetExercise("ex1", "गिनकर सही संख्या पर गोला लगाओ", "ᱞᱮᱠᱷᱟ ᱠᱟᱛᱮ ᱴᱷᱤᱠ ᱮᱞ ᱨᱮ ᱜᱩᱞᱟᱹᱭ ᱢᱮ", "लेखा काते ठीक एल रे गुलाय मे", "गिनती का अभ्यास", "ᱢᱤᱫ, ᱵᱟᱨ, ᱯᱮ...", Icons.Default.Filter3, listOf("1", "2", "3"), "3"),
                    WorksheetExercise("ex2", "सही संख्या की पहचान करो और लिखो", "ᱴᱷᱤᱠ ᱮᱞ ᱪᱤᱱᱦᱟᱹᱣ ᱠᱟᱛᱮ ᱚᱞ ᱢᱮ", "ठीक एल चिनहाव काते ओल मे", "संख्या पहचान", "ᱤᱯᱤᱞ ᱞᱮᱠᱷᱟᱭ ᱢᱮ", Icons.Default.Filter4, listOf("2", "4", "5"), "4"),
                    WorksheetExercise("ex3", "एक हाथ में कितनी उंगलियाँ होती हैं?", "ᱢᱤᱫ ᱛᱤ ᱨᱮ ᱛᱤᱱᱟᱹᱜ ᱠᱟᱹᱴᱩᱵ ᱢᱮᱱᱟᱜᱼᱟ?", "मिद ती रे तिनाः काटुब मेनाःआ?", "उंगलियों की गिनती", "ᱢᱚᱬᱮ (पाँच)", Icons.Default.Filter5, listOf("4", "5", "6"), "5")
                )
            )
            FLNCategory.LITERACY -> GeneratedWorksheet(
                id = "${selectedGrade.name}_LITERACY",
                titleHindi = "प्रारंभिक शब्द और चित्र मिलान",
                titleSantaliOlChiki = "ᱮᱛᱚᱦᱚᱵ ᱟᱹᱲᱟᱹ ᱟᱨ ᱪᱤᱛᱟᱹᱨ ᱡᱚᱲᱟᱣ",
                titleSantaliDevanagari = "एतोहोब आड़ा आर चितार जोड़ाव",
                grade = selectedGrade,
                category = FLNCategory.LITERACY,
                exercises = listOf(
                    WorksheetExercise("ex4", "'पानी' को संथाली में क्या कहते हैं?", "'ᱫᱟᱜ' ᱫᱚ ᱦᱤᱱᱫᱤ ᱛᱮ ᱪᱮᱫ ᱠᱚ ᱢᱮᱛᱟᱜᱼᱟ?", "'दाः' को क्या कहते हैं?", "पीने का पानी", "ᱫᱟᱜ (Dah)", Icons.Default.WaterDrop, listOf("ᱫᱟᱨᱮ", "ᱫᱟᱜ", "ᱥᱤᱧ"), "ᱫᱟᱜ"),
                    WorksheetExercise("ex5", "'गाय' के लिए सही शब्द चुनो", "'ᱜᱟᱹᱭ' ᱨᱮᱭᱟᱜ ᱪᱤᱛᱟᱹᱨ ᱥᱟᱞᱟᱜ ᱡᱚᱲᱟᱣ ᱢᱮ", "गाई के चित्र से मिलाओ", "घरेलू पशु", "ᱜᱟᱹᱭ (Gai)", Icons.Default.Pets, listOf("ᱪᱮᱬᱮ", "ᱜᱟᱹᱭ", "ᱠᱩᱞ"), "ᱜᱟᱹᱭ")
                )
            )
            FLNCategory.CORPUS_READING -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CORPUS_READING",
                titleHindi = "20K कॉर्पस वाक्य पठन (Corpus Reading Comprehension)",
                titleSantaliOlChiki = "᱒᱐K ᱠᱚᱨᱯᱟᱥ ᱟᱹᱭᱟᱹᱛ ᱯᱟᱲᱦᱟᱣ",
                titleSantaliDevanagari = "२०K कॉर्पस आयात पाढ़ाव",
                grade = selectedGrade,
                category = FLNCategory.CORPUS_READING,
                exercises = listOf(
                    WorksheetExercise("cr1", "पुस्तक (Book) का सही संथाली वाक्य चुनें: 'Many books specialise...'", "ᱟᱭᱢᱟ ᱯᱚᱛᱚᱵ ᱠᱚᱜᱮ ᱵᱤᱥᱟᱹᱥ ᱥᱚᱯᱷᱴᱳᱣᱮᱨ ᱨᱮᱭᱟᱜ...", "आयमा पोतोब कोगे बिसास सॉफ्टवेर...", "किताब / पोतोब", "ᱯᱚᱛᱚᱵ", Icons.Default.MenuBook, listOf("ᱯᱚᱛᱚᱵ (Book)", "ᱫᱟᱨᱮ (Tree)", "ᱫᱟᱜ (Water)"), "ᱯᱚᱛᱚᱵ (Book)"),
                    WorksheetExercise("cr2", "शरीर के अंग: 'An arm is an upper limb of the body.'", "ᱮ ᱟᱨ ᱮᱢ ᱫᱚ ᱢᱤᱫᱴᱟᱝ ᱛᱤ ᱨᱮᱭᱟᱜ ᱢᱤᱫᱯᱟᱦᱚᱴᱟ ᱫᱚ ᱦᱩᱭᱩᱜ ᱠᱟᱱᱟ ᱦᱚᱲᱢᱚ...", "ए आर एम दो मिदटांग ती रेयाग मिदपाहटा...", "हाथ / शरीर", "ᱛᱤ ᱟᱨ ᱦᱚᱲᱢᱚ", Icons.Default.Person, listOf("ᱛᱤ (Hand)", "ᱜᱟᱰᱟ (River)", "ᱥᱤᱧ (Sun)"), "ᱛᱤ (Hand)"),
                    WorksheetExercise("cr3", "नदी और स्वर्ण: 'Alluvial gold was discovered in and along the river in 1851.'", "᱑᱘᱕᱑ ᱥᱟᱞ ᱨᱮ ᱜᱟᱰᱟᱨᱮ ᱟᱨ ᱚᱱᱟ ᱥᱩᱨ ᱨᱮ ᱯᱚᱞᱤ ᱦᱟᱥᱟ ᱥᱟᱢᱟᱱᱚᱢ...", "१८५१ साल रे गाडारे आर सामानोम...", "नदी / सोना", "ᱜᱟᱰᱟ ᱟᱨ ᱥᱟᱢᱟᱱᱚᱢ", Icons.Default.Water, listOf("ᱜᱟᱰᱟ (River)", "ᱯᱚᱛᱚᱵ (Book)", "ᱪᱮᱬᱮ (Bird)"), "ᱜᱟᱰᱟ (River)")
                )
            )
            FLNCategory.CORPUS_VOCAB -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CORPUS_VOCAB",
                titleHindi = "20K कॉर्पस प्रमुख शब्दावली मिलान (Corpus Vocabulary Practice)",
                titleSantaliOlChiki = "᱒᱐K ᱠᱚᱨᱯᱟᱥ ᱢᱩᱲᱩᱫ ᱟᱹᱲᱟᱹ ᱡᱚᱲᱟᱣ",
                titleSantaliDevanagari = "२०K कॉर्पस मुड़ुद आड़ा जोड़ाव",
                grade = selectedGrade,
                category = FLNCategory.CORPUS_VOCAB,
                exercises = listOf(
                    WorksheetExercise("cv1", "'लिखना' (To Write / Script) के लिए सही संथाली शब्द:", "'ᱚᱞ' (Ol) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "ओल (लिखना)", "लेखन", "ᱚᱞ (Ol)", Icons.Default.Edit, listOf("ᱚᱞ", "ᱥᱟᱠᱟᱢ", "ᱥᱮᱬᱟᱭᱟ"), "ᱚᱞ"),
                    WorksheetExercise("cv2", "'सीखना / शिक्षा' (To Learn) के लिए सही संथाली शब्द:", "'ᱥᱮᱬᱟᱭᱟ' (Senaya) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "सेणाया (सीखना)", "शिक्षा", "ᱥᱮᱬᱟᱭᱟ (Senaya)", Icons.Default.School, listOf("ᱥᱮᱬᱟᱭᱟ", "ᱜᱟᱰᱟ", "ᱦᱚᱲᱢᱚ"), "ᱥᱮᱬᱟᱭᱟ"),
                    WorksheetExercise("cv3", "'पन्ना / पृष्ठ' (Page) के लिए सही संथाली शब्द:", "'ᱥᱟᱠᱟᱢ' (Sakam) ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ ᱪᱮᱫ ᱠᱟᱱᱟ?", "साकाम (पन्ना)", "पृष्ठ", "ᱥᱟᱠᱟᱢ (Sakam)", Icons.Default.Description, listOf("ᱥᱟᱠᱟᱢ", "ᱯᱚᱛᱚᱵ", "ᱥᱟᱢᱟᱱᱚᱢ"), "ᱥᱟᱠᱟᱢ")
                )
            )
            FLNCategory.CLASSROOM_COMMANDS -> GeneratedWorksheet(
                id = "${selectedGrade.name}_CLASSROOM_COMMANDS",
                titleHindi = "कक्षा निर्देश और अनुशासन (Classroom Prompts)",
                titleSantaliOlChiki = "ᱠᱞᱟᱥ ᱨᱮᱭᱟᱜ ᱦᱩᱠᱩᱢ ᱟᱨ ᱪᱮᱛᱟᱣᱱᱤ",
                titleSantaliDevanagari = "क्लास रेयाग हुकुम आर चेतावनि",
                grade = selectedGrade,
                category = FLNCategory.CLASSROOM_COMMANDS,
                exercises = listOf(
                    WorksheetExercise(
                        id = "cc1",
                        questionHindi = "'बैठ जाओ' का सही संथाली अनुवाद क्या है?",
                        questionSantaliOlChiki = "'ᱫᱩᱲᱩᱵᱽ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'दुड़ुब मे' रेयाग मेनेत्:",
                        hintHindi = "बैठना",
                        hintSantali = "ᱫᱩᱲᱩᱵᱽ ᱢᱮ",
                        icon = Icons.Default.RecordVoiceOver,
                        options = listOf("ᱫᱩᱲᱩᱵᱽ ᱢᱮ", "ᱛᱤᱸᱜᱩᱱ ᱢᱮ", "ᱦᱤᱡᱩᱜ ᱢᱮ"),
                        correctAnswer = "ᱫᱩᱲᱩᱵᱽ ᱢᱮ"
                    ),
                    WorksheetExercise(
                        id = "cc2",
                        questionHindi = "'किताब खोलो' का सही संथाली अनुवाद क्या है?",
                        questionSantaliOlChiki = "'ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'पोतोब झिज मे' रेयाग मेनेत्:",
                        hintHindi = "किताब खोलना",
                        hintSantali = "ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ",
                        icon = Icons.Default.MenuBook,
                        options = listOf("ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ", "ᱠᱷᱟᱛᱟ ᱨᱮ ᱚᱞᱢᱮ", "ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ"),
                        correctAnswer = "ᱯᱚᱛᱚᱵ ᱡᱷᱤᱡᱽ ᱢᱮ"
                    ),
                    WorksheetExercise(
                        id = "cc3",
                        questionHindi = "'शांत रहो' का सही संथाली अनुवाद क्या है?",
                        questionSantaliOlChiki = "'ᱛᱷᱤᱨ ᱛᱟᱦᱮᱸᱱ ᱢᱮ' ᱨᱮᱭᱟᱜ ᱢᱮᱱᱮᱛ:",
                        questionSantaliDevanagari = "'थिर ताहेन मे' रेयाग मेनेत्:",
                        hintHindi = "शांति",
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
                titleSantaliOlChiki = "ᱵᱟᱨ ᱯᱟᱹᱨᱥᱤ ᱥᱟᱱᱛᱟᱲᱤ ᱮᱠᱥᱟᱨᱥᱟᱭᱤᱡᱽ",
                titleSantaliDevanagari = "बार पारसी संथाली एक्सरसाइज",
                grade = selectedGrade,
                category = selectedCategory,
                exercises = listOf(
                    WorksheetExercise(
                        id = "def1",
                        questionHindi = "सही संथाली शब्द चुनें",
                        questionSantaliOlChiki = "ᱴᱷᱤᱠ ᱥᱟᱱᱛᱟᱲᱤ ᱟᱹᱲᱟᱹ ᱵᱟᱪᱷᱟᱣ ᱢᱮ",
                        questionSantaliDevanagari = "ठीक संथाली आड़ा बाछाव मे",
                        hintHindi = "शब्द पहचान",
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
        Text(
            text = "द्विभाषी अभ्यास पत्र (FLN Worksheet Generator)",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Generate printable Hindi <-> Santali worksheets with Ol Chiki and Devanagari offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Grade Selector
        Text(
            text = "कक्षा (Grade Level):",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FLNGrade.values().forEach { grade ->
                FilterChip(
                    selected = selectedGrade == grade,
                    onClick = { selectedGrade = grade },
                    label = { Text(grade.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Selector
        Text(
            text = "विषय (FLN Category):",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                FLNCategory.NUMERACY to "संख्या (Numeracy)",
                FLNCategory.LITERACY to "भाषा (Literacy)",
                FLNCategory.CLASSROOM_COMMANDS to "निर्देश (Commands)",
                FLNCategory.CORPUS_READING to "20K वाक्य (Corpus Reading)",
                FLNCategory.CORPUS_VOCAB to "20K शब्द (Corpus Vocab)"
            ).forEach { (cat, label) ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Worksheet Preview Card
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
                    Text(
                        text = "वर्कशीट पूर्वावलोकन (Preview)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text("A4 Print Ready", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentWorksheet.titleHindi,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentScript == ScriptType.OL_CHIKI) currentWorksheet.titleSantaliOlChiki else currentWorksheet.titleSantaliDevanagari,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                currentWorksheet.exercises.forEachIndexed { i, ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${i + 1}. ",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = ex.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(end = 6.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ex.questionHindi,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "संथाली: ${if (currentScript == ScriptType.OL_CHIKI) ex.questionSantaliOlChiki else ex.questionSantaliDevanagari}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generate and Export Button
        Button(
            onClick = {
                isGenerating = true
                try {
                    val pdfFile = WorksheetPdfGenerator.generatePdf(context, currentWorksheet)
                    generatedPdfPath = pdfFile.absolutePath
                    Toast.makeText(context, "PDF तैयार हो गया: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isGenerating = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "Export PDF",
                tint = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isGenerating) "PDF तैयार हो रहा है..." else "प्रिंट-रेडी PDF डाउनलोड करें (Export PDF)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        if (generatedPdfPath != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "सहेजा गया: $generatedPdfPath",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
