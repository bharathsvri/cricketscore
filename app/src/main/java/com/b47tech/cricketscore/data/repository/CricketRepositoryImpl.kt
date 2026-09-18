package com.b47tech.cricketscore.data.repository

import androidx.room.withTransaction
import com.b47tech.cricketscore.core.engine.BallEvent
import com.b47tech.cricketscore.core.engine.CricketEngine
import com.b47tech.cricketscore.core.engine.MatchStatus
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.core.engine.Team
import com.b47tech.cricketscore.data.local.CricketDatabase
import com.b47tech.cricketscore.data.local.dao.MatchDao
import com.b47tech.cricketscore.data.local.dao.PlayerCareerStatsDao
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CricketRepositoryImpl(
    private val matchDao: MatchDao,
    private val playerCareerStatsDao: PlayerCareerStatsDao,
    private val database: CricketDatabase? = null
) : CricketRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun getAllMatchesFlow(): Flow<List<MatchEntity>> = matchDao.getAllMatchesFlow()

    override fun getActiveMatchFlow(): Flow<MatchEntity?> = matchDao.getActiveMatchFlow()

    override suspend fun getMatchById(id: String): MatchEntity? = withContext(Dispatchers.IO) {
        matchDao.getMatchById(id)
    }

    override fun getMatchFlowById(id: String): Flow<MatchEntity?> = matchDao.getMatchFlowById(id)

    override suspend fun saveMatchFromEngine(engine: CricketEngine) = withContext(Dispatchers.IO) {
        val result = engine.getMatchResult()
        val entity = MatchEntity(
            id = engine.matchId,
            teamAName = engine.teamA.name,
            teamBName = engine.teamB.name,
            teamAPlayersJson = json.encodeToString(engine.teamA.players),
            teamBPlayersJson = json.encodeToString(engine.teamB.players),
            overs = engine.totalOvers,
            playersPerTeam = engine.playersPerTeam,
            ballType = engine.ballType,
            tossWinnerId = engine.tossWinnerId,
            tossDecision = engine.tossDecision,
            battingFirstTeamId = engine.battingFirstTeam.id,
            bowlingFirstTeamId = engine.bowlingFirstTeam.id,
            currentInnings = engine.currentInningsNumber,
            status = engine.status.name,
            targetScore = engine.targetScore,
            winnerTeamName = result?.winnerTeamName,
            resultSummary = result?.resultSummary,
            innings1BallsJson = json.encodeToString(engine.innings1Balls.toList()),
            innings2BallsJson = json.encodeToString(engine.innings2Balls.toList()),
            strikerId = engine.strikerId,
            nonStrikerId = engine.nonStrikerId,
            currentBowlerId = engine.currentBowlerId,
            previousBowlerId = engine.previousBowlerId,
            isFreeHitNext = engine.isFreeHitNext,
            needsBowlerSelection = engine.needsBowlerSelection,
            needsBatsmanSelection = engine.needsBatsmanSelection,
            dismissedBatsmanWasStriker = engine.dismissedBatsmanWasStriker,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        matchDao.insertOrUpdateMatch(entity)

        if (engine.status == MatchStatus.COMPLETED) {
            updateCareerStats(engine)
        }
    }

    override suspend fun loadEngineFromMatchEntity(entity: MatchEntity): CricketEngine = withContext(Dispatchers.IO) {
        val playersA: List<Player> = json.decodeFromString(entity.teamAPlayersJson)
        val playersB: List<Player> = json.decodeFromString(entity.teamBPlayersJson)
        val teamA = Team(id = "teamA", name = entity.teamAName, players = playersA)
        val teamB = Team(id = "teamB", name = entity.teamBName, players = playersB)

        val engine = CricketEngine(
            matchId = entity.id,
            teamA = teamA,
            teamB = teamB,
            totalOvers = entity.overs,
            playersPerTeam = entity.playersPerTeam,
            ballType = entity.ballType,
            tossWinnerId = entity.tossWinnerId,
            tossDecision = entity.tossDecision
        )

        // Decode balls
        val balls1: List<BallEvent> = json.decodeFromString(entity.innings1BallsJson)
        val balls2: List<BallEvent> = json.decodeFromString(entity.innings2BallsJson)

        engine.innings1Balls.addAll(balls1)
        engine.innings2Balls.addAll(balls2)

        // Restore engine state without reflection (R8 and ProGuard safe)
        engine.restoreSavedState(
            status = MatchStatus.valueOf(entity.status),
            currentInningsNumber = entity.currentInnings,
            targetScore = entity.targetScore,
            strikerId = entity.strikerId,
            nonStrikerId = entity.nonStrikerId,
            currentBowlerId = entity.currentBowlerId,
            previousBowlerId = entity.previousBowlerId,
            isFreeHitNext = entity.isFreeHitNext,
            needsBowlerSelection = entity.needsBowlerSelection,
            needsBatsmanSelection = entity.needsBatsmanSelection,
            dismissedBatsmanWasStriker = entity.dismissedBatsmanWasStriker
        )

        engine
    }

    override suspend fun deleteMatch(id: String) = withContext(Dispatchers.IO) {
        matchDao.deleteMatchById(id)
    }

    override fun getAllPlayerCareerStatsFlow(): Flow<List<PlayerCareerStatsEntity>> =
        playerCareerStatsDao.getAllPlayerStatsByRunsFlow()

    private suspend fun updateCareerStats(engine: CricketEngine) {
        val scorecards = listOf(engine.getInningsScorecard(1), engine.getInningsScorecard(2))

        val allPlayers = (engine.teamA.players + engine.teamB.players).distinctBy { it.name }

        allPlayers.forEach { player ->
            val existing = playerCareerStatsDao.getPlayerByName(player.name) ?: PlayerCareerStatsEntity(playerName = player.name)

            var newInningsBatted = existing.inningsBatted
            var newRuns = existing.totalRuns
            var newHighest = existing.highestScore
            var newFours = existing.fours
            var newSixes = existing.sixes
            var newFifties = existing.fifties
            var newHundreds = existing.hundreds
            var newNotOuts = existing.notOuts
            var newBallsFaced = existing.ballsFaced

            var newLegalBallsBowled = existing.legalBallsBowled
            var newWickets = existing.wicketsTaken
            var newRunsConceded = existing.runsConceded
            var newMaidens = existing.maidens
            var newBestWickets = existing.bestBowlingWickets
            var newBestRuns = existing.bestBowlingRuns

            scorecards.forEach { sc ->
                // Check batting
                val bStat = sc.batters.find { it.playerName.equals(player.name, ignoreCase = true) }
                if (bStat != null && (bStat.ballsFaced > 0 || bStat.isOut)) {
                    newInningsBatted += 1
                    newRuns += bStat.runs
                    if (bStat.runs > newHighest) {
                        newHighest = bStat.runs
                    }
                    newFours += bStat.fours
                    newSixes += bStat.sixes
                    if (bStat.runs in 50..99) newFifties += 1
                    if (bStat.runs >= 100) newHundreds += 1
                    if (!bStat.isOut) newNotOuts += 1
                    newBallsFaced += bStat.ballsFaced
                }

                // Check bowling
                val bowlStat = sc.bowlers.find { it.playerName.equals(player.name, ignoreCase = true) }
                if (bowlStat != null && bowlStat.legalBallsBowled > 0) {
                    newLegalBallsBowled += bowlStat.legalBallsBowled
                    newWickets += bowlStat.wickets
                    newRunsConceded += bowlStat.runsConceded
                    newMaidens += bowlStat.maidens

                    if (bowlStat.wickets > newBestWickets ||
                        (bowlStat.wickets == newBestWickets && bowlStat.runsConceded < newBestRuns)
                    ) {
                        newBestWickets = bowlStat.wickets
                        newBestRuns = bowlStat.runsConceded
                    }
                }
            }

            val updated = existing.copy(
                matchesPlayed = existing.matchesPlayed + 1,
                inningsBatted = newInningsBatted,
                totalRuns = newRuns,
                highestScore = newHighest,
                fours = newFours,
                sixes = newSixes,
                fifties = newFifties,
                hundreds = newHundreds,
                notOuts = newNotOuts,
                ballsFaced = newBallsFaced,
                legalBallsBowled = newLegalBallsBowled,
                wicketsTaken = newWickets,
                runsConceded = newRunsConceded,
                maidens = newMaidens,
                bestBowlingWickets = newBestWickets,
                bestBowlingRuns = newBestRuns
            )
            playerCareerStatsDao.insertOrUpdate(updated)
        }
    }

    override suspend fun getAllMatchesList(): List<MatchEntity> = withContext(Dispatchers.IO) {
        matchDao.getAllMatchesList()
    }

    override suspend fun getAllPlayerStatsList(): List<PlayerCareerStatsEntity> = withContext(Dispatchers.IO) {
        playerCareerStatsDao.getAllPlayerStatsList()
    }

    override suspend fun restoreBackup(
        matches: List<MatchEntity>,
        stats: List<PlayerCareerStatsEntity>
    ) = withContext(Dispatchers.IO) {
        val db = database
        if (db != null) {
            db.withTransaction {
                if (matches.isNotEmpty()) {
                    matchDao.insertMatches(matches)
                }
                if (stats.isNotEmpty()) {
                    playerCareerStatsDao.insertAll(stats)
                }
            }
        } else {
            if (matches.isNotEmpty()) {
                matchDao.insertMatches(matches)
            }
            if (stats.isNotEmpty()) {
                playerCareerStatsDao.insertAll(stats)
            }
        }
    }
}
