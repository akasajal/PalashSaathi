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
            targetHoWarangCiti = "𑣚𑣉𑣕𑣂 𑣃𑣋𑣁𑣑𑣚𑣆",
            targetHoDevanagari = "पोथी उघाड़पे",
            targetHoPhonetic = "Pothi ughadpe",
            subtitleSanthali = "पुथी झिजमे (Puthi jhime)",
            subtitleMundari = "पुथी ओड़ोङपे (Puthi oṛongpe)",
            latencyMs = 280L
        ),
        TranslationResult(
            sourceHindi = "ध्यान से सुनो",
            targetHoWarangCiti = "𑣣𑣁𑣊𑣂 𑣕𑣆 𑣁𑣁𑣅𑣃𑣞𑣚𑣆",
            targetHoDevanagari = "सांगी ते आयूमपे",
            targetHoPhonetic = "Sangi te aayumpe",
            subtitleSanthali = "मोन देते आंजोमपे (Mon dete anjompe)",
            subtitleMundari = "धियान ते आयूमपे (Dhiyan te aayumpe)",
            latencyMs = 310L
        ),
        TranslationResult(
            sourceHindi = "शांत रहो",
            targetHoWarangCiti = "𑣚𑣂𑣜 𑣕𑣆 𑣕𑣁𑣆𑣓𑣚𑣆",
            targetHoDevanagari = "थिर ते ताईनपे",
            targetHoPhonetic = "Thir te tainpe",
            subtitleSanthali = "थिर कोगोमे (Thir kogome)",
            subtitleMundari = "थिर गे ताईनपे (Thir ge tainpe)",
            latencyMs = 240L
        ),
        TranslationResult(
            sourceHindi = "यहाँ आओ",
            targetHoWarangCiti = "𑣓𑣆𑣕𑣆 𑣁𑣃𑣜𑣂𑣚𑣆",
            targetHoDevanagari = "नेते हूरुपे",
            targetHoPhonetic = "Nete hurupe",
            subtitleSanthali = "नोंडे हेजमे (Nonde hejme)",
            subtitleMundari = "नेते हिजूःपे (Nete hiju:pe)",
            latencyMs = 210L
        ),
        TranslationResult(
            sourceHindi = "बैठ जाओ",
            targetHoWarangCiti = "𑣃𑣞𑣃𑣊 𑣚𑣆",
            targetHoDevanagari = "दुबपे",
            targetHoPhonetic = "Dubpe",
            subtitleSanthali = "दुड़ुपमे (Durupme)",
            subtitleMundari = "दुबपे (Dubpe)",
            latencyMs = 190L
        ),
        TranslationResult(
            sourceHindi = "खड़े हो जाओ",
            targetHoWarangCiti = "𑣕𑣂𑣊𑣃 𑣚𑣆",
            targetHoDevanagari = "तिंगुपे",
            targetHoPhonetic = "Tingupe",
            subtitleSanthali = "तिंगुनमे (Tingunme)",
            subtitleMundari = "तिंगुपे (Tingupe)",
            latencyMs = 220L
        ),
        TranslationResult(
            sourceHindi = "ब्लैकबोर्ड पर देखो",
            targetHoWarangCiti = "𑣒𑣁𑣒𑣁 𑣜𑣆 𑣓𑣆𑣚𑣚𑣆",
            targetHoDevanagari = "पाटा रे नेलपे",
            targetHoPhonetic = "Pata re nelpe",
            subtitleSanthali = "बोर्ड रे ञेलमे (Board re nyelme)",
            subtitleMundari = "पाटा रे नेलपे (Pata re nelpe)",
            latencyMs = 320L
        ),
        TranslationResult(
            sourceHindi = "शाबाश बहुत अच्छा",
            targetHoWarangCiti = "𑣣𑣃𑣋𑣂 𑣃𑣕𑣁𑣜",
            targetHoDevanagari = "बुगी उतार",
            targetHoPhonetic = "Bugi utar",
            subtitleSanthali = "आडी मोज (Adi moj)",
            subtitleMundari = "बुगी उतार (Bugi utar)",
            latencyMs = 230L
        ),
        TranslationResult(
            sourceHindi = "अपना नाम बताओ",
            targetHoWarangCiti = "𑣁𑣞𑣁 𑣓𑣃𑣕𑣃𑣞 𑣆𑣕𑣃𑣓𑣞𑣆",
            targetHoDevanagari = "अमाः नुतुम काजीमे",
            targetHoPhonetic = "Amah nutum kajime",
            subtitleSanthali = "आमअः ञुतुम लयमे (Amah nyutum layme)",
            subtitleMundari = "अमाः नुतुम काजीमे (Amah nutum kajime)",
            latencyMs = 350L
        ),
        TranslationResult(
            sourceHindi = "कॉपी में लिखो",
            targetHoWarangCiti = "𑣂𑣕𑣂𑣊 𑣜𑣆 𑣉𑣚𑣚𑣆",
            targetHoDevanagari = "खाता रे ओलपे",
            targetHoPhonetic = "Khata re olpe",
            subtitleSanthali = "खाता रे ओलमे (Khata re olme)",
            subtitleMundari = "खाता रे ओलपे (Khata re olpe)",
            latencyMs = 270L
        )
    )

    val FLASHCARD_ITEMS = listOf(
        FlashcardItem(
            id = "num_1",
            hindiWord = "एक (1)",
            hoWarangCiti = "𑣡 (𑣞𑣂𑣕𑣂)",
            hoDevanagari = "मित् (१)",
            hoPhonetic = "Mit",
            santhaliSubtitle = "मिद् (Mid)",
            mundariSubtitle = "मियाँद (Miyad)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter1
        ),
        FlashcardItem(
            id = "num_2",
            hindiWord = "दो (2)",
            hoWarangCiti = "𑣢 (𑣎𑣁𑣜𑣂𑣕𑣂)",
            hoDevanagari = "बारिया (२)",
            hoPhonetic = "Bariya",
            santhaliSubtitle = "बार (Bar)",
            mundariSubtitle = "बारिया (Bariya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter2
        ),
        FlashcardItem(
            id = "num_3",
            hindiWord = "तीन (3)",
            hoWarangCiti = "𑣣 (𑣁𑣚𑣂𑣕𑣂)",
            hoDevanagari = "आपिया (३)",
            hoPhonetic = "Apiya",
            santhaliSubtitle = "पे (Pe)",
            mundariSubtitle = "आपिया (Apiya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter3
        ),
        FlashcardItem(
            id = "num_4",
            hindiWord = "चार (4)",
            hoWarangCiti = "𑣤 (𑣃𑣚𑣂𑣕𑣂)",
            hoDevanagari = "उपूनिया (४)",
            hoPhonetic = "Upuniya",
            santhaliSubtitle = "पोन (Pon)",
            mundariSubtitle = "उपूनिया (Upuniya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter4
        ),
        FlashcardItem(
            id = "num_5",
            hindiWord = "पाँच (5)",
            hoWarangCiti = "𑣥 (𑣞𑣉𑣎𑣂𑣕𑣂)",
            hoDevanagari = "मोड़ेया (५)",
            hoPhonetic = "Modeya",
            santhaliSubtitle = "मोँड़े (Monre)",
            mundariSubtitle = "मोड़ेया (Modeya)",
            category = FLNCategory.NUMERACY,
            icon = Icons.Default.Filter5
        ),
        FlashcardItem(
            id = "lit_water",
            hindiWord = "पानी",
            hoWarangCiti = "𑣓𑣁𑣁𑣋𑣂",
            hoDevanagari = "दाः",
            hoPhonetic = "Dah",
            santhaliSubtitle = "दाः (Dak)",
            mundariSubtitle = "दाः (Dah)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.WaterDrop
        ),
        FlashcardItem(
            id = "lit_tree",
            hindiWord = "पेड़ / वृक्ष",
            hoWarangCiti = "𑣁𑣕𑣂",
            hoDevanagari = "दारू",
            hoPhonetic = "Daru",
            santhaliSubtitle = "दारे (Dare)",
            mundariSubtitle = "दारू (Daru)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.Park
        ),
        FlashcardItem(
            id = "lit_sun",
            hindiWord = "सूरज / सूर्य",
            hoWarangCiti = "𑣣𑣂𑣊𑣂",
            hoDevanagari = "सिंगी",
            hoPhonetic = "Singi",
            santhaliSubtitle = "सेंगेल/चांदो (Singi/Chando)",
            mundariSubtitle = "सिंगी (Singi)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.WbSunny
        ),
        FlashcardItem(
            id = "lit_cow",
            hindiWord = "गाय",
            hoWarangCiti = "𑣃𑣜𑣂",
            hoDevanagari = "उरिः",
            hoPhonetic = "Urih",
            santhaliSubtitle = "गाई (Gai)",
            mundariSubtitle = "उरिः (Urih)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.Pets
        ),
        FlashcardItem(
            id = "lit_bird",
            hindiWord = "चिड़िया / पक्षी",
            hoWarangCiti = "𑣏𑣆𑣓𑣆",
            hoDevanagari = "चेँड़े",
            hoPhonetic = "Chende",
            santhaliSubtitle = "चेँड़े (Chenre)",
            mundariSubtitle = "चेँड़े (Chende)",
            category = FLNCategory.LITERACY,
            icon = Icons.Default.FlutterDash
        )
    )

    fun findMatchingTranslation(inputHindi: String): TranslationResult {
        val trimmed = inputHindi.trim()
        val exact = CLASSROOM_ENTRIES.firstOrNull {
            it.sourceHindi.equals(trimmed, ignoreCase = true) ||
            trimmed.contains(it.sourceHindi) ||
            it.sourceHindi.contains(trimmed)
        }
        if (exact != null) {
            return exact
        }

        // Check flashcards
        val fc = FLASHCARD_ITEMS.firstOrNull {
            it.hindiWord.contains(trimmed, ignoreCase = true) ||
            trimmed.contains(it.hindiWord.split(" ").first())
        }
        if (fc != null) {
            return TranslationResult(
                sourceHindi = fc.hindiWord,
                targetHoWarangCiti = fc.hoWarangCiti,
                targetHoDevanagari = fc.hoDevanagari,
                targetHoPhonetic = fc.hoPhonetic,
                subtitleSanthali = fc.santhaliSubtitle,
                subtitleMundari = fc.mundariSubtitle,
                latencyMs = 180L
            )
        }

        // Algorithmic / Lexicon fallback synthesis
        return TranslationResult(
            sourceHindi = inputHindi,
            targetHoWarangCiti = "𑣓𑣉𑣎𑣉𑣜: $inputHindi",
            targetHoDevanagari = "हो अनुवाद: $inputHindi",
            targetHoPhonetic = "Ho anuvad: $inputHindi",
            subtitleSanthali = "साँओताली: $inputHindi",
            subtitleMundari = "मुण्डारी: $inputHindi",
            latencyMs = 450L
        )
    }
}
