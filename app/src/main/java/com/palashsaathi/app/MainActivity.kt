package com.palashsaathi.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.palashsaathi.app.engine.AudioSynthesisEngine
import com.palashsaathi.app.ui.screens.MainScreen
import com.palashsaathi.app.ui.theme.PalashSaathiTheme

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: AudioSynthesisEngine

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize audio engine
        audioEngine = AudioSynthesisEngine(applicationContext)

        // Request audio recording permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            PalashSaathiTheme {
                MainScreen(audioEngine = audioEngine)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.shutdown()
    }
}
