package com.palashsaathi.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.palashsaathi.app.data.model.FLNCategory
import com.palashsaathi.app.data.model.FlashcardItem
import com.palashsaathi.app.data.model.TranslationResult

object FLNDictionary {

    val CLASSROOM_ENTRIES = listOf(
        TranslationResult(
            sourceHindi = "किताब खोलो",
            targetSantaliOlChiki = "ᱯᱩᱛᱷᱤ ᱡᱷᱤᱡᱽᱢᱮ",
            targetSantaliDevanagari = "पुथी झिजमे",
            targetSantaliPhonetic = "Puthi jhijme",
            subtitleHo = "पोथी उघाड़पे (Pothi ughadpe)",
            subtitleMundari = "पुथी ओड़ोङपे (Puthi orongpe)",
            latencyMs = 280L
        ),
        TranslationResult(
            sourceHindi = "ध्यान से सुनो",
            targetSantaliOlChiki = "ᱢᱚᱱ ᱮᱢ ᱠᱟᱛᱮ ᱟᱧᱡᱚᱢᱯᱮ",
            targetSantaliDevanagari = "मोन देते आंजोमपे",
            targetSantaliPhonetic = "Mon dete anjompe",
            subtitleHo = "सांगी ते आयूमपे (Sangi te aayumpe)",
            subtitleMundari = "धियान ते आयूमपे (Dhiyan te aayumpe)",
            latencyMs = 310L
        ),
        TranslationResult(
            sourceHindi = "शांत रहो",
            targetSantaliOlChiki = "ᱛᱷᱤᱨ ᱠᱚᱜᱳᱢᱮ",
            targetSantaliDevanagari = "थिर कोगोमे",
            targetSantaliPhonetic = "Thir kogome",
            subtitleHo = "थिर ते ताईनपे (Thir te tainpe)",
            subtitleMundari = "थिर गे ताईनपे (Thir ge tainpe)",
            latencyMs = 240L
        ),
        TranslationResult(
            sourceHindi = "यहाँ आओ",
            targetSantaliOlChiki = "ᱱᱚᱸᱰᱮ ᱦᱤᱡᱩᱜᱽᱢᱮ",
            targetSantaliDevanagari = "नोंडे हेजमे",
            targetSantaliPhonetic = "Nonde hejme",
            subtitleHo = "नेते हूरुपे (Nete hurupe)",
            subtitleMundari = "नेते हिजूःपे (Nete hiju:pe)",
            latencyMs = 210L
        ),
        TranslationResult(
            sourceHindi = "बैठ जाओ",
            targetSantaliOlChiki = "ᱫᱩᱲᱩᱵᱽᱢᱮ",
            targetSantaliDevanagari = "दुड़ुपमे",
            targetSantaliPhonetic = "Durupme",
            subtitleHo = "दुबपे (Dubpe)",
            subtitleMundari = "दुबपे (Dubpe)",
            latencyMs = 190L
        ),
        TranslationResult(
            sourceHindi = "खड़े हो जाओ",
            targetSantaliOlChiki = "ᱛᱤᱸᱜᱩᱱᱢᱮ",
            targetSantaliDevanagari = "तिंगुनमे",
            targetSantaliPhonetic = "Tingunme",
            subtitleHo = "तिंगुपे (Tingupe)",
            subtitleMundari = "तिंगुपे (Tingupe)",
            latencyMs = 220L
        ),
        TranslationResult(
            sourceHindi = "ब्लैकबोर्ड पर देखो",
            targetSantaliOlChiki = "ᱵᱳᱨᱰ ᱨᱮ ᱧᱮᱞᱢᱮ",
            targetSantaliDevanagari = "बोर्ड रे ञेलमे",
            targetSantaliPhonetic = "Board re nyelme",
            subtitleHo = "पाटा रे नेलपे (Pata re nelpe)",
            subtitleMundari = "पाटा रे नेलपे (Pata re nelpe)",
            latencyMs = 320L
        ),
        TranslationResult(
            sourceHindi = "शाबाश बहुत अच्छा",
            targetSantaliOlChiki = "ᱟᱹᱰᱤ ᱢᱚᱡᱽ",
            targetSantaliDevanagari = "आडी मोज",
            targetSantaliPhonetic = "Adi moj",
            subtitleHo = "बुगी उतार (Bugi utar)",
            subtitleMundari = "बुगी उतार (Bugi utar)",
            latencyMs = 230L
        ),
        TranslationResult(
            sourceHindi = "अपना नाम बताओ",
            targetSantaliOlChiki = "ᱟᱢᱟᱜ ᱧᱩᱛᱩᱢ ᱞᱟᱹᱭᱢᱮ",
            targetSantaliDevanagari = "आमअः ञुतुम लयमे",
            targetSantaliPhonetic = "Amah nyutum layme",
            subtitleHo = "अमाः नुतुम काजीमे (Amah nutum kajime)",
            subtitleMundari = "अमाः नुतुम काजीमे (Amah nutum kajime)",
            latencyMs = 350L
        ),
        TranslationResult(
            sourceHindi = "कॉपी में लिखो",
            targetSantaliOlChiki = "ᱠᱷᱟᱛᱟ ᱨᱮ ᱚᱞᱢᱮ",
            targetSantaliDevanagari = "खाता रे ओलमे",
            targetSantaliPhonetic = "Khata re olme",
            subtitleHo = "खाता रे ओलपे (Khata re olpe)",
            subtitleMundari = "खाता रे ओलपे (Khata re olpe)",
            latencyMs = 270L
        )
    )

