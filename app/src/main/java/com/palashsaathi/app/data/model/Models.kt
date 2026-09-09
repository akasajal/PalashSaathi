package com.palashsaathi.app.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class ScriptType(val displayName: String) {
    OL_CHIKI("Ol Chiki (ᱚᱞ ᱪᱤᱠᱤ)"),
    DEVANAGARI("Devanagari (देवनागरी)")
}

data class TranslationResult(
    val sourceHindi: String,
    val targetSantaliOlChiki: String,
    val targetSantaliDevanagari: String,
    val targetSantaliPhonetic: String,
    val subtitleHo: String,
    val subtitleMundari: String,
    val latencyMs: Long,
    val audioDurationMs: Long = 1800L,
    val fromCorpus: Boolean = false
)

enum class FLNGrade(val label: String) {
    GRADE_1("Grade 1 (कक्षा 1)"),
    GRADE_2("Grade 2 (कक्षा 2)"),
    GRADE_3("Grade 3 (कक्षा 3)")
}

enum class FLNCategory(val label: String) {
    NUMERACY("Numeracy (संख्या व गणित)"),
    LITERACY("Literacy (भाषा व शब्द)"),
    CLASSROOM_COMMANDS("Classroom Prompts (कक्षा निर्देश)"),
    CORPUS_VOCAB("20K Vocab (कोष शब्दावली)"),
    CORPUS_READING("20K Sentences (वाक्य व गद्यांश)")
}

data class FlashcardItem(
    val id: String,
    val hindiWord: String,
    val santaliOlChiki: String,
    val santaliDevanagari: String,
    val santaliPhonetic: String,
    val hoSubtitle: String,
    val mundariSubtitle: String,
    val category: FLNCategory,
    val icon: ImageVector
)

data class WorksheetExercise(
    val id: String,
    val questionHindi: String,
    val questionSantaliOlChiki: String,
    val questionSantaliDevanagari: String,
    val hintHindi: String,
    val hintSantali: String,
    val icon: ImageVector,
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)

data class GeneratedWorksheet(
    val id: String,
    val titleHindi: String,
    val titleSantaliOlChiki: String,
    val titleSantaliDevanagari: String,
    val grade: FLNGrade,
    val category: FLNCategory,
    val exercises: List<WorksheetExercise>,
    val generatedTimestamp: Long = System.currentTimeMillis()
)

data class CorpusSentence(
    val id: Int,
    val english: String,
    val santaliOlChiki: String,
    val santaliDevanagari: String = "",
    val santaliPhonetic: String = ""
)
