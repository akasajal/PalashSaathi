package com.palashsaathi.app.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.sin

class AudioSynthesisEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale("hi", "IN"))
                    isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioSynthesisEngine", "Error initializing TTS", e)
        }
    }

    /**
     * Speaks the Santali phonetic or Devanagari string. Uses native TTS if available,
     * or generates an audible acoustic waveform directly via AudioTrack.
     */
    suspend fun speakSantali(phoneticText: String, devanagariText: String) = withContext(Dispatchers.Default) {
        if (isTtsReady && tts != null) {
            val textToSpeak = devanagariText.ifEmpty { phoneticText }
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "santali_audio_id")
        } else {
            playAcousticChime()
        }
    }

    // Alias for backward compatibility
    suspend fun speakHo(phoneticText: String, devanagariText: String) = speakSantali(phoneticText, devanagariText)

    /**
     * Plays a pleasant multi-tone chime indicating voice response completion
     */
    private fun playAcousticChime() {
        val sampleRate = 22050
        val durationMs = 350
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)

        val freq1 = 523.25 // C5
        val freq2 = 659.25 // E5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples) // decay
            val sample = ((sin(2.0 * Math.PI * freq1 * t) + sin(2.0 * Math.PI * freq2 * t)) * 0.4 * envelope * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
        } catch (e: Exception) {
            Log.e("AudioSynthesisEngine", "AudioTrack play error", e)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
