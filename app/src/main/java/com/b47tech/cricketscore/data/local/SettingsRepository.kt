package com.b47tech.cricketscore.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ScoringSettings(
    val isWideEnabled: Boolean = true,
    val isNoBallEnabled: Boolean = true,
    val isFreeHitEnabled: Boolean = true,
    val isByeEnabled: Boolean = true,
    val isLegByeEnabled: Boolean = true,
    val themeMode: String = "DARK", // "DARK", "LIGHT", "SYSTEM"
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("b47_cricket_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<ScoringSettings> = _settings.asStateFlow()

    private fun loadSettings(): ScoringSettings {
        return ScoringSettings(
            isWideEnabled = prefs.getBoolean("wide_enabled", true),
            isNoBallEnabled = prefs.getBoolean("noball_enabled", true),
            isFreeHitEnabled = prefs.getBoolean("freehit_enabled", true),
            isByeEnabled = prefs.getBoolean("bye_enabled", true),
            isLegByeEnabled = prefs.getBoolean("legbye_enabled", true),
            themeMode = prefs.getString("theme_mode", "DARK") ?: "DARK",
            isSoundEnabled = prefs.getBoolean("sound_enabled", true),
            isVibrationEnabled = prefs.getBoolean("vibration_enabled", true)
        )
    }

    fun updateWideEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("wide_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isWideEnabled = enabled)
    }

    fun updateNoBallEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("noball_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isNoBallEnabled = enabled)
    }

    fun updateFreeHitEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("freehit_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isFreeHitEnabled = enabled)
    }

    fun updateByeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("bye_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isByeEnabled = enabled)
    }

    fun updateLegByeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("legbye_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isLegByeEnabled = enabled)
    }

    fun updateThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isSoundEnabled = enabled)
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
        _settings.value = _settings.value.copy(isVibrationEnabled = enabled)
    }
}
