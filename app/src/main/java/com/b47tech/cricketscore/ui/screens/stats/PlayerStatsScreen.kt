package com.b47tech.cricketscore.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerStatsScreen(
    viewModel: PlayerStatsViewModel,
    onBack: () -> Unit
) {
    val stats by viewModel.playerStats.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Batting, 1: Bowling

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Player Career Stats", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = CricketGold
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Batting Leaders", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Bowling Leaders", fontWeight = FontWeight.Bold) }
                )
            }

            if (stats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No player statistics yet. Complete matches to accumulate career records.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 0) {
                        val battingList = stats.filter { it.totalRuns > 0 || it.inningsBatted > 0 }
                            .sortedByDescending { it.totalRuns }
                        items(battingList) { player ->
                            BattingStatCard(player)
                        }
                    } else {
                        val bowlingList = stats.filter { it.legalBallsBowled > 0 }
                            .sortedByDescending { it.wicketsTaken }
                        items(bowlingList) { player ->
                            BowlingStatCard(player)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BattingStatCard(player: PlayerCareerStatsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CricketGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${player.totalRuns} Runs",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Matches", value = "${player.matchesPlayed}")
                StatItem(label = "Innings", value = "${player.inningsBatted}")
                StatItem(label = "Avg", value = String.format(Locale.US, "%.1f", player.battingAverage))
                StatItem(label = "SR", value = String.format(Locale.US, "%.1f", player.battingStrikeRate))
                StatItem(label = "HS", value = "${player.highestScore}")
                StatItem(label = "4s/6s", value = "${player.fours}/${player.sixes}")
            }
        }
    }
}

@Composable
fun BowlingStatCard(player: PlayerCareerStatsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = CricketGreenLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${player.wicketsTaken} Wickets",
                    style = MaterialTheme.typography.titleMedium,
                    color = WicketRed,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Matches", value = "${player.matchesPlayed}")
                StatItem(label = "Overs", value = player.oversBowledString)
                StatItem(label = "Runs", value = "${player.runsConceded}")
                StatItem(label = "Econ", value = String.format(Locale.US, "%.1f", player.bowlingEconomyRate))
                StatItem(label = "Avg", value = if (player.wicketsTaken > 0) String.format(Locale.US, "%.1f", player.bowlingAverage) else "-")
                StatItem(label = "Best", value = "${player.bestBowlingWickets}/${player.bestBowlingRuns}")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
        Text(text = value, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
