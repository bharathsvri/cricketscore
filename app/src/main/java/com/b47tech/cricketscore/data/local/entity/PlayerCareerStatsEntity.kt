package com.b47tech.cricketscore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "player_career_stats")
data class PlayerCareerStatsEntity(
    @PrimaryKey
    val playerName: String,
    val matchesPlayed: Int = 0,
    val inningsBatted: Int = 0,
    val totalRuns: Int = 0,
    val highestScore: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val fifties: Int = 0,
    val hundreds: Int = 0,
    val notOuts: Int = 0,
    val ballsFaced: Int = 0,
    val legalBallsBowled: Int = 0,
    val wicketsTaken: Int = 0,
    val runsConceded: Int = 0,
    val maidens: Int = 0,
    val bestBowlingWickets: Int = 0,
    val bestBowlingRuns: Int = 0
) {
    val battingAverage: Double
        get() {
            val dismissals = inningsBatted - notOuts
            return if (dismissals > 0) totalRuns.toDouble() / dismissals else totalRuns.toDouble()
        }

    val battingStrikeRate: Double
        get() = if (ballsFaced > 0) (totalRuns.toDouble() * 100.0 / ballsFaced) else 0.0

    val bowlingEconomyRate: Double
        get() = if (legalBallsBowled > 0) (runsConceded.toDouble() * 6.0 / legalBallsBowled) else 0.0

    val bowlingAverage: Double
        get() = if (wicketsTaken > 0) (runsConceded.toDouble() / wicketsTaken) else 0.0

    val oversBowledString: String
        get() = "${legalBallsBowled / 6}.${legalBallsBowled % 6}"
}
