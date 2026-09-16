package com.b47tech.cricketscore.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CricketRepository
) : ViewModel() {

    val activeMatch: StateFlow<MatchEntity?> = repository.getActiveMatchFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentMatches: StateFlow<List<MatchEntity>> = repository.getAllMatchesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteMatch(matchId: String) {
        viewModelScope.launch {
            repository.deleteMatch(matchId)
        }
    }

    class Factory(private val repository: CricketRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
