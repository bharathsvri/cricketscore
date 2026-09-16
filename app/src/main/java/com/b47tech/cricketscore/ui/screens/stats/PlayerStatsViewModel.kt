package com.b47tech.cricketscore.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PlayerStatsViewModel(
    repository: CricketRepository
) : ViewModel() {

    val playerStats: StateFlow<List<PlayerCareerStatsEntity>> = repository.getAllPlayerCareerStatsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val repository: CricketRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerStatsViewModel(repository) as T
        }
    }
}
