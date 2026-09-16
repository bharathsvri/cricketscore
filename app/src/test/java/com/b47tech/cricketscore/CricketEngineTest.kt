package com.b47tech.cricketscore

import com.b47tech.cricketscore.core.engine.CricketEngine
import com.b47tech.cricketscore.core.engine.MatchStatus
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.core.engine.Team
import com.b47tech.cricketscore.core.engine.WicketType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CricketEngineTest {

    private lateinit var teamA: Team
    private lateinit var teamB: Team
    private lateinit var engine: CricketEngine

    @Before
    fun setUp() {
        val playersA = (1..11).map { Player("A$it", "Player A$it", "teamA") }
        val playersB = (1..11).map { Player("B$it", "Player B$it", "teamB") }
        teamA = Team("teamA", "India", playersA)
        teamB = Team("teamB", "Australia", playersB)

        // India won toss and chose to BAT
        engine = CricketEngine(
            matchId = "test-match-1",
            teamA = teamA,
            teamB = teamB,
            totalOvers = 2,
            playersPerTeam = 11,
            ballType = "Leather",
            tossWinnerId = "teamA",
            tossDecision = "BAT"
        )
    }

    @Test
    fun testStartMatchAndFirstInnings() {
        assertEquals(teamA.id, engine.battingFirstTeam.id)
        assertEquals(teamB.id, engine.bowlingFirstTeam.id)

        engine.startFirstInnings("A1", "A2", "B1")
        assertEquals(MatchStatus.INNINGS_1, engine.status)
        assertEquals("A1", engine.strikerId)
        assertEquals("A2", engine.nonStrikerId)
        assertEquals("B1", engine.currentBowlerId)
        assertEquals(0, engine.getTotalRuns())
        assertEquals(0, engine.getTotalWickets())
        assertEquals("0.0", engine.getOversString())
    }

    @Test
    fun testScoringRunsAndStrikeRotation() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Ball 1: Single run -> Strike rotates to A2
        engine.recordRuns(1)
        assertEquals(1, engine.getTotalRuns())
        assertEquals("A2", engine.strikerId)
        assertEquals("A1", engine.nonStrikerId)
        assertEquals("0.1", engine.getOversString())

        // Ball 2: Dot ball -> A2 remains on strike
        engine.recordDotBall()
        assertEquals(1, engine.getTotalRuns())
        assertEquals("A2", engine.strikerId)
        assertEquals("0.2", engine.getOversString())

        // Ball 3: Boundary (4) -> A2 remains on strike
        engine.recordBoundary(isFour = true)
        assertEquals(5, engine.getTotalRuns())
        assertEquals("A2", engine.strikerId)
        assertEquals("0.3", engine.getOversString())

        // Ball 4: Six (6) -> A2 remains on strike
        engine.recordBoundary(isFour = false)
        assertEquals(11, engine.getTotalRuns())
        assertEquals("A2", engine.strikerId)

        // Check batter stats
        val scorecard = engine.getInningsScorecard(1)
        val a1 = scorecard.batters.first { it.playerId == "A1" }
        val a2 = scorecard.batters.first { it.playerId == "A2" }
        assertEquals(1, a1.runs)
        assertEquals(1, a1.ballsFaced)
        assertEquals(10, a2.runs)
        assertEquals(3, a2.ballsFaced)
        assertEquals(1, a2.fours)
        assertEquals(1, a2.sixes)
    }

    @Test
    fun testOverCompletionAndBowlerSelection() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Bowl 6 singles
        for (i in 1..6) {
            engine.recordRuns(1)
        }

        assertEquals(6, engine.getTotalRuns())
        assertEquals("1.0", engine.getOversString())
        assertTrue(engine.needsBowlerSelection)

        // B1 cannot bowl 2 consecutive overs
        assertFalse(engine.selectBowler("B1"))

        // Select B2
        assertTrue(engine.selectBowler("B2"))
        assertFalse(engine.needsBowlerSelection)
        assertEquals("B2", engine.currentBowlerId)
    }

    @Test
    fun testWidesAndNoBallsWithFreeHit() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Wide ball: adds 1 extra, does NOT increment ball count
        engine.recordWide(extraRuns = 0)
        assertEquals(1, engine.getTotalRuns())
        assertEquals("0.0", engine.getOversString())
        assertEquals(0, engine.getLegalBallsBowled())

        // No-ball with 2 runs off bat: total 3 runs (1 penalty + 2 bat)
        engine.recordNoBall(runsOffBat = 2, extraRuns = 0)
        assertEquals(4, engine.getTotalRuns())
        assertEquals("0.0", engine.getOversString())
        assertTrue(engine.isFreeHitNext)

        // Attempt Bowled on Free Hit -> Disallowed by Free Hit rule!
        assertFalse(engine.recordWicket(WicketType.BOWLED, "A1"))

        // Free Hit delivery: Single run taken -> Free Hit consumed
        engine.recordRuns(1)
        assertEquals(5, engine.getTotalRuns())
        assertEquals("0.1", engine.getOversString())
        assertFalse(engine.isFreeHitNext)
    }

    @Test
    fun testWicketAndBatsmanSelection() {
        engine.startFirstInnings("A1", "A2", "B1")

        // A1 caught out
        assertTrue(engine.recordWicket(WicketType.CAUGHT, dismissedPlayerId = "A1", fielderId = "B2"))
        assertEquals(1, engine.getTotalWickets())
        assertTrue(engine.needsBatsmanSelection)

        // Select A3 as new batsman
        assertTrue(engine.selectNewBatsman("A3"))
        assertFalse(engine.needsBatsmanSelection)
        assertEquals("A3", engine.strikerId)

        val scorecard = engine.getInningsScorecard(1)
        val a1 = scorecard.batters.first { it.playerId == "A1" }
        assertTrue(a1.isOut)
        assertEquals("c Player B2 b Player B1", a1.dismissalText)
        assertEquals(1, scorecard.fallOfWickets.size)
        assertEquals("Player A1", scorecard.fallOfWickets.first().dismissedPlayerName)
    }

    @Test
    fun testUndoFunctionality() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Ball 1: 4 runs
        engine.recordBoundary(isFour = true)
        assertEquals(4, engine.getTotalRuns())
        assertEquals("0.1", engine.getOversString())

        // Undo Ball 1
        assertTrue(engine.undoLastBall())
        assertEquals(0, engine.getTotalRuns())
        assertEquals("0.0", engine.getOversString())
        assertEquals("A1", engine.strikerId)

        // Wicket then undo
        engine.recordWicket(WicketType.BOWLED, "A1")
        assertEquals(1, engine.getTotalWickets())
        assertTrue(engine.needsBatsmanSelection)

        assertTrue(engine.undoLastBall())
        assertEquals(0, engine.getTotalWickets())
        assertFalse(engine.needsBatsmanSelection)
        assertEquals("A1", engine.strikerId)
    }

    @Test
    fun testSecondInningsAndMatchResult() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Total 2 overs match. Score 12 runs in 2 overs (12 balls)
        for (i in 1..12) {
            engine.recordRuns(1)
            if (engine.needsBowlerSelection) {
                engine.selectBowler(if (i <= 6) "B2" else "B1")
            }
        }

        assertEquals(MatchStatus.INNINGS_BREAK, engine.status)
        assertEquals(12, engine.getTotalRuns(1))

        // Start 2nd Innings: Target is 13 (12 + 1)
        engine.startSecondInnings("B1", "B2", "A1")
        assertEquals(MatchStatus.INNINGS_2, engine.status)
        assertEquals(13, engine.targetScore)
        assertEquals(13, engine.getRunsNeeded())

        // Australia hits 2 sixes and a single: 6 + 6 + 1 = 13 runs!
        engine.recordBoundary(false)
        engine.recordBoundary(false)
        engine.recordRuns(1)

        assertEquals(MatchStatus.COMPLETED, engine.status)
        val result = engine.getMatchResult()
        assertNotNull(result)
        assertEquals(teamB.name, result!!.winnerTeamName)
        assertTrue(result.resultSummary.contains("Australia won by 10 wickets"))
    }

    @Test
    fun testByesAndLegByes() {
        engine.startFirstInnings("A1", "A2", "B1")

        // 1 Bye -> Team gets 1 run, batsman gets 0 runs, strike rotates, legal delivery counts
        assertTrue(engine.recordBye(runs = 1, isLegBye = false))
        assertEquals(1, engine.getTotalRuns())
        assertEquals("0.1", engine.getOversString())
        assertEquals("A2", engine.strikerId)

        // Bowler should NOT be charged with bye runs
        val scorecard = engine.getInningsScorecard(1)
        val bowler = scorecard.bowlers.first { it.playerId == "B1" }
        assertEquals(0, bowler.runsConceded)
        assertEquals(1, scorecard.extras.byes)

        // 2 Leg Byes -> Even runs, strike remains with A2
        assertTrue(engine.recordBye(runs = 2, isLegBye = true))
        assertEquals(3, engine.getTotalRuns())
        assertEquals("A2", engine.strikerId)
        assertEquals(2, engine.getInningsScorecard(1).extras.legByes)
    }

    @Test
    fun testRunOutAllowedOnFreeHit() {
        engine.startFirstInnings("A1", "A2", "B1")

        // Bowled No Ball -> triggers Free Hit
        engine.recordNoBall(runsOffBat = 0, extraRuns = 0)
        assertTrue(engine.isFreeHitNext)

        // Bowled is disallowed on Free Hit
        assertFalse(engine.recordWicket(WicketType.BOWLED, "A1"))

        // Run out IS allowed on Free Hit!
        assertTrue(engine.recordWicket(WicketType.RUN_OUT_STRIKER, dismissedPlayerId = "A1", fielderId = "B2"))
        assertEquals(1, engine.getTotalWickets())
        assertFalse(engine.isFreeHitNext)
    }

    @Test
    fun testMatchTiedCondition() {
        engine.startFirstInnings("A1", "A2", "B1")

        // 1st Innings: 10 runs scored in 2 overs (12 balls)
        for (i in 1..10) {
            engine.recordRuns(1)
            if (engine.needsBowlerSelection) engine.selectBowler("B2")
        }
        engine.recordDotBall()
        engine.recordDotBall()

        assertEquals(MatchStatus.INNINGS_BREAK, engine.status)
        assertEquals(10, engine.getTotalRuns(1))

        // 2nd Innings: Target is 11
        engine.startSecondInnings("B1", "B2", "A1")
        assertEquals(11, engine.targetScore)

        // 2nd Innings: Australia also scores exactly 10 runs in their 2 overs
        for (i in 1..10) {
            engine.recordRuns(1)
            if (engine.needsBowlerSelection) engine.selectBowler("A2")
        }
        engine.recordDotBall()
        engine.recordDotBall()

        assertEquals(MatchStatus.COMPLETED, engine.status)
        val result = engine.getMatchResult()
        assertNotNull(result)
        assertTrue(result!!.isTie)
        assertTrue(result.resultSummary.contains("Match Tied"))
    }
}
