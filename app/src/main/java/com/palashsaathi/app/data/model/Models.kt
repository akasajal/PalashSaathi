package com.palashsaathi.app.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class ScriptType(val displayName: String) {
    OL_CHIKI("Ol Chiki (ᱚᱞ ᱪᱤᱠᱤ)"),
    DEVANAGARI("Devanagari (देवनागरी)")
}

enum class LanguagePairMode(
    val titleHindi: String,
    val titleEnglish: String,
    val shortLabel: String,
    val sourceLabel: String
) {
    HINDI_TO_SANTALI("हिन्दी से संथाली", "Hindi to Santali", "HI → SAT", "Hindi (हिन्दी)"),
    ENGLISH_TO_SANTALI("अंग्रेज़ी से संथाली", "English to Santali", "EN → SAT", "English (अंग्रेज़ी)")
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
    val fromCorpus: Boolean = false,
    val sourceEnglish: String = ""
) {
    fun getSource(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI && sourceEnglish.isNotBlank()) {
            sourceEnglish
        } else {
            sourceHindi
        }
    }
}

enum class FLNGrade(val labelHindi: String, val labelEnglish: String) {
    GRADE_1("कक्षा 1 (Grade 1)", "Grade 1"),
    GRADE_2("कक्षा 2 (Grade 2)", "Grade 2"),
    GRADE_3("कक्षा 3 (Grade 3)", "Grade 3");

    val label: String get() = labelHindi

    fun getLabel(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI) labelEnglish else labelHindi
    }
}

enum class FLNCategory(val labelHindi: String, val labelEnglish: String) {
    NUMERACY("संख्या व गणित (Numeracy)", "Numeracy"),
    LITERACY("भाषा व शब्द (Literacy)", "Literacy"),
    CLASSROOM_COMMANDS("कक्षा निर्देश (Prompts)", "Classroom Prompts"),
    CORPUS_VOCAB("20K कोष शब्दावली", "20K Corpus Vocab"),
    CORPUS_READING("20K वाक्य व गद्यांश", "20K Corpus Sentences");

    val label: String get() = labelHindi

    fun getLabel(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI) labelEnglish else labelHindi
    }
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
    val icon: ImageVector,
    val englishWord: String = ""
) {
    fun getSourceWord(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI && englishWord.isNotBlank()) {
            englishWord
        } else {
            hindiWord
        }
    }
}

data class WorksheetExercise(
    val id: String,
    val questionHindi: String,
    val questionSantaliOlChiki: String,
    val questionSantaliDevanagari: String,
    val hintHindi: String,
    val hintSantali: String,
    val icon: ImageVector,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val questionEnglish: String = "",
    val hintEnglish: String = ""
) {
    fun getQuestion(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI && questionEnglish.isNotBlank()) {
            questionEnglish
        } else {
            questionHindi
        }
    }

    fun getHint(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI && hintEnglish.isNotBlank()) {
            hintEnglish
        } else {
            hintHindi
        }
    }
}

data class GeneratedWorksheet(
    val id: String,
    val titleHindi: String,
    val titleSantaliOlChiki: String,
    val titleSantaliDevanagari: String,
    val grade: FLNGrade,
    val category: FLNCategory,
    val exercises: List<WorksheetExercise>,
    val generatedTimestamp: Long = System.currentTimeMillis(),
    val titleEnglish: String = ""
) {
    fun getTitle(mode: LanguagePairMode): String {
        return if (mode == LanguagePairMode.ENGLISH_TO_SANTALI && titleEnglish.isNotBlank()) {
            titleEnglish
        } else {
            titleHindi
        }
    }
}

data class CorpusSentence(
    val id: Int,
    val english: String,
    val santaliOlChiki: String,
    val santaliDevanagari: String = "",
    val santaliPhonetic: String = ""
)
