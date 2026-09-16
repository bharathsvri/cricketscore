package com.b47tech.cricketscore.ui.screens.scorecard

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.InningsScorecard
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    matchId: String,
    viewModel: ScorecardViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    fun shareScorecardText() {
        val sc1 = state.innings1Scorecard
        val sc2 = state.innings2Scorecard
        val text = buildString {
            appendLine("🏏 ${state.matchTitle}")
            if (!state.resultSummary.isNullOrBlank()) {
                appendLine("🏆 Result: ${state.resultSummary}")
            }
            appendLine("------------------------------")
            if (sc1 != null) {
                appendLine("1st Innings: ${sc1.battingTeamName}")
                appendLine("Score: ${sc1.totalRuns}/${sc1.totalWickets} (${sc1.oversString} ov)")
                appendLine("Top Batters:")
                sc1.batters.filter { it.ballsFaced > 0 }.sortedByDescending { it.runs }.take(3).forEach {
                    appendLine("• ${it.playerName}: ${it.runs} (${it.ballsFaced}b, ${it.fours}x4, ${it.sixes}x6)")
                }
                appendLine("Top Bowlers:")
                sc1.bowlers.filter { it.legalBallsBowled > 0 }.sortedByDescending { it.wickets }.take(2).forEach {
                    appendLine("• ${it.playerName}: ${it.wickets}/${it.runsConceded} (${it.oversString} ov)")
                }
            }
            if (sc2 != null) {
                appendLine("------------------------------")
                appendLine("2nd Innings: ${sc2.battingTeamName}")
                appendLine("Score: ${sc2.totalRuns}/${sc2.totalWickets} (${sc2.oversString} ov)")
                appendLine("Top Batters:")
                sc2.batters.filter { it.ballsFaced > 0 }.sortedByDescending { it.runs }.take(3).forEach {
                    appendLine("• ${it.playerName}: ${it.runs} (${it.ballsFaced}b, ${it.fours}x4, ${it.sixes}x6)")
                }
                appendLine("Top Bowlers:")
                sc2.bowlers.filter { it.legalBallsBowled > 0 }.sortedByDescending { it.wickets }.take(2).forEach {
                    appendLine("• ${it.playerName}: ${it.wickets}/${it.runsConceded} (${it.oversString} ov)")
                }
            }
            appendLine("------------------------------")
            appendLine("Scored with B47 Cricket Score by B47 Tech")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Scorecard"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.matchTitle.ifEmpty { "Match Scorecard" }, fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { shareScorecardText() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = CricketGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        },
        bottomBar = {
            AdBanner()
        },
        containerColor = DarkBg
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CricketGold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Result Banner (if completed)
                if (!state.resultSummary.isNullOrBlank()) {
                    Surface(
                        color = CricketGreenPrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🏆 ${state.resultSummary}",
                            modifier = Modifier.padding(12.dp),
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Innings Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = CricketGold
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = state.innings1Scorecard?.let { "1st: ${it.battingTeamName}" } ?: "1st Innings",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        enabled = state.hasSecondInnings,
                        text = {
                            Text(
                                text = state.innings2Scorecard?.let { "2nd: ${it.battingTeamName}" } ?: "2nd Innings",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }

                val currentScorecard = if (selectedTab == 0) state.innings1Scorecard else state.innings2Scorecard

                if (currentScorecard != null) {
                    InningsScorecardView(scorecard = currentScorecard)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2nd Innings has not started yet.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InningsScorecardView(scorecard: InningsScorecard) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Total Score Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = scorecard.battingTeamName,
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Run Rate: ${String.format(Locale.US, "%.2f", scorecard.runRate)}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "${scorecard.totalRuns}/${scorecard.totalWickets} (${scorecard.oversString} ov)",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Batting Table Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Batting", color = CricketGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
                        Text("R", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                        Text("B", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                        Text("4s", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                        Text("6s", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                        Text("SR", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                    }

                    HorizontalDivider(color = DarkSurfaceVariant)

                    scorecard.batters.filter { it.ballsFaced > 0 || it.isOut }.forEach { batter ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(2f)) {
                                Text(batter.playerName, color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(batter.dismissalText, color = TextMuted, fontSize = 10.sp)
                            }
                            Text("${batter.runs}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(0.6f))
                            Text("${batter.ballsFaced}", color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                            Text("${batter.fours}", color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                            Text("${batter.sixes}", color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                            Text(String.format(Locale.US, "%.1f", batter.strikeRate), color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                        }
                    }

                    // Did Not Bat
                    val dnb = scorecard.batters.filter { it.ballsFaced == 0 && !it.isOut }
                    if (dnb.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Did not bat: " + dnb.joinToString(", ") { it.playerName },
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    HorizontalDivider(color = DarkSurfaceVariant)

                    // Extras
                    val ex = scorecard.extras
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Extras", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = "${ex.total} (b ${ex.byes}, lb ${ex.legByes}, w ${ex.wides}, nb ${ex.noBalls})",
                            color = CricketGoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Bowling Table Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bowling", color = CricketGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(2f))
                        Text("O", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                        Text("M", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                        Text("R", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                        Text("W", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                        Text("ECON", color = TextMuted, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                    }

                    HorizontalDivider(color = DarkSurfaceVariant)

                    scorecard.bowlers.filter { it.legalBallsBowled > 0 }.forEach { bowler ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(bowler.playerName, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(2f))
                            Text(bowler.oversString, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                            Text("${bowler.maidens}", color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(0.5f))
                            Text("${bowler.runsConceded}", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(0.6f))
                            Text("${bowler.wickets}", color = WicketRed, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(0.6f))
                            Text(String.format(Locale.US, "%.1f", bowler.economyRate), color = TextMuted, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                        }
                    }
                }
            }
        }

        // Fall of Wickets Card
        if (scorecard.fallOfWickets.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Fall of Wickets",
                            color = CricketGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        HorizontalDivider(color = DarkSurfaceVariant)
                        scorecard.fallOfWickets.forEach { fow ->
                            Text(
                                text = "${fow.wicketNumber}-${fow.runs} (${fow.dismissedPlayerName}, ${fow.overString} ov)",
                                color = TextWhite,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
