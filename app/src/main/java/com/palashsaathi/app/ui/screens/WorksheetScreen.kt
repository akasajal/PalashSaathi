package com.palashsaathi.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
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
        GeneratedWorksheet(
            id = "${selectedGrade.name}_${selectedCategory.name}",
            titleHindi = if (selectedCategory == FLNCategory.NUMERACY) "संख्या पहचान और गिनती (1 से 5)" else "प्रारंभिक शब्द और चित्र मिलान",
            titleHoWarangCiti = if (selectedCategory == FLNCategory.NUMERACY) "𑣚𑣆𑣊𑣁𑣜 𑣞𑣂𑣕𑣂 𑣁𑣕𑣆 𑣞𑣉𑣎𑣂𑣕𑣂" else "𑣞𑣃𑣕𑣃𑣞 𑣁𑣕𑣆 𑣏𑣂𑣕𑣂 𑣞𑣂𑣚𑣁𑣎",
            titleHoDevanagari = if (selectedCategory == FLNCategory.NUMERACY) "लेखा और गिनती (मित् ते मोड़ेया)" else "शब्द और चित्र मिलान (संगी साला)",
            grade = selectedGrade,
            category = selectedCategory,
            exercises = if (selectedCategory == FLNCategory.NUMERACY) {
                listOf(
                    WorksheetExercise("ex1", "गिनकर सही संख्या पर गोला लगाओ", "𑣚𑣆𑣊𑣁𑣜 𑣕𑣆 𑣞𑣂𑣚𑣁𑣎 𑣚𑣆", "लेखा केते जोड़ावपे", "सेब को गिनें", "मित्, बारिया, आपिया...", "🍎🍎🍎", listOf("1", "2", "3"), "3"),
                    WorksheetExercise("ex2", "कितने तारे हैं? संख्या लिखो", "𑣕𑣂𑣓𑣁𑣊 𑣂𑣚𑣂𑣊 𑣞𑣆𑣓𑣁𑣋? 𑣉𑣚𑣚𑣆", "तिनाः इपिल मेनाः? ओलपे", "आकाश के तारे", "इपिल लेखापे", "⭐ ⭐ ⭐ ⭐", listOf("2", "4", "5"), "4"),
                    WorksheetExercise("ex3", "एक हाथ में कितनी उंगलियाँ होती हैं?", "𑣞𑣂𑣕𑣂 𑣕𑣂 𑣜𑣆 𑣕𑣂𑣓𑣁𑣊 𑣋𑣁𑣓𑣋𑣁?", "मित् ती रे तिनाः गांगा?", "हाथ की उंगलियाँ", "मोड़ेया (पाँच)", "✋", listOf("4", "5", "6"), "5")
                )
            } else {
                listOf(
                    WorksheetExercise("ex4", "'पानी' को हो भाषा में क्या कहते हैं?", "𑣓𑣁𑣁𑣋𑣂 𑣏𑣂𑣓𑣁𑣋 𑣞𑣆𑣓𑣁𑣋?", "'दाः' को क्या कहते हैं?", "पीने का पानी", "दाः (Dah)", "💧", listOf("दारू", "दाः", "सिंगी"), "दाः"),
                    WorksheetExercise("ex5", "'गाय' के चित्र का सही शब्द चुनो", "𑣃𑣜𑣂 𑣁𑣕𑣆 𑣏𑣂𑣕𑣂 𑣞𑣂𑣚𑣁𑣎", "उरिः के चित्र से मिलाओ", "घरेलू पशु", "उरिः (Urih)", "🐄", listOf("चेँड़े", "उरिः", "कुल"), "उरिः")
                )
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "द्विभाषी अभ्यास पत्र (FLN Worksheet Generator)",
            style = MaterialTheme.typography.titleLarge,
            color = DarkCharcoal,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Generate printable Hindi <-> Ho worksheets with Warang Citi and Devanagari offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Grade Selector
        Text(text = "कक्षा (Grade Level):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                        selectedContainerColor = PalashOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Selector
        Text(text = "विषय (FLN Category):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(FLNCategory.NUMERACY, FLNCategory.LITERACY).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(if (cat == FLNCategory.NUMERACY) "संख्या (Numeracy)" else "भाषा (Literacy)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ForestGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Worksheet Preview Card
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
                    Text(
                        text = "वर्कशीट पूर्वावलोकन (Preview)",
                        style = MaterialTheme.typography.labelLarge,
                        color = PalashOrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(containerColor = ForestGreenBackground) {
                        Text("A4 Print Ready", color = ForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentWorksheet.titleHindi,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
                Text(
                    text = if (currentScript == ScriptType.WARANG_CITI) currentWorksheet.titleHoWarangCiti else currentWorksheet.titleHoDevanagari,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PalashOrangeDark,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderOutline)

                currentWorksheet.exercises.forEachIndexed { i, ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${i + 1}. ", fontWeight = FontWeight.Bold)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${ex.questionHindi} ${ex.illustrationEmoji}", fontWeight = FontWeight.Medium)
                            Text(
                                text = "हो: ${if (currentScript == ScriptType.WARANG_CITI) ex.questionHoWarangCiti else ex.questionHoDevanagari}",
                                fontSize = 12.sp,
                                color = Color.Gray
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
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = "Export PDF",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isGenerating) "PDF तैयार हो रहा है..." else "प्रिंट-रेडी PDF डाउनलोड करें (Export PDF)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (generatedPdfPath != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ForestGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "सहेजा गया: $generatedPdfPath",
                        fontSize = 12.sp,
                        color = ForestGreen
                    )
                }
            }
        }
    }
}
