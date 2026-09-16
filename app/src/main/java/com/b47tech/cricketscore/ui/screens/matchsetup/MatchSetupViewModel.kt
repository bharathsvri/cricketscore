package com.b47tech.cricketscore.ui.screens.matchsetup

import androidx.lifecycle.ViewModel
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.core.engine.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class PlayerInput(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val jerseyNumber: String = "",
    val isCaptain: Boolean = false,
    val isWicketKeeper: Boolean = false
)

data class MatchSetupUiState(
    val matchName: String = "",
    val teamAName: String = "Team 1",
    val teamBName: String = "Team 2",
    val overs: Int = 20,
    val isCustomOvers: Boolean = false,
    val customOversText: String = "",
    val playersCount: Int = 11,
    val ballType: String = "Leather",
    val teamAPlayers: List<PlayerInput> = (1..11).map { PlayerInput(name = "Player A$it", jerseyNumber = "$it") },
    val teamBPlayers: List<PlayerInput> = (1..11).map { PlayerInput(name = "Player B$it", jerseyNumber = "$it") },
    val validationError: String? = null
)

class MatchSetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MatchSetupUiState())
    val uiState: StateFlow<MatchSetupUiState> = _uiState.asStateFlow()

    fun updateMatchName(name: String) {
        _uiState.value = _uiState.value.copy(matchName = name)
    }

    fun updateTeamAName(name: String) {
        _uiState.value = _uiState.value.copy(teamAName = name)
    }

    fun updateTeamBName(name: String) {
        _uiState.value = _uiState.value.copy(teamBName = name)
    }

    fun updateOvers(overs: Int) {
        _uiState.value = _uiState.value.copy(overs = overs.coerceIn(1, 100), isCustomOvers = false)
    }

    fun setCustomOvers(text: String) {
        val parsed = text.toIntOrNull()
        _uiState.value = _uiState.value.copy(
            isCustomOvers = true,
            customOversText = text,
            overs = if (parsed != null && parsed > 0) parsed else _uiState.value.overs
        )
    }

    fun updatePlayersCount(count: Int) {
        val newCount = count.coerceIn(2, 15)
        val curA = _uiState.value.teamAPlayers.toMutableList()
        val curB = _uiState.value.teamBPlayers.toMutableList()

        while (curA.size < newCount) {
            val num = curA.size + 1
            curA.add(PlayerInput(name = "Player A$num", jerseyNumber = "$num"))
        }
        while (curA.size > newCount) {
            curA.removeAt(curA.size - 1)
        }

        while (curB.size < newCount) {
            val num = curB.size + 1
            curB.add(PlayerInput(name = "Player B$num", jerseyNumber = "$num"))
        }
        while (curB.size > newCount) {
            curB.removeAt(curB.size - 1)
        }

        _uiState.value = _uiState.value.copy(
            playersCount = newCount,
            teamAPlayers = curA,
            teamBPlayers = curB
        )
    }

    fun updateBallType(type: String) {
        _uiState.value = _uiState.value.copy(ballType = type)
    }

    fun updatePlayerA(index: Int, player: PlayerInput) {
        val list = _uiState.value.teamAPlayers.toMutableList()
        if (index in list.indices) {
            list[index] = player
            _uiState.value = _uiState.value.copy(teamAPlayers = list)
        }
    }

    fun updatePlayerB(index: Int, player: PlayerInput) {
        val list = _uiState.value.teamBPlayers.toMutableList()
        if (index in list.indices) {
            list[index] = player
            _uiState.value = _uiState.value.copy(teamBPlayers = list)
        }
    }

    fun addPlayerToTeamA() {
        val list = _uiState.value.teamAPlayers.toMutableList()
        val num = list.size + 1
        list.add(PlayerInput(name = "Player A$num", jerseyNumber = "$num"))
        _uiState.value = _uiState.value.copy(teamAPlayers = list, playersCount = list.size)
    }

    fun addPlayerToTeamB() {
        val list = _uiState.value.teamBPlayers.toMutableList()
        val num = list.size + 1
        list.add(PlayerInput(name = "Player B$num", jerseyNumber = "$num"))
        _uiState.value = _uiState.value.copy(teamBPlayers = list, playersCount = list.size)
    }

    fun removePlayerFromTeamA(index: Int) {
        val list = _uiState.value.teamAPlayers.toMutableList()
        if (list.size > 2 && index in list.indices) {
            list.removeAt(index)
            _uiState.value = _uiState.value.copy(teamAPlayers = list, playersCount = list.size)
        }
    }

    fun removePlayerFromTeamB(index: Int) {
        val list = _uiState.value.teamBPlayers.toMutableList()
        if (list.size > 2 && index in list.indices) {
            list.removeAt(index)
            _uiState.value = _uiState.value.copy(teamBPlayers = list, playersCount = list.size)
        }
    }

    fun validate(): Boolean {
        val s = _uiState.value
        if (s.teamAName.trim().isEmpty()) {
            _uiState.value = s.copy(validationError = "Please enter Team 1 name.")
            return false
        }
        if (s.teamBName.trim().isEmpty()) {
            _uiState.value = s.copy(validationError = "Please enter Team 2 name.")
            return false
        }
        if (s.overs <= 0) {
            _uiState.value = s.copy(validationError = "Overs must be greater than 0.")
            return false
        }

        // Duplicate checks
        val namesA = s.teamAPlayers.map { it.name.trim().lowercase() }
        if (namesA.size != namesA.distinct().size) {
            _uiState.value = s.copy(validationError = "Duplicate player names found in ${s.teamAName}.")
            return false
        }
        val namesB = s.teamBPlayers.map { it.name.trim().lowercase() }
        if (namesB.size != namesB.distinct().size) {
            _uiState.value = s.copy(validationError = "Duplicate player names found in ${s.teamBName}.")
            return false
        }

        _uiState.value = s.copy(validationError = null)
        return true
    }

    fun buildTeams(): Pair<Team, Team> {
        val s = _uiState.value
        val playersA = s.teamAPlayers.map { p ->
            Player(
                id = p.id,
                name = p.name.trim().ifEmpty { "Player" },
                teamId = "teamA",
                jerseyNumber = p.jerseyNumber.toIntOrNull(),
                isCaptain = p.isCaptain,
                isWicketKeeper = p.isWicketKeeper
            )
        }
        val playersB = s.teamBPlayers.map { p ->
            Player(
                id = p.id,
                name = p.name.trim().ifEmpty { "Player" },
                teamId = "teamB",
                jerseyNumber = p.jerseyNumber.toIntOrNull(),
                isCaptain = p.isCaptain,
                isWicketKeeper = p.isWicketKeeper
            )
        }
        val teamA = Team(id = "teamA", name = s.teamAName.trim().ifEmpty { "Team 1" }, players = playersA)
        val teamB = Team(id = "teamB", name = s.teamBName.trim().ifEmpty { "Team 2" }, players = playersB)
        return Pair(teamA, teamB)
    }
}