    val FLASHCARD_ITEMS = listOf(
        FlashcardItem(
            id = "num_1",
            hindiWord = "एक (1)",
            santaliOlChiki = "᱑ (ᱢᱤᱫ)",
            santaliDevanagari = "मिद् (१)",
            santaliPhonetic = "Mid",
            hoSubtitle = "मित् (Mit)",
            mundariSubtitle = "मियाँद (Miyad)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter1
        ),
        FlashcardItem(
            id = "num_2",
            hindiWord = "दो (2)",
            santaliOlChiki = "᱒ (ᱵᱟᱨ)",
            santaliDevanagari = "बार (२)",
            santaliPhonetic = "Bar",
            hoSubtitle = "बारिया (Bariya)",
            mundariSubtitle = "बारिया (Bariya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter2
        ),
        FlashcardItem(
            id = "num_3",
            hindiWord = "तीन (3)",
            santaliOlChiki = "᱓ (ᱯᱮ)",
            santaliDevanagari = "पे (३)",
            santaliPhonetic = "Pe",
            hoSubtitle = "आपिया (Apiya)",
            mundariSubtitle = "आपिया (Apiya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter3
        ),
        FlashcardItem(
            id = "num_4",
            hindiWord = "चार (4)",
            santaliOlChiki = "᱔ (ᱯᱳᱱ)",
            santaliDevanagari = "पोन (४)",
            santaliPhonetic = "Pon",
            hoSubtitle = "उपूनिया (Upuniya)",
            mundariSubtitle = "उपूनिया (Upuniya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter4
        ),
        FlashcardItem(
            id = "num_5",
            hindiWord = "पाँच (5)",
            santaliOlChiki = "᱕ (ᱢᱚᱬᱮ)",
            santaliDevanagari = "मोँड़े (५)",
            santaliPhonetic = "Monre",
            hoSubtitle = "मोड़ेया (Modeya)",
            mundariSubtitle = "मोड़ेया (Modeya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter5
        ),
        FlashcardItem(
            id = "lit_water",
            hindiWord = "पानी",
            santaliOlChiki = "ᱫᱟᱜ",
            santaliDevanagari = "दाः",
            santaliPhonetic = "Dah",
            hoSubtitle = "दाः (Dah)",
            mundariSubtitle = "दाः (Dah)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.WaterDrop
        ),
        FlashcardItem(
            id = "lit_tree",
            hindiWord = "पेड़ / वृक्ष",
            santaliOlChiki = "ᱫᱟᱨᱮ",
            santaliDevanagari = "दारे",
            santaliPhonetic = "Dare",
            hoSubtitle = "दारू (Daru)",
            mundariSubtitle = "दारू (Daru)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.Park
        ),
        FlashcardItem(
            id = "lit_sun",
            hindiWord = "सूरज / सूर्य",
            santaliOlChiki = "ᱥᱤᱧ",
            santaliDevanagari = "सिंगी",
            santaliPhonetic = "Singi",
            hoSubtitle = "सिंगी (Singi)",
            mundariSubtitle = "सिंगी (Singi)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.WbSunny
        ),
        FlashcardItem(
            id = "lit_cow",
            hindiWord = "गाय",
            santaliOlChiki = "ᱜᱟᱹᱭ",
            santaliDevanagari = "गाई",
            santaliPhonetic = "Gai",
            hoSubtitle = "उरिः (Urih)",
            mundariSubtitle = "उरिः (Urih)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.Pets
        ),
        FlashcardItem(
            id = "lit_bird",
            hindiWord = "चिड़िया / पक्षी",
            santaliOlChiki = "ᱪᱮᱬᱮ",
            santaliDevanagari = "चेँड़े",
            santaliPhonetic = "Chenre",
            hoSubtitle = "चेँड़े (Chende)",
            mundariSubtitle = "चेँड़े (Chende)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.FlutterDash
        )
    )

