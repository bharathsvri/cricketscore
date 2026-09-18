package com.b47tech.cricketscore.core.engine

import java.util.UUID

data class EngineSnapshot(
    val currentInnings: Int,
    val strikerId: String?,
    val nonStrikerId: String?,
    val currentBowlerId: String?,
    val previousBowlerId: String?,
    val isFreeHitNext: Boolean,
    val needsBowlerSelection: Boolean,
    val needsBatsmanSelection: Boolean,
    val dismissedBatsmanWasStriker: Boolean = true
)

class CricketEngine(
    val matchId: String = UUID.randomUUID().toString(),
    val teamA: Team,
    val teamB: Team,
    val totalOvers: Int,
    val playersPerTeam: Int,
    val ballType: String = "Leather",
    val tossWinnerId: String,
    val tossDecision: String // "BAT" or "BOWL"
) {
    val battingFirstTeam: Team
    val bowlingFirstTeam: Team

    init {
        val tossWinnerIsTeamA = tossWinnerId == teamA.id
        if (tossDecision.equals("BAT", ignoreCase = true)) {
            battingFirstTeam = if (tossWinnerIsTeamA) teamA else teamB
            bowlingFirstTeam = if (tossWinnerIsTeamA) teamB else teamA
        } else {
            battingFirstTeam = if (tossWinnerIsTeamA) teamB else teamA
            bowlingFirstTeam = if (tossWinnerIsTeamA) teamA else teamB
        }
    }

    var status: MatchStatus = MatchStatus.NOT_STARTED
        private set

    var currentInningsNumber: Int = 1
        private set

    val innings1Balls = mutableListOf<BallEvent>()
    val innings2Balls = mutableListOf<BallEvent>()

    // State history stack for undoing
    private val stateSnapshots1 = mutableListOf<EngineSnapshot>()
    private val stateSnapshots2 = mutableListOf<EngineSnapshot>()

    var strikerId: String? = null
        private set
    var nonStrikerId: String? = null
        private set
    var currentBowlerId: String? = null
        private set
    var previousBowlerId: String? = null
        private set

    var isFreeHitNext: Boolean = false
        private set
    var needsBowlerSelection: Boolean = false
        private set
    var needsBatsmanSelection: Boolean = false
        private set
    var dismissedBatsmanWasStriker: Boolean = true
        private set

    var targetScore: Int? = null
        private set

    val maxWickets: Int
        get() = (playersPerTeam - 1).coerceAtLeast(1)

    fun startFirstInnings(openingStrikerId: String, openingNonStrikerId: String, openingBowlerId: String) {
        currentInningsNumber = 1
        status = MatchStatus.INNINGS_1
        strikerId = openingStrikerId
        nonStrikerId = openingNonStrikerId
        currentBowlerId = openingBowlerId
        previousBowlerId = null
        isFreeHitNext = false
        needsBowlerSelection = false
        needsBatsmanSelection = false
        innings1Balls.clear()
        stateSnapshots1.clear()
    }

    fun startSecondInnings(openingStrikerId: String, openingNonStrikerId: String, openingBowlerId: String) {
        val inn1Score = getInningsScorecard(1)
        targetScore = inn1Score.totalRuns + 1
        currentInningsNumber = 2
        status = MatchStatus.INNINGS_2
        strikerId = openingStrikerId
        nonStrikerId = openingNonStrikerId
        currentBowlerId = openingBowlerId
        previousBowlerId = null
        isFreeHitNext = false
        needsBowlerSelection = false
        needsBatsmanSelection = false
        innings2Balls.clear()
        stateSnapshots2.clear()
    }

    fun restoreSavedState(
        status: MatchStatus,
        currentInningsNumber: Int,
        targetScore: Int?,
        strikerId: String?,
        nonStrikerId: String?,
        currentBowlerId: String?,
        previousBowlerId: String?,
        isFreeHitNext: Boolean,
        needsBowlerSelection: Boolean,
        needsBatsmanSelection: Boolean,
        dismissedBatsmanWasStriker: Boolean
    ) {
        this.status = status
        this.currentInningsNumber = currentInningsNumber
        this.targetScore = targetScore
        this.strikerId = strikerId
        this.nonStrikerId = nonStrikerId
        this.currentBowlerId = currentBowlerId
        this.previousBowlerId = previousBowlerId
        this.isFreeHitNext = isFreeHitNext
        this.needsBowlerSelection = needsBowlerSelection
        this.needsBatsmanSelection = needsBatsmanSelection
        this.dismissedBatsmanWasStriker = dismissedBatsmanWasStriker
    }

    fun currentBattingTeam(): Team = if (currentInningsNumber == 1) battingFirstTeam else bowlingFirstTeam
    fun currentBowlingTeam(): Team = if (currentInningsNumber == 1) bowlingFirstTeam else battingFirstTeam

    private fun currentBallsList(): MutableList<BallEvent> =
        if (currentInningsNumber == 1) innings1Balls else innings2Balls

    private fun currentSnapshotsList(): MutableList<EngineSnapshot> =
        if (currentInningsNumber == 1) stateSnapshots1 else stateSnapshots2

    private fun takeSnapshot() {
        currentSnapshotsList().add(
            EngineSnapshot(
                currentInnings = currentInningsNumber,
                strikerId = strikerId,
                nonStrikerId = nonStrikerId,
                currentBowlerId = currentBowlerId,
                previousBowlerId = previousBowlerId,
                isFreeHitNext = isFreeHitNext,
                needsBowlerSelection = needsBowlerSelection,
                needsBatsmanSelection = needsBatsmanSelection,
                dismissedBatsmanWasStriker = dismissedBatsmanWasStriker
            )
        )
    }

    private fun getPlayerName(team: Team, playerId: String?): String {
        return team.players.find { it.id == playerId }?.name ?: "Unknown"
    }

    fun getLegalBallsBowled(inningsNum: Int = currentInningsNumber): Int {
        val list = if (inningsNum == 1) innings1Balls else innings2Balls
        return list.count { it.isLegalDelivery }
    }

    fun getTotalRuns(inningsNum: Int = currentInningsNumber): Int {
        val list = if (inningsNum == 1) innings1Balls else innings2Balls
        return list.sumOf { it.totalRunsOnBall }
    }

    fun getTotalWickets(inningsNum: Int = currentInningsNumber): Int {
        val list = if (inningsNum == 1) innings1Balls else innings2Balls
        return list.count { it.isWicket && it.wicketType != WicketType.RETIRED_HURT }
    }

    fun getCompletedOvers(inningsNum: Int = currentInningsNumber): Int {
        return getLegalBallsBowled(inningsNum) / 6
    }

    fun getCurrentOverBalls(inningsNum: Int = currentInningsNumber): Int {
        return getLegalBallsBowled(inningsNum) % 6
    }

    fun getOversString(inningsNum: Int = currentInningsNumber): String {
        return "${getCompletedOvers(inningsNum)}.${getCurrentOverBalls(inningsNum)}"
    }

    fun getCurrentRunRate(inningsNum: Int = currentInningsNumber): Double {
        val balls = getLegalBallsBowled(inningsNum)
        return if (balls > 0) (getTotalRuns(inningsNum).toDouble() * 6.0 / balls) else 0.0
    }

    fun getRequiredRunRate(): Double {
        if (currentInningsNumber != 2 || targetScore == null) return 0.0
        val runsNeeded = (targetScore!! - getTotalRuns(2)).coerceAtLeast(0)
        val ballsRemaining = (totalOvers * 6) - getLegalBallsBowled(2)
        return if (ballsRemaining > 0) (runsNeeded.toDouble() * 6.0 / ballsRemaining) else 0.0
    }

    fun getRunsNeeded(): Int {
        if (currentInningsNumber != 2 || targetScore == null) return 0
        return (targetScore!! - getTotalRuns(2)).coerceAtLeast(0)
    }

    fun getBallsRemaining(): Int {
        val balls = (totalOvers * 6) - getLegalBallsBowled(currentInningsNumber)
        return balls.coerceAtLeast(0)
    }

    fun getCurrentOverDeliveries(): List<BallEvent> {
        val balls = currentBallsList()
        val currentLegalBalls = getLegalBallsBowled()
        val overIndex = currentLegalBalls / 6
        // Return all balls that belong to the current over index
        return balls.filter { it.overIndex == overIndex }
    }

    // --- SCORING ACTIONS ---

    fun recordDotBall(): Boolean = recordRuns(0)

    fun recordRuns(runs: Int): Boolean {
        if (isMatchOver() || isInningsBreak()) return false
        if (needsBowlerSelection || needsBatsmanSelection) return false
        val sId = strikerId ?: return false
        val nsId = nonStrikerId ?: return false
        val bId = currentBowlerId ?: return false

        takeSnapshot()

        val legalBalls = getLegalBallsBowled()
        val overIdx = legalBalls / 6
        val ballInOver = (legalBalls % 6) + 1
        val wasFH = isFreeHitNext

        val event = BallEvent(
            id = UUID.randomUUID().toString(),
            matchId = matchId,
            inningsNumber = currentInningsNumber,
            overIndex = overIdx,
            ballInOver = ballInOver,
            bowlerId = bId,
            bowlerName = getPlayerName(currentBowlingTeam(), bId),
            strikerId = sId,
            strikerName = getPlayerName(currentBattingTeam(), sId),
            nonStrikerId = nsId,
            nonStrikerName = getPlayerName(currentBattingTeam(), nsId),
            runsOffBat = runs,
            extraRuns = 0,
            extraType = ExtraType.NONE,
            isLegalDelivery = true,
            isWicket = false,
            wasFreeHit = wasFH
        )
        currentBallsList().add(event)

        // Free hit consumed on legal delivery
        isFreeHitNext = false

        // Rotate strike on odd runs
        if (runs % 2 != 0) {
            rotateStrike()
        }

        handleDeliveryEnd(ballInOver == 6)
        return true
    }

    fun recordBoundary(isFour: Boolean): Boolean {
        return recordRuns(if (isFour) 4 else 6)
    }

    fun recordWide(extraRuns: Int = 0): Boolean {
        if (isMatchOver() || isInningsBreak()) return false
        if (needsBowlerSelection || needsBatsmanSelection) return false
        val sId = strikerId ?: return false
        val nsId = nonStrikerId ?: return false
        val bId = currentBowlerId ?: return false

        takeSnapshot()

        val legalBalls = getLegalBallsBowled()
        val overIdx = legalBalls / 6
        val ballInOver = (legalBalls % 6)

        val totalExtra = 1 + extraRuns // 1 for wide penalty + additional runs
        val wasFH = isFreeHitNext

        val event = BallEvent(
            id = UUID.randomUUID().toString(),
            matchId = matchId,
            inningsNumber = currentInningsNumber,
            overIndex = overIdx,
            ballInOver = ballInOver,
            bowlerId = bId,
            bowlerName = getPlayerName(currentBowlingTeam(), bId),
            strikerId = sId,
            strikerName = getPlayerName(currentBattingTeam(), sId),
            nonStrikerId = nsId,
            nonStrikerName = getPlayerName(currentBattingTeam(), nsId),
            runsOffBat = 0,
            extraRuns = totalExtra,
            extraType = ExtraType.WIDE,
            isLegalDelivery = false,
            isWicket = false,
            wasFreeHit = wasFH
        )
        currentBallsList().add(event)

        // Strike rotates only if odd extra runs were completed by running
        if (extraRuns % 2 != 0) {
            rotateStrike()
        }

        // On Wide, Free Hit status remains active if it was already active
        checkMatchOrInningsStatus()
        return true
    }

    fun recordNoBall(runsOffBat: Int = 0, extraRuns: Int = 0): Boolean {
        if (isMatchOver() || isInningsBreak()) return false
        if (needsBowlerSelection || needsBatsmanSelection) return false
        val sId = strikerId ?: return false
        val nsId = nonStrikerId ?: return false
        val bId = currentBowlerId ?: return false

        takeSnapshot()

        val legalBalls = getLegalBallsBowled()
        val overIdx = legalBalls / 6
        val ballInOver = (legalBalls % 6)
        val wasFH = isFreeHitNext

        val totalExtra = 1 + extraRuns // 1 for no-ball penalty + extra byes/leg-byes
        val event = BallEvent(
            id = UUID.randomUUID().toString(),
            matchId = matchId,
            inningsNumber = currentInningsNumber,
            overIndex = overIdx,
            ballInOver = ballInOver,
            bowlerId = bId,
            bowlerName = getPlayerName(currentBowlingTeam(), bId),
            strikerId = sId,
            strikerName = getPlayerName(currentBattingTeam(), sId),
            nonStrikerId = nsId,
            nonStrikerName = getPlayerName(currentBattingTeam(), nsId),
            runsOffBat = runsOffBat,
            extraRuns = totalExtra,
            extraType = ExtraType.NO_BALL,
            isLegalDelivery = false,
            isWicket = false,
            wasFreeHit = wasFH
        )
        currentBallsList().add(event)

        // No ball gives a Free Hit on the next delivery!
        isFreeHitNext = true

        if ((runsOffBat + extraRuns) % 2 != 0) {
            rotateStrike()
        }

        checkMatchOrInningsStatus()
        return true
    }

    fun recordBye(runs: Int, isLegBye: Boolean = false): Boolean {
        if (isMatchOver() || isInningsBreak() || runs <= 0) return false
        if (needsBowlerSelection || needsBatsmanSelection) return false
        val sId = strikerId ?: return false
        val nsId = nonStrikerId ?: return false
        val bId = currentBowlerId ?: return false

        takeSnapshot()

        val legalBalls = getLegalBallsBowled()
        val overIdx = legalBalls / 6
        val ballInOver = (legalBalls % 6) + 1
        val wasFH = isFreeHitNext

        val event = BallEvent(
            id = UUID.randomUUID().toString(),
            matchId = matchId,
            inningsNumber = currentInningsNumber,
            overIndex = overIdx,
            ballInOver = ballInOver,
            bowlerId = bId,
            bowlerName = getPlayerName(currentBowlingTeam(), bId),
            strikerId = sId,
            strikerName = getPlayerName(currentBattingTeam(), sId),
            nonStrikerId = nsId,
            nonStrikerName = getPlayerName(currentBattingTeam(), nsId),
            runsOffBat = 0,
            extraRuns = runs,
            extraType = if (isLegBye) ExtraType.LEG_BYE else ExtraType.BYE,
            isLegalDelivery = true,
            isWicket = false,
            wasFreeHit = wasFH
        )
        currentBallsList().add(event)

        isFreeHitNext = false

        if (runs % 2 != 0) {
            rotateStrike()
        }

        handleDeliveryEnd(ballInOver == 6)
        return true
    }

    fun recordWicket(
        wicketType: WicketType,
        dismissedPlayerId: String,
        fielderId: String? = null,
        runsCompleted: Int = 0
    ): Boolean {
        if (isMatchOver() || isInningsBreak()) return false
        if (needsBowlerSelection || needsBatsmanSelection) return false
        val sId = strikerId ?: return false
        val nsId = nonStrikerId ?: return false
        val bId = currentBowlerId ?: return false

        // Free Hit Rule (ICC Clause 21.19 / MCC Law 21):
        // Striker cannot be dismissed except for methods permitted on a No Ball:
        // Run Out or Batter Retiring Hurt.
        if (isFreeHitNext && (wicketType != WicketType.RUN_OUT_STRIKER &&
                wicketType != WicketType.RUN_OUT_NON_STRIKER &&
                wicketType != WicketType.RETIRED_HURT)
        ) {
            return false
        }

        takeSnapshot()

        val legalBalls = getLegalBallsBowled()
        val overIdx = legalBalls / 6
        val ballInOver = (legalBalls % 6) + 1
        val wasFH = isFreeHitNext

        val dismissedName = getPlayerName(currentBattingTeam(), dismissedPlayerId)
        val fielderName = fielderId?.let { getPlayerName(currentBowlingTeam(), it) }

        val event = BallEvent(
            id = UUID.randomUUID().toString(),
            matchId = matchId,
            inningsNumber = currentInningsNumber,
            overIndex = overIdx,
            ballInOver = ballInOver,
            bowlerId = bId,
            bowlerName = getPlayerName(currentBowlingTeam(), bId),
            strikerId = sId,
            strikerName = getPlayerName(currentBattingTeam(), sId),
            nonStrikerId = nsId,
            nonStrikerName = getPlayerName(currentBattingTeam(), nsId),
            runsOffBat = runsCompleted,
            extraRuns = 0,
            extraType = ExtraType.NONE,
            isLegalDelivery = true,
            isWicket = true,
            wicketType = wicketType,
            dismissedPlayerId = dismissedPlayerId,
            dismissedPlayerName = dismissedName,
            fielderId = fielderId,
            fielderName = fielderName,
            wasFreeHit = wasFH
        )
        currentBallsList().add(event)

        isFreeHitNext = false

        val isStrikerDismissed = (dismissedPlayerId == sId)
        dismissedBatsmanWasStriker = isStrikerDismissed

        if (runsCompleted % 2 != 0) {
            rotateStrike()
        }

        if (isStrikerDismissed) {
            strikerId = null
        } else {
            nonStrikerId = null
        }

        // Check if all out (max wickets reached OR no partner remaining to bat)
        val totalWicketsNow = getTotalWickets()
        val battingTeam = currentBattingTeam()
        val alreadyBattedIds = getInningsScorecard(currentInningsNumber).batters
            .filter { it.isOut || it.playerId == strikerId || it.playerId == nonStrikerId }
            .map { it.playerId }
        val remainingBatters = battingTeam.players.count { !alreadyBattedIds.contains(it.id) }
        val battersAtCrease = (if (strikerId != null) 1 else 0) + (if (nonStrikerId != null) 1 else 0)
        val isAllOut = totalWicketsNow >= maxWickets || (battersAtCrease < 2 && remainingBatters == 0)

        if (!isAllOut) {
            needsBatsmanSelection = true
        } else {
            needsBatsmanSelection = false
        }

        handleDeliveryEnd(ballInOver == 6)
        return true
    }

    private fun handleDeliveryEnd(isOverComplete: Boolean) {
        checkMatchOrInningsStatus()

        if (status == MatchStatus.INNINGS_1 || status == MatchStatus.INNINGS_2) {
            if (isOverComplete) {
                // Over ended
                rotateStrike()
                previousBowlerId = currentBowlerId
                currentBowlerId = null
                needsBowlerSelection = true
            }
        }
    }

    private fun checkMatchOrInningsStatus() {
        val currentWickets = getTotalWickets()
        val legalBalls = getLegalBallsBowled()
        val totalBallsInInnings = totalOvers * 6
        val isOversFinished = legalBalls >= totalBallsInInnings

        val battingTeam = currentBattingTeam()
        val alreadyBattedIds = getInningsScorecard(currentInningsNumber).batters
            .filter { it.isOut || it.playerId == strikerId || it.playerId == nonStrikerId }
            .map { it.playerId }
        val remainingBatters = battingTeam.players.count { !alreadyBattedIds.contains(it.id) }
        val battersAtCrease = (if (strikerId != null) 1 else 0) + (if (nonStrikerId != null) 1 else 0)
        val isAllOut = currentWickets >= maxWickets || (battersAtCrease < 2 && remainingBatters == 0)

        if (currentInningsNumber == 1) {
            if (isOversFinished || isAllOut) {
                status = MatchStatus.INNINGS_BREAK
                needsBowlerSelection = false
                needsBatsmanSelection = false
            }
        } else if (currentInningsNumber == 2) {
            val runs2 = getTotalRuns(2)
            val target = targetScore ?: return

            if (runs2 >= target) {
                // Chasing team won!
                status = MatchStatus.COMPLETED
                needsBowlerSelection = false
                needsBatsmanSelection = false
            } else if (isOversFinished || isAllOut) {
                // Defending team won or tied
                status = MatchStatus.COMPLETED
                needsBowlerSelection = false
                needsBatsmanSelection = false
            }
        }
    }

    fun selectNewBatsman(playerId: String): Boolean {
        if (!needsBatsmanSelection) return false
        val battingTeam = currentBattingTeam()
        if (battingTeam.players.none { it.id == playerId }) return false

        // Check player has not already batted or is not currently on field
        val alreadyBattedIds = getInningsScorecard(currentInningsNumber).batters
            .filter { it.isOut || it.playerId == strikerId || it.playerId == nonStrikerId }
            .map { it.playerId }
        if (alreadyBattedIds.contains(playerId)) return false

        if (dismissedBatsmanWasStriker || strikerId == null) {
            strikerId = playerId
        } else {
            nonStrikerId = playerId
        }
        needsBatsmanSelection = false
        return true
    }

    fun selectBowler(bowlerId: String): Boolean {
        if (!needsBowlerSelection) return false
        val bowlingTeam = currentBowlingTeam()
        if (bowlingTeam.players.none { it.id == bowlerId }) return false

        // Cannot bowl consecutive overs unless only 1 bowler available
        if (bowlingTeam.players.size > 1 && bowlerId == previousBowlerId) {
            return false
        }

        currentBowlerId = bowlerId
        needsBowlerSelection = false
        return true
    }

    fun rotateStrike() {
        val temp = strikerId
        strikerId = nonStrikerId
        nonStrikerId = temp
    }

    fun swapStrikeManually(): Boolean {
        if (strikerId == null || nonStrikerId == null) return false
        rotateStrike()
        return true
    }

    fun undoLastBall(): Boolean {
        val balls = currentBallsList()
        val snapshots = currentSnapshotsList()
        if (balls.isEmpty() || snapshots.isEmpty()) return false

        balls.removeAt(balls.size - 1)
        val lastSnap = snapshots.removeAt(snapshots.size - 1)

        // Restore snapshot state
        currentInningsNumber = lastSnap.currentInnings
        strikerId = lastSnap.strikerId
        nonStrikerId = lastSnap.nonStrikerId
        currentBowlerId = lastSnap.currentBowlerId
        previousBowlerId = lastSnap.previousBowlerId
        isFreeHitNext = lastSnap.isFreeHitNext
        needsBowlerSelection = lastSnap.needsBowlerSelection
        needsBatsmanSelection = lastSnap.needsBatsmanSelection
        dismissedBatsmanWasStriker = lastSnap.dismissedBatsmanWasStriker

        // Re-evaluate match status
        if (currentInningsNumber == 1) {
            status = MatchStatus.INNINGS_1
        } else {
            status = MatchStatus.INNINGS_2
        }

        return true
    }

    fun isMatchOver(): Boolean = status == MatchStatus.COMPLETED || status == MatchStatus.ABANDONED
    fun isInningsBreak(): Boolean = status == MatchStatus.INNINGS_BREAK

    fun getMatchResult(): MatchResult? {
        if (status != MatchStatus.COMPLETED) return null
        val score1 = getInningsScorecard(1)
        val score2 = getInningsScorecard(2)

        val target = targetScore ?: (score1.totalRuns + 1)
        val runs2 = score2.totalRuns
        val wickets2 = score2.totalWickets

        return when {
            runs2 >= target -> {
                val wicketsRemaining = maxWickets - wickets2
                MatchResult(
                    winnerTeamId = currentBowlingTeam().id, // Team batting 2nd is bowlingFirstTeam
                    winnerTeamName = bowlingFirstTeam.name,
                    resultSummary = "${bowlingFirstTeam.name} won by $wicketsRemaining wicket${if (wicketsRemaining > 1) "s" else ""}",
                    isTie = false
                )
            }
            runs2 == target - 1 -> {
                MatchResult(
                    winnerTeamId = null,
                    winnerTeamName = null,
                    resultSummary = "Match Tied (${score1.totalRuns} runs each)!",
                    isTie = true
                )
            }
            else -> {
                val runMargin = (target - 1) - runs2
                MatchResult(
                    winnerTeamId = battingFirstTeam.id,
                    winnerTeamName = battingFirstTeam.name,
                    resultSummary = "${battingFirstTeam.name} won by $runMargin run${if (runMargin > 1) "s" else ""}",
                    isTie = false
                )
            }
        }
    }

    // --- DETAILED SCORECARD BUILDER ---

    fun getInningsScorecard(inningsNumber: Int): InningsScorecard {
        val battingTeam = if (inningsNumber == 1) battingFirstTeam else bowlingFirstTeam
        val bowlingTeam = if (inningsNumber == 1) bowlingFirstTeam else battingFirstTeam
        val balls = if (inningsNumber == 1) innings1Balls else innings2Balls

        // 1. Batting Stats
        val batterStatsMap = linkedMapOf<String, BatterStats>()
        // Initialize batters in order of appearance
        val orderedBatterIds = mutableListOf<String>()

        fun ensureBatter(id: String, name: String) {
            if (!orderedBatterIds.contains(id)) {
                orderedBatterIds.add(id)
                batterStatsMap[id] = BatterStats(playerId = id, playerName = name)
            }
        }

        // Initialize players who have batted
        balls.forEach { b ->
            ensureBatter(b.strikerId, b.strikerName)
            ensureBatter(b.nonStrikerId, b.nonStrikerName)
        }

        var widesCount = 0
        var noBallsCount = 0
        var byesCount = 0
        var legByesCount = 0

        val fallOfWickets = mutableListOf<FallOfWicket>()
        var runningRuns = 0
        var runningWickets = 0
        var runningLegalBalls = 0

        balls.forEach { b ->
            runningRuns += b.totalRunsOnBall
            if (b.isLegalDelivery) {
                runningLegalBalls++
            }

            // Extras
            when (b.extraType) {
                ExtraType.WIDE -> widesCount += b.extraRuns
                ExtraType.NO_BALL -> noBallsCount += b.extraRuns
                ExtraType.BYE -> byesCount += b.extraRuns
                ExtraType.LEG_BYE -> legByesCount += b.extraRuns
                ExtraType.NONE -> {}
            }

            // Batting tally
            val current = batterStatsMap[b.strikerId]
            if (current != null) {
                val runsAdd = b.runsOffBat
                val ballsAdd = if (b.extraType == ExtraType.WIDE) 0 else 1
                val foursAdd = if (b.runsOffBat == 4) 1 else 0
                val sixesAdd = if (b.runsOffBat == 6) 1 else 0

                batterStatsMap[b.strikerId] = current.copy(
                    runs = current.runs + runsAdd,
                    ballsFaced = current.ballsFaced + ballsAdd,
                    fours = current.fours + foursAdd,
                    sixes = current.sixes + sixesAdd
                )
            }

            // Wicket
            if (b.isWicket && b.dismissedPlayerId != null) {
                runningWickets++
                val dId = b.dismissedPlayerId
                val dismissedStat = batterStatsMap[dId]
                val dText = when (b.wicketType) {
                    WicketType.BOWLED -> "b ${b.bowlerName}"
                    WicketType.CAUGHT -> {
                        if (b.fielderName != null && b.fielderName != b.bowlerName) {
                            "c ${b.fielderName} b ${b.bowlerName}"
                        } else {
                            "c & b ${b.bowlerName}"
                        }
                    }
                    WicketType.LBW -> "lbw b ${b.bowlerName}"
                    WicketType.STUMPED -> "st ${b.fielderName ?: "WK"} b ${b.bowlerName}"
                    WicketType.HIT_WICKET -> "hit wicket b ${b.bowlerName}"
                    WicketType.RUN_OUT_STRIKER, WicketType.RUN_OUT_NON_STRIKER -> {
                        "run out (${b.fielderName ?: ""})"
                    }
                    WicketType.RETIRED_HURT -> "retired hurt"
                    null -> "out"
                }

                if (dismissedStat != null) {
                    batterStatsMap[dId] = dismissedStat.copy(
                        isOut = true,
                        dismissalText = dText
                    )
                }

                fallOfWickets.add(
                    FallOfWicket(
                        wicketNumber = runningWickets,
                        runs = runningRuns,
                        dismissedPlayerName = b.dismissedPlayerName ?: "Unknown",
                        overString = "${runningLegalBalls / 6}.${runningLegalBalls % 6}"
                    )
                )
            }
        }

        // Add any remaining team members as 'did not bat'
        battingTeam.players.forEach { p ->
            if (!batterStatsMap.containsKey(p.id)) {
                batterStatsMap[p.id] = BatterStats(
                    playerId = p.id,
                    playerName = p.name,
                    dismissalText = "did not bat"
                )
            }
        }

        // 2. Bowling Stats
        val bowlerStatsMap = linkedMapOf<String, BowlerStats>()
        balls.forEach { b ->
            if (!bowlerStatsMap.containsKey(b.bowlerId)) {
                bowlerStatsMap[b.bowlerId] = BowlerStats(
                    playerId = b.bowlerId,
                    playerName = b.bowlerName
                )
            }
            val current = bowlerStatsMap[b.bowlerId]!!

            val legalAdd = if (b.isLegalDelivery) 1 else 0
            val wideAdd = if (b.extraType == ExtraType.WIDE) 1 else 0
            val noBallAdd = if (b.extraType == ExtraType.NO_BALL) 1 else 0

            // Bowler conceded: runs off bat + wides + no-balls (byes and leg-byes are NOT charged)
            val conceded = b.runsOffBat + (if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) b.extraRuns else 0)

            // Bowler's wickets (run-outs do NOT count toward bowler's wickets)
            val wicketAdd = if (b.isWicket && b.wicketType != WicketType.RUN_OUT_STRIKER &&
                b.wicketType != WicketType.RUN_OUT_NON_STRIKER && b.wicketType != WicketType.RETIRED_HURT) 1 else 0

            val dotAdd = if (b.isLegalDelivery && b.totalRunsOnBall == 0) 1 else 0

            bowlerStatsMap[b.bowlerId] = current.copy(
                legalBallsBowled = current.legalBallsBowled + legalAdd,
                runsConceded = current.runsConceded + conceded,
                wickets = current.wickets + wicketAdd,
                wides = current.wides + wideAdd,
                noBalls = current.noBalls + noBallAdd,
                dotBalls = current.dotBalls + dotAdd
            )
        }

        // Calculate maidens per bowler
        val oversGrouped = balls.groupBy { "${it.bowlerId}_${it.overIndex}" }
        oversGrouped.forEach { (key, overBalls) ->
            val bId = key.substringBefore("_")
            val legalInOver = overBalls.count { it.isLegalDelivery }
            if (legalInOver == 6) {
                val runsInOverCharged = overBalls.sumOf { b ->
                    b.runsOffBat + (if (b.extraType == ExtraType.WIDE || b.extraType == ExtraType.NO_BALL) b.extraRuns else 0)
                }
                if (runsInOverCharged == 0 && bowlerStatsMap.containsKey(bId)) {
                    val bStat = bowlerStatsMap[bId]!!
                    bowlerStatsMap[bId] = bStat.copy(maidens = bStat.maidens + 1)
                }
            }
        }

        val totalRuns = balls.sumOf { it.totalRunsOnBall }
        val totalWickets = balls.count { it.isWicket && it.wicketType != WicketType.RETIRED_HURT }
        val totalLegalBalls = balls.count { it.isLegalDelivery }

        return InningsScorecard(
            inningsNumber = inningsNumber,
            battingTeamId = battingTeam.id,
            battingTeamName = battingTeam.name,
            bowlingTeamId = bowlingTeam.id,
            bowlingTeamName = bowlingTeam.name,
            totalRuns = totalRuns,
            totalWickets = totalWickets,
            totalLegalBalls = totalLegalBalls,
            batters = batterStatsMap.values.toList(),
            bowlers = bowlerStatsMap.values.toList(),
            extras = ExtrasBreakdown(widesCount, noBallsCount, byesCount, legByesCount),
            fallOfWickets = fallOfWickets,
            isCompleted = if (inningsNumber == 1) status != MatchStatus.INNINGS_1 else status == MatchStatus.COMPLETED
        )
    }
}
