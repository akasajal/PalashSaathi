package com.palashsaathi.app.engine

/**
 * Utility for Warang Citi (𑣓𑣉𑣎𑣉𑣜 𑣏𑣂𑣕𑣂) script handling and transliteration.
 * Warang Citi is the native writing system for the Ho language, codified by Lako Bodra.
 * Unicode Block: U+118A0 - U+118FF.
 */
object WarangCitiConverter {

    // Warang Citi Digits 0 - 9
    private val WARANG_CITI_DIGITS = arrayOf(
        "\uD806\uDCE0", // 0 𑣠
        "\uD806\uDCE1", // 1 𑣡
        "\uD806\uDCE2", // 2 𑣢
        "\uD806\uDCE3", // 3 𑣣
        "\uD806\uDCE4", // 4 𑣤
        "\uD806\uDCE5", // 5 𑣥
        "\uD806\uDCE6", // 6 𑣦
        "\uD806\uDCE7", // 7 𑣧
        "\uD806\uDCE8", // 8 𑣨
        "\uD806\uDCE9"  // 9 𑣩
    )

    /**
     * Converts an integer or standard digit string into Warang Citi numerals.
     */
    fun toWarangCitiNumber(number: Int): String {
        val str = number.toString()
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(WARANG_CITI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Map of common Ho phonemes to Warang Citi characters
     */
    private val DEVANAGARI_TO_WARANG_CITI = mapOf(
        "अ" to "\uD806\uDCBA", // 𑂺 / 𑣁
        "आ" to "\uD806\uDCA0", // 𑒠 / 𑣀
        "इ" to "\uD806\uDCA2", // 𑣂
        "ई" to "\uD806\uDCA3", // 𑣃
        "उ" to "\uD806\uDCA4", // 𑣄
        "ऊ" to "\uD806\uDCA5", // 𑣅
        "ए" to "\uD806\uDCA6", // 𑣆
        "ओ" to "\uD806\uDCA8", // 𑣈
        "क" to "\uD806\uDCAA", // 𑣊
        "ख" to "\uD806\uDCAB", // 𑣋
        "ग" to "\uD806\uDCAC", // 𑣌
        "घ" to "\uD806\uDCAD", // 𑣍
        "च" to "\uD806\uDCAE", // 𑣎
        "छ" to "\uD806\uDCAF", // 𑣏
        "ज" to "\uD806\uDCB0", // 𑣐
        "झ" to "\uD806\uDCB1", // 𑣑
        "ट" to "\uD806\uDCB2", // 𑣒
        "ठ" to "\uD806\uDCB3", // 𑣓
        "ड" to "\uD806\uDCB4", // 𑣔
        "ढ" to "\uD806\uDCB5", // 𑣕
        "त" to "\uD806\uDCB6", // 𑣖
        "थ" to "\uD806\uDCB7", // 𑣗
        "द" to "\uD806\uDCB8", // 𑣘
        "ध" to "\uD806\uDCB9", // 𑣙
        "न" to "\uD806\uDCB3", // 𑣓
        "प" to "\uD806\uDCBA", // 𑣚
        "फ" to "\uD806\uDCBB", // 𑣛
        "ब" to "\uD806\uDCBC", // 𑣜
        "भ" to "\uD806\uDCBD", // 𑣝
        "म" to "\uD806\uDCBE", // 𑣞
        "य" to "\uD806\uDCBF", // 𑣟
        "र" to "\uD806\uDCC0", // 𑣠
        "ल" to "\uD806\uDCC1", // 𑣡
        "व" to "\uD806\uDCC2", // 𑣢
        "स" to "\uD806\uDCC3", // 𑣣
        "ह" to "\uD806\uDCC4"  // 𑣤
    )

    /**
     * Helper to render dual-script label: Warang Citi followed by Devanagari
     */
    fun formatDualScript(warangCiti: String, devanagari: String): String {
        return "$warangCiti ($devanagari)"
    }
}
