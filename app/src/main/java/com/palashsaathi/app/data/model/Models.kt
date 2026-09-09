package com.palashsaathi.app.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class ScriptType(val displayName: String) {
    WARANG_CITI("Warang Citi (𑣓𑣉𑣎𑣉𑣜)"),
    DEVANAGARI("Devanagari (देवनागरी)")
}

data class TranslationResult(
    val sourceHindi: String,
    val targetHoWarangCiti: String,
    val targetHoDevanagari: String,
    val targetHoPhonetic: String,
    val subtitleSanthali: String,
    val subtitleMundari: String,
    val latencyMs: Long,
    val audioDurationMs: Long = 1800L
)

enum class FLNGrade(val label: String) {
    GRADE_1("Grade 1 (कक्षा 1)"),
    GRADE_2("Grade 2 (कक्षा 2)"),
    GRADE_3("Grade 3 (कक्षा 3)")
}

enum class FLNCategory(val label: String) {
    NUMERACY("Numeracy (संख्या व गणित)"),
    LITERACY("Literacy (भाषा व शब्द)"),
    CLASSROOM_COMMANDS("Classroom Prompts (कक्षा निर्देश)")
}

data class FlashcardItem(
    val id: String,
    val hindiWord: String,
    val hoWarangCiti: String,
    val hoDevanagari: String,
    val hoPhonetic: String,
    val santhaliSubtitle: String,
    val mundariSubtitle: String,
    val category: FLNCategory,
    val icon: ImageVector
)

data class WorksheetExercise(
    val id: String,
    val questionHindi: String,
    val questionHoWarangCiti: String,
    val questionHoDevanagari: String,
    val hintHindi: String,
    val hintHo: String,
    val icon: ImageVector,
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)

data class GeneratedWorksheet(
    val id: String,
    val titleHindi: String,
    val titleHoWarangCiti: String,
    val titleHoDevanagari: String,
    val grade: FLNGrade,
    val category: FLNCategory,
    val exercises: List<WorksheetExercise>,
    val generatedTimestamp: Long = System.currentTimeMillis()
)
