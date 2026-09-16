package com.b47tech.cricketscore.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.b47tech.cricketscore.data.local.ScoringSettings
import com.b47tech.cricketscore.data.local.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<ScoringSettings> = repository.settings

    fun setWideEnabled(enabled: Boolean) = repository.updateWideEnabled(enabled)
    fun setNoBallEnabled(enabled: Boolean) = repository.updateNoBallEnabled(enabled)
    fun setFreeHitEnabled(enabled: Boolean) = repository.updateFreeHitEnabled(enabled)
    fun setByeEnabled(enabled: Boolean) = repository.updateByeEnabled(enabled)
    fun setLegByeEnabled(enabled: Boolean) = repository.updateLegByeEnabled(enabled)
    fun setThemeMode(mode: String) = repository.updateThemeMode(mode)
    fun setSoundEnabled(enabled: Boolean) = repository.updateSoundEnabled(enabled)
    fun setVibrationEnabled(enabled: Boolean) = repository.updateVibrationEnabled(enabled)

    class Factory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
