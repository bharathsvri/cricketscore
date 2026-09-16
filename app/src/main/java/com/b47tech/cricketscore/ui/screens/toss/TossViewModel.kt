package com.b47tech.cricketscore.ui.screens.toss

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TossUiState(
    val tossWinnerId: String = "teamA",
    val tossDecision: String = "BAT" // "BAT" or "BOWL"
)

class TossViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TossUiState())
    val uiState: StateFlow<TossUiState> = _uiState.asStateFlow()

    fun updateTossWinner(winnerId: String) {
        _uiState.value = _uiState.value.copy(tossWinnerId = winnerId)
    }

    fun updateTossDecision(decision: String) {
        _uiState.value = _uiState.value.copy(tossDecision = decision)
    }
}
