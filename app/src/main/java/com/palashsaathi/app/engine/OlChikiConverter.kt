package com.palashsaathi.app.engine

/**
 * Utility for Santali Ol Chiki (ᱚᱞ ᱪᱤᱠᱤ) script handling, numeral conversion,
 * and bidirectional transliteration with Devanagari and Latin phonetics.
 *
 * Ol Chiki was created by Pandit Raghunath Murmu in 1925 as the native writing system
 * for the Santali language.
 * Unicode Block: U+1C50 - U+1C7F.
 */
object OlChikiConverter {

    // Ol Chiki Digits: ᱐ (0) to ᱙ (9)
    private val OL_CHIKI_DIGITS = arrayOf(
        "\u1C50", // 0 ᱐
        "\u1C51", // 1 ᱑
        "\u1C52", // 2 ᱒
        "\u1C53", // 3 ᱓
        "\u1C54", // 4 ᱔
        "\u1C55", // 5 ᱕
        "\u1C56", // 6 ᱖
        "\u1C57", // 7 ᱗
        "\u1C58", // 8 ᱘
        "\u1C59"  // 9 ᱙
    )

    /**
     * Converts an integer or standard digit string into Ol Chiki numerals.
     */
    fun toOlChikiNumber(number: Int): String {
        val str = number.toString()
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(OL_CHIKI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Mapping from Ol Chiki characters to Devanagari equivalents.
     */
    private val OL_CHIKI_TO_DEVANAGARI = mapOf(
        'ᱚ' to "अ",
        'ᱛ' to "त्",
        'ᱜ' to "ग्",
        'ᱝ' to "ं",
        'ᱞ' to "ल्",
        'ᱟ' to "आ",
        'ᱠ' to "क्",
        'ᱡ' to "ज्",
        'ᱢ' to "म्",
        'ᱣ' to "व्",
        'ᱤ' to "इ",
        'ᱥ' to "स्",
        'ᱦ' to "ह्",
        'ᱧ' to "ञ्",
        'ᱨ' to "र्",
        'ᱩ' to "उ",
        'ᱪ' to "च्",
        'ᱫ' to "द्",
        'ᱬ' to "ण्",
        'ᱭ' to "य्",
        'ᱮ' to "ए",
        'ᱯ' to "प्",
        'ᱰ' to "ड्",
        'ᱱ' to "न्",
        'ᱲ' to "ड़्",
        'ᱳ' to "ओ",
        'ᱴ' to "ट्",
        'ᱵ' to "ब्",
        'ᱶ' to "ँ",
        'ᱷ' to "ह",
        'ᱸ' to "ँ",
        'ᱹ' to "",
        'ᱺ' to "",
        'ᱽ' to "",
        '᱾' to "।",
        '᱿' to "॥"
    )

    /**
     * Mapping from Ol Chiki characters to Latin phonetics.
     */
    private val OL_CHIKI_TO_LATIN = mapOf(
        'ᱚ' to "o",
        'ᱛ' to "t",
        'ᱜ' to "g",
        'ᱝ' to "ng",
        'ᱞ' to "l",
        'ᱟ' to "a",
        'ᱠ' to "k",
        'ᱡ' to "j",
        'ᱢ' to "m",
        'ᱣ' to "w",
        'ᱤ' to "i",
        'ᱥ' to "s",
        'ᱦ' to "h",
        'ᱧ' to "ny",
        'ᱨ' to "r",
        'ᱩ' to "u",
        'ᱪ' to "c",
        'ᱫ' to "d",
        'ᱬ' to "n",
        'ᱭ' to "y",
        'ᱮ' to "e",
        'ᱯ' to "p",
        'ᱰ' to "d",
        'ᱱ' to "n",
        'ᱲ' to "r",
        'ᱳ' to "o",
        'ᱴ' to "t",
        'ᱵ' to "b",
        'ᱶ' to "w",
        'ᱷ' to "h",
        'ᱸ' to "n",
        'ᱹ' to "",
        'ᱺ' to "",
        'ᱽ' to "'",
        '᱾' to ".",
        '᱿' to "."
    )

    /**
     * Converts Ol Chiki text to Devanagari transliteration.
     */
    fun toDevanagari(olChikiText: String): String {
        val sb = StringBuilder()
        for (ch in olChikiText) {
            sb.append(OL_CHIKI_TO_DEVANAGARI[ch] ?: ch.toString())
        }
        return sb.toString().replace("् ", " ").replace("्$", "")
    }

    /**
     * Converts Ol Chiki text to Romanized Latin phonetics.
     */
    fun toPhonetic(olChikiText: String): String {
        val sb = StringBuilder()
        for (ch in olChikiText) {
            sb.append(OL_CHIKI_TO_LATIN[ch] ?: ch.toString())
        }
        return sb.toString()
    }

    /**
     * Helper to render dual-script label: Ol Chiki followed by Devanagari
     */
    fun formatDualScript(olChiki: String, devanagari: String): String {
        return "$olChiki ($devanagari)"
    }
}
