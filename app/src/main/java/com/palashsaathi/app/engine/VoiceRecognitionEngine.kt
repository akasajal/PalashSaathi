package com.palashsaathi.app.engine

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.palashsaathi.app.data.model.LanguagePairMode

/**
 * 100% In-App Headless Voice Recognition Engine.
 *
 * Utilizes Android's native SpeechRecognizer service without ANY external
 * dialogs or Google modal popups (zero RecognizerIntent Activity launchers).
 *
 * Features:
 * - Operates entirely within the app's custom Compose UI.
 * - Streams live acoustic RMS amplitude decibels to modulate the UI microphone scale.
 * - Streams real-time interim partial transcriptions as the teacher speaks.
 * - Auto-detects on-device recognition (Android 12+ API 31+).
 * - Multi-language support: Hindi ("hi-IN") and Indian English ("en-IN").
 * - Fully thread-safe (ensures all speech service calls occur on the Main Looper).
 */
class VoiceRecognitionEngine(private val context: Context) {

    companion object {
        private const val TAG = "VoiceRecognitionEngine"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening: Boolean = false

    private var onAmplitudeCallback: ((Float) -> Unit)? = null
    private var onPartialCallback: ((String) -> Unit)? = null
    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    val isRecognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    /**
     * Initializes the SpeechRecognizer instance on the main thread.
     */
    private fun ensureRecognizer(): SpeechRecognizer? {
        if (speechRecognizer != null) return speechRecognizer

        return try {
            val recognizer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
            ) {
                SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
            } else {
                SpeechRecognizer.createSpeechRecognizer(context)
            }

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d(TAG, "SpeechRecognizer ready for speech")
                }

                override fun onBeginningOfSpeech() {
                    Log.d(TAG, "SpeechRecognizer beginning of speech")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Normalize typical Android rmsdB range (-2.0f .. 10.0f) to 0.0f .. 1.0f
                    val normalized = ((rmsdB + 2.0f) / 11.0f).coerceIn(0.0f, 1.0f)
                    mainHandler.post {
                        onAmplitudeCallback?.invoke(normalized)
                    }
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Log.d(TAG, "SpeechRecognizer end of speech")
                    mainHandler.post {
                        onAmplitudeCallback?.invoke(0f)
                    }
                }

                override fun onError(error: Int) {
                    isListening = false
                    mainHandler.post {
                        onAmplitudeCallback?.invoke(0f)
                    }

                    val message = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No verbal words recognized. Please speak clearly."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Please speak into the mic."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                        SpeechRecognizer.ERROR_NETWORK -> "Network required for speech model."
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer busy, retrying..."
                        SpeechRecognizer.ERROR_SERVER -> "Speech recognition server error."
                        SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
                        else -> "Speech recognition error ($error)."
                    }
                    Log.w(TAG, "SpeechRecognizer error: $error -> $message")

                    mainHandler.post {
                        onErrorCallback?.invoke(message)
                    }
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    mainHandler.post {
                        onAmplitudeCallback?.invoke(0f)
                    }

                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spokenText = matches?.firstOrNull()?.trim() ?: ""
                    Log.d(TAG, "SpeechRecognizer results: $spokenText (candidates: $matches)")

                    mainHandler.post {
                        if (spokenText.isNotBlank()) {
                            onResultCallback?.invoke(spokenText)
                        } else {
                            onErrorCallback?.invoke("No words recognized.")
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partialText = matches?.firstOrNull()?.trim() ?: ""
                    if (partialText.isNotBlank()) {
                        mainHandler.post {
                            onPartialCallback?.invoke(partialText)
                        }
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer = recognizer
            recognizer
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create SpeechRecognizer", e)
            null
        }
    }

    /**
     * Starts listening directly inside the app with NO popup dialogs.
     */
    fun startListening(
        languageMode: LanguagePairMode,
        onAmplitude: (Float) -> Unit,
        onPartial: (String) -> Unit,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        this.onAmplitudeCallback = onAmplitude
        this.onPartialCallback = onPartial
        this.onResultCallback = onResult
        this.onErrorCallback = onError

        mainHandler.post {
            try {
                val recognizer = ensureRecognizer()
                if (recognizer == null) {
                    onError("Speech recognition not available on this device.")
                    return@post
                }

                val langTag = if (languageMode == LanguagePairMode.ENGLISH_TO_SANTALI) "en-IN" else "hi-IN"
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }

                isListening = true
                recognizer.startListening(intent)
                Log.d(TAG, "SpeechRecognizer started listening with language: $langTag")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting SpeechRecognizer", e)
                isListening = false
                onError("Failed to start speech recognition: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Stops listening gracefully and requests transcription results.
     */
    fun stopListening() {
        mainHandler.post {
            try {
                if (isListening) {
                    speechRecognizer?.stopListening()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping SpeechRecognizer", e)
            } finally {
                isListening = false
            }
        }
    }

    /**
     * Cancels active recognition.
     */
    fun cancel() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e(TAG, "Error canceling SpeechRecognizer", e)
            } finally {
                isListening = false
            }
        }
    }

    /**
     * Releases recognizer resources.
     */
    fun destroy() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying SpeechRecognizer", e)
            } finally {
                speechRecognizer = null
                isListening = false
            }
        }
    }
}
