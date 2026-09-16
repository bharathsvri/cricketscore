package com.b47tech.cricketscore.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.b47tech.cricketscore.core.export.BackupManager
import com.b47tech.cricketscore.data.local.ScoringSettings
import com.b47tech.cricketscore.data.local.SettingsRepository
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val cricketRepository: CricketRepository
) : ViewModel() {

    val settings: StateFlow<ScoringSettings> = settingsRepository.settings

    fun setWideEnabled(enabled: Boolean) = settingsRepository.updateWideEnabled(enabled)
    fun setNoBallEnabled(enabled: Boolean) = settingsRepository.updateNoBallEnabled(enabled)
    fun setFreeHitEnabled(enabled: Boolean) = settingsRepository.updateFreeHitEnabled(enabled)
    fun setByeEnabled(enabled: Boolean) = settingsRepository.updateByeEnabled(enabled)
    fun setLegByeEnabled(enabled: Boolean) = settingsRepository.updateLegByeEnabled(enabled)
    fun setThemeMode(mode: String) = settingsRepository.updateThemeMode(mode)
    fun setSoundEnabled(enabled: Boolean) = settingsRepository.updateSoundEnabled(enabled)
    fun setVibrationEnabled(enabled: Boolean) = settingsRepository.updateVibrationEnabled(enabled)

    fun exportBackup(context: Context, onFileReady: (File) -> Unit) {
        viewModelScope.launch {
            val file = BackupManager.createBackupFile(context, cricketRepository)
            if (file != null) {
                onFileReady(file)
            }
        }
    }

    fun restoreBackup(jsonContent: String, onComplete: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            val result = BackupManager.restoreBackup(jsonContent, cricketRepository)
            onComplete(result)
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val cricketRepository: CricketRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, cricketRepository) as T
        }
    }
}
