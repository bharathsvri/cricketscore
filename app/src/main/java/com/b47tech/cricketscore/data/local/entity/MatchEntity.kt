package com.b47tech.cricketscore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: String,
    val teamAName: String,
    val teamBName: String,
    val teamAPlayersJson: String,
    val teamBPlayersJson: String,
    val overs: Int,
    val playersPerTeam: Int,
    val ballType: String,
    val tossWinnerId: String,
    val tossDecision: String,
    val battingFirstTeamId: String,
    val bowlingFirstTeamId: String,
    val currentInnings: Int,
    val status: String,
    val targetScore: Int?,
    val winnerTeamName: String?,
    val resultSummary: String?,
    val innings1BallsJson: String,
    val innings2BallsJson: String,
    val strikerId: String?,
    val nonStrikerId: String?,
    val currentBowlerId: String?,
    val previousBowlerId: String?,
    val isFreeHitNext: Boolean,
    val needsBowlerSelection: Boolean,
    val needsBatsmanSelection: Boolean,
    val dismissedBatsmanWasStriker: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
