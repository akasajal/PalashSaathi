package com.palashsaathi.app.engine

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

/**
 * 100% Native on-device AudioRecord engine.
 * Streams raw microphone PCM audio directly inside the app, calculates real-time
 * acoustic amplitude for live UI waveforms, and performs Voice Activity Detection (VAD).
 * Requires ZERO external Google intents, zero cloud services, and zero network calls.
 */
class AudioRecordEngine {

    companion object {
        private const val TAG = "AudioRecordEngine"
        private const val SAMPLE_RATE = 16000 // 16 kHz standard for speech acoustic processing
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val engineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    var isRecording: Boolean = false
        private set

    /**
     * Starts listening directly from the device microphone.
     * @param onAmplitude Callback with normalized live acoustic amplitude (0.0f to 1.0f) for the UI visualizer.
     * @param onSpeechFinished Callback when speech capture completes with raw PCM bytes and duration in ms.
     */
    @SuppressLint("MissingPermission")
    fun startRecording(
        onAmplitude: (Float) -> Unit,
        onSpeechFinished: (pcmData: ByteArray, durationMs: Long) -> Unit
    ) {
        if (isRecording) return

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = (minBufferSize * 2).coerceAtLeast(2048)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                return
            }

            audioRecord?.startRecording()
            isRecording = true

            recordingJob = engineScope.launch {
                val pcmOutputStream = ByteArrayOutputStream()
                val shortBuffer = ShortArray(1024)
                val byteBuffer = ByteArray(2048)
                val startTime = System.currentTimeMillis()

                var hasDetectedSpeech = false
                var silenceFrames = 0
                val maxSilenceFrames = 22 // Approx 1.4 seconds of silence after speech

                while (isActive && isRecording) {
                    val readCount = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: 0
                    if (readCount > 0) {
                        // Calculate real-time acoustic RMS amplitude
                        var sum = 0.0
                        for (i in 0 until readCount) {
                            val sample = shortBuffer[i]
                            // Convert short to byte for PCM stream
                            byteBuffer[i * 2] = (sample.toInt() and 0xFF).toByte()
                            byteBuffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
                            sum += sample * sample
                        }
                        pcmOutputStream.write(byteBuffer, 0, readCount * 2)

                        val rms = sqrt(sum / readCount)
                        val normalized = (rms / 22000.0).toFloat().coerceIn(0f, 1f)

                        withContext(Dispatchers.Main) {
                            onAmplitude(normalized)
                        }

                        // Voice Activity Detection (VAD)
                        if (normalized > 0.06f) {
                            hasDetectedSpeech = true
                            silenceFrames = 0
                        } else if (hasDetectedSpeech) {
                            silenceFrames++
                            // Automatically stop when the user finishes speaking and pauses
                            if (silenceFrames >= maxSilenceFrames) {
                                break
                            }
                        }

                        // Max safety recording timeout (8 seconds)
                        if (System.currentTimeMillis() - startTime > 8000) {
                            break
                        }
                    } else {
                        delay(20)
                    }
                }

                val duration = System.currentTimeMillis() - startTime
                val pcmBytes = pcmOutputStream.toByteArray()

                stopInternal()

                withContext(Dispatchers.Main) {
                    onAmplitude(0f)
                    onSpeechFinished(pcmBytes, duration)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in native AudioRecord", e)
            stopInternal()
        }
    }

    fun stopRecording(cancel: Boolean = false) {
        if (cancel) {
            recordingJob?.cancel()
            recordingJob = null
            stopInternal()
        } else {
            isRecording = false
            try {
                audioRecord?.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping AudioRecord", e)
            }
        }
    }

    private fun stopInternal() {
        isRecording = false
        try {
            audioRecord?.stop()
        } catch (_: Exception) {}
        try {
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing AudioRecord", e)
        } finally {
            audioRecord = null
            recordingJob = null
        }
    }
}
