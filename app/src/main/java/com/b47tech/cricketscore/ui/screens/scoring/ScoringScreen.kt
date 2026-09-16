package com.b47tech.cricketscore.ui.screens.scoring

import android.app.Activity
import android.content.Intent
import com.b47tech.cricketscore.core.ads.AdManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.b47tech.cricketscore.core.engine.MatchStatus
import com.b47tech.cricketscore.ui.components.*
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoringScreen(
    viewModel: ScoringViewModel,
    onBackToHome: () -> Unit,
    onNavigateToScorecard: (matchId: String) -> Unit,
    onNavigateToSecondInningsOpeners: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showWicketDialog by remember { mutableStateOf(false) }
    var manualBowlerSelection by remember { mutableStateOf(false) }

    val engine = viewModel.engine

    fun shareMatchSummary() {
        val summary = buildString {
            appendLine("🏏 ${state.battingTeamName} vs ${state.bowlingTeamName}")
            appendLine("Score: ${state.totalRuns}/${state.totalWickets} (${state.oversString}/${state.totalOvers} ov)")
            if (state.isSecondInnings && state.targetScore != null) {
                appendLine("Target: ${state.targetScore} | CRR: ${String.format("%.2f", state.crr)} | RRR: ${String.format("%.2f", state.rrr)}")
            }
            if (state.matchResult != null) {
                appendLine("Result: ${state.matchResult?.resultSummary}")
            }
            appendLine("Scored with B47 Cricket Score by B47 Tech")
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, summary)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Score"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${state.battingTeamName} vs ${state.bowlingTeamName}",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToScorecard(state.matchId) }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Scorecard", tint = CricketGold)
                    }
                    IconButton(onClick = { shareMatchSummary() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Live Score Board
            ScoreBoardCard(
                battingTeamName = state.battingTeamName,
                bowlingTeamName = state.bowlingTeamName,
                totalRuns = state.totalRuns,
                totalWickets = state.totalWickets,
                completedOversString = state.oversString,
                totalOvers = state.totalOvers,
                crr = state.crr,
                rrr = state.rrr,
                isSecondInnings = state.isSecondInnings,
                targetScore = state.targetScore,
                runsNeeded = state.runsNeeded,
                ballsRemaining = state.ballsRemaining,
                isFreeHit = state.isFreeHit
            )

            // Batters Table
            BatsmenTable(
                striker = state.striker,
                nonStriker = state.nonStriker,
                onSwapStrike = { viewModel.swapStrike() }
            )

            // Current Bowler Table
            BowlerTable(
                bowler = state.currentBowler,
                onChangeBowler = { manualBowlerSelection = true }
            )

            // This Over Timeline
            ThisOverTimeline(
                deliveries = state.currentOverDeliveries
            )

            // Scoring Keypad
            ScoringKeypad(
                onRunClick = { runs -> viewModel.recordRuns(runs) },
                onWideClick = { extraRuns -> viewModel.recordWide(extraRuns) },
                onNoBallClick = { runsOffBat, extraRuns -> viewModel.recordNoBall(runsOffBat, extraRuns) },
                onByeClick = { runs, isLegBye -> viewModel.recordBye(runs, isLegBye) },
                onWicketClick = { showWicketDialog = true },
                onUndoClick = { viewModel.undo() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Wicket Dialog
        if (showWicketDialog && engine != null) {
            val curBattingTeam = engine.currentBattingTeam()
            val curBowlingTeam = engine.currentBowlingTeam()
            val strikerPlayer = curBattingTeam.players.find { it.id == engine.strikerId }
            val nonStrikerPlayer = curBattingTeam.players.find { it.id == engine.nonStrikerId }

            val battedIds = engine.getInningsScorecard(engine.currentInningsNumber).batters
                .filter { it.isOut || it.playerId == engine.strikerId || it.playerId == engine.nonStrikerId }
                .map { it.playerId }
            val availableNextBatters = curBattingTeam.players.filterNot { battedIds.contains(it.id) }

            WicketDialog(
                striker = strikerPlayer,
                nonStriker = nonStrikerPlayer,
                fielders = curBowlingTeam.players,
                availableNextBatters = availableNextBatters,
                isFreeHit = state.isFreeHit,
                onDismiss = { showWicketDialog = false },
                onConfirmWicket = { wicketType, dismissedPlayerId, fielderId, runsCompleted, nextBatsmanId ->
                    viewModel.recordWicket(wicketType, dismissedPlayerId, fielderId, runsCompleted, nextBatsmanId)
                    showWicketDialog = false
                }
            )
        }

        // 2. Bowler Selection Dialog (Auto at over end or manual)
        if ((state.needsBowlerSelection || manualBowlerSelection) && engine != null) {
            val bowlingTeam = engine.currentBowlingTeam()
            val nextOverNum = (engine.getLegalBallsBowled() / 6) + 1
            BowlerSelectionDialog(
                bowlers = bowlingTeam.players,
                previousBowlerId = engine.previousBowlerId,
                overNumber = nextOverNum,
                onBowlerSelected = { bowler ->
                    viewModel.selectBowler(bowler.id)
                    manualBowlerSelection = false
                }
            )
        }

        // 3. Batsman Selection Dialog (If batsman was not selected in wicket dialog)
        if (state.needsBatsmanSelection && engine != null) {
            val curBattingTeam = engine.currentBattingTeam()
            val battedIds = engine.getInningsScorecard(engine.currentInningsNumber).batters
                .filter { it.isOut || it.playerId == engine.strikerId || it.playerId == engine.nonStrikerId }
                .map { it.playerId }
            val availableNextBatters = curBattingTeam.players.filterNot { battedIds.contains(it.id) }

            if (availableNextBatters.isNotEmpty()) {
                BatsmanSelectionDialog(
                    availableBatters = availableNextBatters,
                    onBatsmanSelected = { batsman ->
                        viewModel.selectNewBatsman(batsman.id)
                    }
                )
            }
        }

        // 4. Innings Break Dialog
        if (state.status == MatchStatus.INNINGS_BREAK && engine != null) {
            InningsBreakDialog(
                battingTeamName = engine.battingFirstTeam.name,
                bowlingTeamName = engine.bowlingFirstTeam.name,
                totalRuns = engine.getTotalRuns(1),
                totalWickets = engine.getTotalWickets(1),
                oversString = engine.getOversString(1),
                target = (engine.getTotalRuns(1) + 1),
                onStartSecondInnings = onNavigateToSecondInningsOpeners,
                onViewScorecard = { onNavigateToScorecard(state.matchId) }
            )
        }

        // 5. Match Completed Dialog
        if (state.status == MatchStatus.COMPLETED && state.matchResult != null) {
            val activity = context as? Activity
            MatchCompletedDialog(
                resultSummary = state.matchResult!!.resultSummary,
                onViewScorecard = {
                    if (activity != null) {
                        AdManager.getInstance().showMatchCompletedInterstitial(activity) {
                            onNavigateToScorecard(state.matchId)
                        }
                    } else {
                        onNavigateToScorecard(state.matchId)
                    }
                },
                onShare = { shareMatchSummary() },
                onGoHome = {
                    if (activity != null) {
                        AdManager.getInstance().showMatchCompletedInterstitial(activity) {
                            onBackToHome()
                        }
                    } else {
                        onBackToHome()
                    }
                }
            )
        }
    }
}
