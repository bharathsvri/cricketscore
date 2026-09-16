package com.b47tech.cricketscore.ui.screens.scoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.b47tech.cricketscore.core.engine.*
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScoringUiState(
    val matchId: String = "",
    val battingTeamName: String = "",
    val bowlingTeamName: String = "",
    val totalRuns: Int = 0,
    val totalWickets: Int = 0,
    val oversString: String = "0.0",
    val totalOvers: Int = 20,
    val crr: Double = 0.0,
    val rrr: Double = 0.0,
    val isSecondInnings: Boolean = false,
    val targetScore: Int? = null,
    val runsNeeded: Int = 0,
    val ballsRemaining: Int = 0,
    val isFreeHit: Boolean = false,
    val striker: BatterStats? = null,
    val nonStriker: BatterStats? = null,
    val currentBowler: BowlerStats? = null,
    val currentOverDeliveries: List<BallEvent> = emptyList(),
    val needsBowlerSelection: Boolean = false,
    val needsBatsmanSelection: Boolean = false,
    val status: MatchStatus = MatchStatus.INNINGS_1,
    val matchResult: MatchResult? = null
)

class ScoringViewModel(
    private val repository: CricketRepository
) : ViewModel() {

    var engine: CricketEngine? = null
        private set

    private val _uiState = MutableStateFlow(ScoringUiState())
    val uiState: StateFlow<ScoringUiState> = _uiState.asStateFlow()

    fun attachEngine(cricketEngine: CricketEngine) {
        this.engine = cricketEngine
        updateState()
    }

    fun loadMatchById(matchId: String) {
        viewModelScope.launch {
            val entity = repository.getMatchById(matchId) ?: return@launch
            val loadedEngine = repository.loadEngineFromMatchEntity(entity)
            this@ScoringViewModel.engine = loadedEngine
            updateState()
        }
    }

    private fun updateState() {
        val eng = engine ?: return
        val innNum = eng.currentInningsNumber
        val scorecard = eng.getInningsScorecard(innNum)

        val strikerStat = eng.strikerId?.let { sId ->
            scorecard.batters.find { it.playerId == sId } ?: BatterStats(playerId = sId, playerName = eng.currentBattingTeam().players.find { it.id == sId }?.name ?: "")
        }

        val nonStrikerStat = eng.nonStrikerId?.let { nsId ->
            scorecard.batters.find { it.playerId == nsId } ?: BatterStats(playerId = nsId, playerName = eng.currentBattingTeam().players.find { it.id == nsId }?.name ?: "")
        }

        val bowlerStat = eng.currentBowlerId?.let { bId ->
            scorecard.bowlers.find { it.playerId == bId } ?: BowlerStats(playerId = bId, playerName = eng.currentBowlingTeam().players.find { it.id == bId }?.name ?: "")
        }

        _uiState.value = ScoringUiState(
            matchId = eng.matchId,
            battingTeamName = eng.currentBattingTeam().name,
            bowlingTeamName = eng.currentBowlingTeam().name,
            totalRuns = eng.getTotalRuns(),
            totalWickets = eng.getTotalWickets(),
            oversString = eng.getOversString(),
            totalOvers = eng.totalOvers,
            crr = eng.getCurrentRunRate(),
            rrr = eng.getRequiredRunRate(),
            isSecondInnings = (innNum == 2),
            targetScore = eng.targetScore,
            runsNeeded = eng.getRunsNeeded(),
            ballsRemaining = eng.getBallsRemaining(),
            isFreeHit = eng.isFreeHitNext,
            striker = strikerStat,
            nonStriker = nonStrikerStat,
            currentBowler = bowlerStat,
            currentOverDeliveries = eng.getCurrentOverDeliveries(),
            needsBowlerSelection = eng.needsBowlerSelection,
            needsBatsmanSelection = eng.needsBatsmanSelection,
            status = eng.status,
            matchResult = eng.getMatchResult()
        )

        // Persist to Room database immediately
        viewModelScope.launch {
            repository.saveMatchFromEngine(eng)
        }
    }

    fun recordRuns(runs: Int) {
        val eng = engine ?: return
        if (eng.recordRuns(runs)) {
            updateState()
        }
    }

    fun recordWide(extraRuns: Int) {
        val eng = engine ?: return
        if (eng.recordWide(extraRuns)) {
            updateState()
        }
    }

    fun recordNoBall(runsOffBat: Int, extraRuns: Int) {
        val eng = engine ?: return
        if (eng.recordNoBall(runsOffBat, extraRuns)) {
            updateState()
        }
    }

    fun recordBye(runs: Int, isLegBye: Boolean) {
        val eng = engine ?: return
        if (eng.recordBye(runs, isLegBye)) {
            updateState()
        }
    }

    fun recordWicket(
        wicketType: WicketType,
        dismissedPlayerId: String,
        fielderId: String?,
        runsCompleted: Int,
        nextBatsmanId: String?
    ) {
        val eng = engine ?: return
        val recorded = eng.recordWicket(wicketType, dismissedPlayerId, fielderId, runsCompleted)
        if (recorded) {
            if (nextBatsmanId != null && eng.needsBatsmanSelection) {
                eng.selectNewBatsman(nextBatsmanId)
            }
            updateState()
        }
    }

    fun selectNewBatsman(playerId: String) {
        val eng = engine ?: return
        if (eng.selectNewBatsman(playerId)) {
            updateState()
        }
    }

    fun selectBowler(bowlerId: String) {
        val eng = engine ?: return
        if (eng.selectBowler(bowlerId)) {
            updateState()
        }
    }

    fun swapStrike() {
        val eng = engine ?: return
        if (eng.swapStrikeManually()) {
            updateState()
        }
    }

    fun undo() {
        val eng = engine ?: return
        if (eng.undoLastBall()) {
            updateState()
        }
    }

    fun startSecondInnings(strikerId: String, nonStrikerId: String, bowlerId: String) {
        val eng = engine ?: return
        eng.startSecondInnings(strikerId, nonStrikerId, bowlerId)
        updateState()
    }

    class Factory(private val repository: CricketRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScoringViewModel(repository) as T
        }
    }
}
