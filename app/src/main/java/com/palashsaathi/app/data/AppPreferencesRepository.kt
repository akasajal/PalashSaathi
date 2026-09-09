package com.palashsaathi.app.data

import android.content.Context
import android.content.SharedPreferences
import com.palashsaathi.app.data.model.LanguagePairMode
import com.palashsaathi.app.data.model.ScriptType
import com.palashsaathi.app.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persistent preferences repository for user settings.
 * Persists Theme Mode, Translation Language Mode, Script Type, and UI states
 * to Android SharedPreferences across app refreshes and process restarts.
 */
class AppPreferencesRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "palash_saathi_user_preferences"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_LANGUAGE_MODE = "key_language_mode"
        private const val KEY_SCRIPT_TYPE = "key_script_type"
        private const val KEY_LANDING_SYNOPSIS_DISMISSED = "key_landing_synopsis_dismissed"

        @Volatile
        private var INSTANCE: AppPreferencesRepository? = null

        fun getInstance(context: Context): AppPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferencesRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Reactive StateFlows initialized synchronously from disk
    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _languageMode = MutableStateFlow(loadLanguageMode())
    val languageMode: StateFlow<LanguagePairMode> = _languageMode.asStateFlow()

    private val _scriptType = MutableStateFlow(loadScriptType())
    val scriptType: StateFlow<ScriptType> = _scriptType.asStateFlow()

    private val _showLandingSynopsis = MutableStateFlow(!prefs.getBoolean(KEY_LANDING_SYNOPSIS_DISMISSED, false))
    val showLandingSynopsis: StateFlow<Boolean> = _showLandingSynopsis.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setLanguageMode(mode: LanguagePairMode) {
        prefs.edit().putString(KEY_LANGUAGE_MODE, mode.name).apply()
        _languageMode.value = mode
    }

    fun setScriptType(script: ScriptType) {
        prefs.edit().putString(KEY_SCRIPT_TYPE, script.name).apply()
        _scriptType.value = script
    }

    fun dismissLandingSynopsis() {
        prefs.edit().putBoolean(KEY_LANDING_SYNOPSIS_DISMISSED, true).apply()
        _showLandingSynopsis.value = false
    }

    private fun loadThemeMode(): AppThemeMode {
        val raw = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(raw ?: AppThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    private fun loadLanguageMode(): LanguagePairMode {
        val raw = prefs.getString(KEY_LANGUAGE_MODE, LanguagePairMode.HINDI_TO_SANTALI.name)
        return try {
            LanguagePairMode.valueOf(raw ?: LanguagePairMode.HINDI_TO_SANTALI.name)
        } catch (_: Exception) {
            LanguagePairMode.HINDI_TO_SANTALI
        }
    }

    private fun loadScriptType(): ScriptType {
        val raw = prefs.getString(KEY_SCRIPT_TYPE, ScriptType.OL_CHIKI.name)
        return try {
            ScriptType.valueOf(raw ?: ScriptType.OL_CHIKI.name)
        } catch (_: Exception) {
            ScriptType.OL_CHIKI
        }
    }
}
