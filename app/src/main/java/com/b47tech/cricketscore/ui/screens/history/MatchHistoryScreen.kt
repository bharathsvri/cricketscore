package com.b47tech.cricketscore.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHistoryScreen(
    viewModel: MatchHistoryViewModel,
    onBack: () -> Unit,
    onResumeMatch: (matchId: String) -> Unit,
    onViewScorecard: (matchId: String) -> Unit
) {
    val matches by viewModel.matches.collectAsState()
    var matchToDelete by remember { mutableStateOf<MatchEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match History", fontWeight = FontWeight.Bold, color = TextWhite) },
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
        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches found.",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches) { match ->
                    val isCompleted = match.status == "COMPLETED"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (isCompleted) {
                                    onViewScorecard(match.id)
                                } else {
                                    onResumeMatch(match.id)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${match.teamAName} vs ${match.teamBName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { matchToDelete = match },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextMuted
                                    )
                                }
                            }

                            if (!match.resultSummary.isNullOrBlank()) {
                                Text(
                                    text = match.resultSummary,
                                    color = CricketGoldLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${match.overs} Overs • ${match.ballType}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )

                                Surface(
                                    color = if (isCompleted) CricketGreenPrimary else CricketGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isCompleted) "Completed" else "In Progress",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        color = if (isCompleted) TextWhite else DarkBg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (matchToDelete != null) {
            AlertDialog(
                onDismissRequest = { matchToDelete = null },
                title = { Text("Delete Match?") },
                text = { Text("Are you sure you want to delete ${matchToDelete!!.teamAName} vs ${matchToDelete!!.teamBName}? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMatch(matchToDelete!!.id)
                            matchToDelete = null
                        }
                    ) {
                        Text("Delete", color = WicketRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { matchToDelete = null }) {
                        Text("Cancel", color = TextMuted)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}
