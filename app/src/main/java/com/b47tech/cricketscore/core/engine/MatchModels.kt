package com.b47tech.cricketscore.core.engine

import kotlinx.serialization.Serializable

enum class MatchStatus {
    NOT_STARTED,
    INNINGS_1,
    INNINGS_BREAK,
    INNINGS_2,
    COMPLETED,
    ABANDONED
}

enum class ExtraType {
    NONE,
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE
}

enum class WicketType {
    BOWLED,
    CAUGHT,
    RUN_OUT_STRIKER,
    RUN_OUT_NON_STRIKER,
    LBW,
    STUMPED,
    HIT_WICKET,
    RETIRED_HURT
}

@Serializable
data class Player(
    val id: String,
    val name: String,
    val teamId: String,
    val jerseyNumber: Int? = null,
    val isCaptain: Boolean = false,
    val isWicketKeeper: Boolean = false
)

@Serializable
data class Team(
    val id: String,
    val name: String,
    val players: List<Player> = emptyList()
)

@Serializable
data class BallEvent(
    val id: String,
    val matchId: String,
    val inningsNumber: Int,
    val overIndex: Int, // 0-based over index (e.g. 0 for 1st over)
    val ballInOver: Int, // 1-based legal ball index within over (1 to 6)
    val bowlerId: String,
    val bowlerName: String,
    val strikerId: String,
    val strikerName: String,
    val nonStrikerId: String,
    val nonStrikerName: String,
    val runsOffBat: Int = 0,
    val extraRuns: Int = 0,
    val extraType: ExtraType = ExtraType.NONE,
    val isLegalDelivery: Boolean = true,
    val isWicket: Boolean = false,
    val wicketType: WicketType? = null,
    val dismissedPlayerId: String? = null,
    val dismissedPlayerName: String? = null,
    val fielderId: String? = null,
    val fielderName: String? = null,
    val wasFreeHit: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val totalRunsOnBall: Int
        get() = runsOffBat + extraRuns

    fun getShortLabel(): String {
        return when {
            isWicket && wicketType == WicketType.RUN_OUT_STRIKER || wicketType == WicketType.RUN_OUT_NON_STRIKER -> {
                if (runsOffBat > 0) "RO+$runsOffBat" else "RO"
            }
            isWicket -> "W"
            extraType == ExtraType.WIDE -> {
                val totalWide = 1 + extraRuns
                if (totalWide > 1) "Wd+$totalWide" else "Wd"
            }
            extraType == ExtraType.NO_BALL -> {
                val totalRuns = 1 + runsOffBat + extraRuns
                if (totalRuns > 1) "Nb+$totalRuns" else "Nb"
            }
            extraType == ExtraType.BYE -> "${extraRuns}b"
            extraType == ExtraType.LEG_BYE -> "${extraRuns}lb"
            runsOffBat == 0 -> "•"
            runsOffBat == 4 -> "4"
            runsOffBat == 6 -> "6"
            else -> "$runsOffBat"
        }
    }
}

@Serializable
data class FallOfWicket(
    val wicketNumber: Int,
    val runs: Int,
    val dismissedPlayerName: String,
    val overString: String
)

@Serializable
data class BatterStats(
    val playerId: String,
    val playerName: String,
    val runs: Int = 0,
    val ballsFaced: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val isOut: Boolean = false,
    val dismissalText: String = "not out"
) {
    val strikeRate: Double
        get() = if (ballsFaced > 0) (runs.toDouble() * 100.0 / ballsFaced) else 0.0
}

@Serializable
data class BowlerStats(
    val playerId: String,
    val playerName: String,
    val legalBallsBowled: Int = 0,
    val maidens: Int = 0,
    val runsConceded: Int = 0,
    val wickets: Int = 0,
    val wides: Int = 0,
    val noBalls: Int = 0,
    val dotBalls: Int = 0
) {
    val oversString: String
        get() = "${legalBallsBowled / 6}.${legalBallsBowled % 6}"

    val economyRate: Double
        get() = if (legalBallsBowled > 0) (runsConceded.toDouble() * 6.0 / legalBallsBowled) else 0.0
}

@Serializable
data class ExtrasBreakdown(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0
) {
    val total: Int
        get() = wides + noBalls + byes + legByes
}

@Serializable
data class InningsScorecard(
    val inningsNumber: Int,
    val battingTeamId: String,
    val battingTeamName: String,
    val bowlingTeamId: String,
    val bowlingTeamName: String,
    val totalRuns: Int,
    val totalWickets: Int,
    val totalLegalBalls: Int,
    val batters: List<BatterStats>,
    val bowlers: List<BowlerStats>,
    val extras: ExtrasBreakdown,
    val fallOfWickets: List<FallOfWicket>,
    val isCompleted: Boolean = false
) {
    val oversString: String
        get() = "${totalLegalBalls / 6}.${totalLegalBalls % 6}"

    val runRate: Double
        get() = if (totalLegalBalls > 0) (totalRuns.toDouble() * 6.0 / totalLegalBalls) else 0.0
}

@Serializable
data class MatchResult(
    val winnerTeamId: String? = null,
    val winnerTeamName: String? = null,
    val resultSummary: String,
    val isTie: Boolean = false,
    val isDraw: Boolean = false
)