    fun findMatchingTranslation(inputHindi: String): TranslationResult {
        val trimmed = inputHindi.trim()

        // 1. Check fixed classroom entries
        val exact = CLASSROOM_ENTRIES.firstOrNull {
            it.sourceHindi.equals(trimmed, ignoreCase = true) ||
            trimmed.contains(it.sourceHindi) ||
            it.sourceHindi.contains(trimmed)
        }
        if (exact != null) {
            return exact
        }

        // 2. Check foundational flashcards
        val fc = FLASHCARD_ITEMS.firstOrNull {
            it.hindiWord.contains(trimmed, ignoreCase = true) ||
            trimmed.contains(it.hindiWord.split(" ").first())
        }
        if (fc != null) {
            return TranslationResult(
                sourceHindi = fc.hindiWord,
                targetSantaliOlChiki = fc.santaliOlChiki,
                targetSantaliDevanagari = fc.santaliDevanagari,
                targetSantaliPhonetic = fc.santaliPhonetic,
                subtitleHo = fc.hoSubtitle,
                subtitleMundari = fc.mundariSubtitle,
                latencyMs = 180L
            )
        }

        // 3. Check 20K Corpus match
        val corpusMatch = SantaliCorpusRepository.findBestMatch(trimmed)
        if (corpusMatch != null) {
            return TranslationResult(
                sourceHindi = inputHindi,
                targetSantaliOlChiki = corpusMatch.santaliOlChiki,
                targetSantaliDevanagari = corpusMatch.santaliDevanagari.ifBlank { corpusMatch.santaliOlChiki },
                targetSantaliPhonetic = corpusMatch.santaliPhonetic,
                subtitleHo = "हो: ${corpusMatch.santaliPhonetic}",
                subtitleMundari = "मुण्डारी: ${corpusMatch.santaliPhonetic}",
                latencyMs = 210L,
                fromCorpus = true
            )
        }

        // 4. Fallback synthesis
        return TranslationResult(
            sourceHindi = inputHindi,
            targetSantaliOlChiki = "ᱚᱞ ᱪᱤᱠᱤ: $inputHindi",
            targetSantaliDevanagari = "संथाली अनुवाद: $inputHindi",
            targetSantaliPhonetic = "Santali anuvad: $inputHindi",
            subtitleHo = "हो: $inputHindi",
            subtitleMundari = "मुण्डारी: $inputHindi",
            latencyMs = 420L
        )
    }
}
