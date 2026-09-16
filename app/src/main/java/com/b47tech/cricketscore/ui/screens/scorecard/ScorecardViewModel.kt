package com.b47tech.cricketscore.ui.screens.scorecard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.b47tech.cricketscore.core.engine.CricketEngine
import com.b47tech.cricketscore.core.engine.InningsScorecard
import com.b47tech.cricketscore.core.engine.MatchResult
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScorecardUiState(
    val isLoading: Boolean = true,
    val matchTitle: String = "",
    val resultSummary: String? = null,
    val innings1Scorecard: InningsScorecard? = null,
    val innings2Scorecard: InningsScorecard? = null,
    val hasSecondInnings: Boolean = false,
    val error: String? = null
)

class ScorecardViewModel(
    private val repository: CricketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScorecardUiState())
    val uiState: StateFlow<ScorecardUiState> = _uiState.asStateFlow()

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val entity = repository.getMatchById(matchId)
            if (entity == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Match not found")
                return@launch
            }

            val engine = repository.loadEngineFromMatchEntity(entity)
            val inn1 = engine.getInningsScorecard(1)
            val inn2 = if (engine.innings2Balls.isNotEmpty() || engine.currentInningsNumber == 2) {
                engine.getInningsScorecard(2)
            } else null

            val result = engine.getMatchResult()

            _uiState.value = ScorecardUiState(
                isLoading = false,
                matchTitle = "${engine.teamA.name} vs ${engine.teamB.name}",
                resultSummary = result?.resultSummary ?: entity.resultSummary,
                innings1Scorecard = inn1,
                innings2Scorecard = inn2,
                hasSecondInnings = (inn2 != null)
            )
        }
    }

    class Factory(private val repository: CricketRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScorecardViewModel(repository) as T
        }
    }
}
