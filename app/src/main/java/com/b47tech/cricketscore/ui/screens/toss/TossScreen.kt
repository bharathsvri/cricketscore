package com.b47tech.cricketscore.ui.screens.toss

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.Team
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TossScreen(
    teamA: Team,
    teamB: Team,
    viewModel: TossViewModel,
    onBack: () -> Unit,
    onProceedToOpeners: (tossWinnerId: String, tossDecision: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val tossWinnerName = if (state.tossWinnerId == teamA.id) teamA.name else teamB.name

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Toss", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onProceedToOpeners(state.tossWinnerId, state.tossDecision) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Select Openers & Bowler",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    AdBanner()
                }
            }
        },
        containerColor = DarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Who won toss card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Who won the Toss?",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(teamA, teamB).forEach { team ->
                            val isSelected = state.tossWinnerId == team.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateTossWinner(team.id) },
                                color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = team.name,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Decision card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "$tossWinnerName elected to?",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("BAT" to "🏏 Bat First", "BOWL" to "⚾ Bowl First").forEach { (decision, label) ->
                            val isSelected = state.tossDecision.equals(decision, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateTossDecision(decision) },
                                color = if (isSelected) CricketGold else DarkSurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DarkBg else TextWhite,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$tossWinnerName won the toss and elected to ${state.tossDecision.lowercase()} first.",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGoldLight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
