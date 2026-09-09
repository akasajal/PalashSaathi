package com.palashsaathi.app.engine

import com.palashsaathi.app.data.model.LanguagePairMode
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * On-device acoustic feature extractor and phrase classifier.
 * Analyzes raw 16-bit 16kHz PCM audio buffers for syllable segmentation,
 * Zero-Crossing Rate (ZCR), energy envelope distribution, and duration.
 */
object AcousticKeywordSpotter {

    data class AcousticProfile(
        val phrase: String,
        val targetSyllables: Int,
        val targetZcrHz: Int,
        val targetDurationMs: Long,
        val frontHeavy: Boolean
    )

    private val HINDI_PROFILES = listOf(
        AcousticProfile("शाबाश", 2, 2100, 700L, true),
        AcousticProfile("शांत रहो", 3, 2200, 950L, true),
        AcousticProfile("बैठ जाओ", 3, 1100, 900L, false),
        AcousticProfile("यहाँ आओ", 3, 900, 850L, false),
        AcousticProfile("पानी पियो", 4, 1200, 1100L, false),
        AcousticProfile("किताब खोलो", 4, 1750, 1300L, true),
        AcousticProfile("खड़े हो जाओ", 4, 1350, 1250L, false),
        AcousticProfile("ध्यान से सुनो", 4, 1900, 1400L, false),
        AcousticProfile("ब्लैकबोर्ड पर देखो", 5, 1600, 1750L, false),
        AcousticProfile("यह पन्ना पढ़ो", 4, 1450, 1350L, true)
    )

    private val ENGLISH_PROFILES = listOf(
        AcousticProfile("Stand up", 2, 2300, 750L, true),
        AcousticProfile("Sit down", 2, 1900, 750L, true),
        AcousticProfile("Come here", 2, 1100, 700L, false),
        AcousticProfile("Well done", 2, 1000, 800L, false),
        AcousticProfile("Be quiet", 2, 1800, 850L, false),
        AcousticProfile("Drink water", 3, 1300, 1050L, true),
        AcousticProfile("Open your book", 4, 1600, 1350L, true),
        AcousticProfile("Listen carefully", 5, 2100, 1500L, true),
        AcousticProfile("Look at the blackboard", 5, 1700, 1700L, false),
        AcousticProfile("Read this page", 3, 1750, 1100L, false)
    )

    /**
     * Classifies raw PCM 16-bit 16kHz audio into the most probable spoken classroom verbal command.
     */
    fun classifyPcmAudio(pcmData: ByteArray, durationMs: Long, languageMode: LanguagePairMode): String {
        val profiles = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) ENGLISH_PROFILES else HINDI_PROFILES
        if (pcmData.size < 3200) {
            // Under 100ms of audio, return default first prompt
            return profiles.first().phrase
        }

        val sampleCount = pcmData.size / 2
        val samples = ShortArray(sampleCount)
        for (i in 0 until sampleCount) {
            val lo = pcmData[i * 2].toInt() and 0xFF
            val hi = pcmData[i * 2 + 1].toInt()
            samples[i] = ((hi shl 8) or lo).toShort()
        }

        // 1. Calculate Zero Crossing Rate (ZCR) - distinguishes fricatives ("sh", "s", "th") from sonorants
        var zeroCrossings = 0
        for (i in 1 until sampleCount) {
            if ((samples[i] >= 0 && samples[i - 1] < 0) || (samples[i] < 0 && samples[i - 1] >= 0)) {
                zeroCrossings++
            }
        }
        val durationSec = (durationMs.coerceAtLeast(100L)) / 1000.0
        val zcrHz = (zeroCrossings / (2.0 * durationSec)).toInt()

        // 2. Calculate frame energies for syllable counting & front/back bias
        val frameSize = 320 // 20ms at 16kHz
        val numFrames = sampleCount / frameSize
        val frameEnergies = FloatArray(numFrames)
        var maxEnergy = 0.001f

        for (f in 0 until numFrames) {
            var sumSquare = 0.0
            val offset = f * frameSize
            for (s in 0 until frameSize) {
                val v = samples[offset + s].toDouble()
                sumSquare += v * v
            }
            val rms = sqrt(sumSquare / frameSize).toFloat()
            frameEnergies[f] = rms
            if (rms > maxEnergy) maxEnergy = rms
        }

        // Syllable peak detection (peaks >= 30% max energy separated by at least 100ms / 5 frames)
        var syllables = 0
        var lastPeakFrame = -10
        val threshold = maxEnergy * 0.30f

        for (f in 1 until numFrames - 1) {
            val cur = frameEnergies[f]
            if (cur > threshold && cur > frameEnergies[f - 1] && cur >= frameEnergies[f + 1]) {
                if (f - lastPeakFrame >= 5) {
                    syllables++
                    lastPeakFrame = f
                }
            }
        }
        val detectedSyllables = syllables.coerceIn(2, 6)

        // Front vs back heavy energy distribution
        val halfFrames = numFrames / 2
        var frontEnergySum = 0.0
        var backEnergySum = 0.0
        for (f in 0 until numFrames) {
            if (f < halfFrames) frontEnergySum += frameEnergies[f]
            else backEnergySum += frameEnergies[f]
        }
        val isFrontHeavy = frontEnergySum >= backEnergySum

        // Find best matching profile using weighted distance
        var bestScore = Double.MAX_VALUE
        var bestMatch = profiles.first().phrase

        for (profile in profiles) {
            val sylDiff = abs(profile.targetSyllables - detectedSyllables) * 2.0
            val zcrDiff = (abs(profile.targetZcrHz - zcrHz) / 500.0)
            val durDiff = (abs(profile.targetDurationMs - durationMs) / 600.0)
            val biasPenalty = if (profile.frontHeavy == isFrontHeavy) 0.0 else 0.8

            val totalScore = sylDiff + zcrDiff + durDiff + biasPenalty
            if (totalScore < bestScore) {
                bestScore = totalScore
                bestMatch = profile.phrase
            }
        }

        return bestMatch
    }
}
