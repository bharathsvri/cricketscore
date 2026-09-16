package com.b47tech.cricketscore.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartNewMatch: () -> Unit,
    onResumeMatch: (String) -> Unit,
    onViewScorecard: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val activeMatch by viewModel.activeMatch.collectAsState()
    val recentMatches by viewModel.recentMatches.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "B47 Cricket Score",
                            style = MaterialTheme.typography.titleLarge,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "By B47 Tech",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Player Stats",
                            tint = TextWhite
                        )
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Match History",
                            tint = TextWhite
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CricketGreenDark
                )
            )
        },
        bottomBar = {
            AdBanner()
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartNewMatch,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Match") },
                text = { Text("New Match", fontWeight = FontWeight.Bold) },
                containerColor = CricketGreenPrimary,
                contentColor = TextWhite
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Ongoing Match Banner (if any)
            if (activeMatch != null) {
                item {
                    ActiveMatchCard(
                        match = activeMatch!!,
                        onResumeClick = { onResumeMatch(activeMatch!!.id) }
                    )
                }
            }

            // Quick Start Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onStartNewMatch() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CricketGreenDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Start a Cricket Match",
                                style = MaterialTheme.typography.titleMedium,
                                color = CricketGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Custom teams, overs, toss, ball-by-ball scoring with full undo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.SportsCricket,
                            contentDescription = "Cricket Bat and Ball",
                            tint = CricketGold,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            // Recent Matches Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Matches",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (recentMatches.isNotEmpty()) {
                        TextButton(onClick = onNavigateToHistory) {
                            Text(text = "View All", color = CricketGold)
                        }
                    }
                }
            }

            // Recent Matches Items
            if (recentMatches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matches yet. Tap 'New Match' to start!",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(recentMatches.take(5)) { match ->
                    RecentMatchItem(
                        match = match,
                        onClick = { onViewScorecard(match.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveMatchCard(
    match: MatchEntity,
    onResumeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = CricketGold,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LIVE / ONGOING",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = DarkBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "${match.overs} Overs Match",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "${match.teamAName} vs ${match.teamBName}",
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Innings ${match.currentInnings} in progress",
                color = CricketGreenLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = onResumeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Resume Scoring", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecentMatchItem(
    match: MatchEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.teamAName} vs ${match.teamBName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (match.status == "COMPLETED") "Completed" else "In Progress",
                    color = if (match.status == "COMPLETED") CricketGreenLight else CricketGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!match.resultSummary.isNullOrBlank()) {
                Text(
                    text = match.resultSummary,
                    color = CricketGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "${match.overs} ov • ${match.ballType} ball",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}
