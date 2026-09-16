package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.b47tech.cricketscore.ui.theme.*

@Composable
fun InningsBreakDialog(
    battingTeamName: String,
    bowlingTeamName: String,
    totalRuns: Int,
    totalWickets: Int,
    oversString: String,
    target: Int,
    onStartSecondInnings: () -> Unit,
    onViewScorecard: () -> Unit
) {
    Dialog(onDismissRequest = { /* Modal */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Innings Break",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CricketGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$battingTeamName scored $totalRuns/$totalWickets in $oversString overs.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Target for $bowlingTeamName",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$target Runs",
                            color = CricketGoldLight,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onStartSecondInnings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Start 2nd Innings",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                OutlinedButton(
                    onClick = onViewScorecard,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "View 1st Innings Scorecard",
                        color = CricketGold
                    )
                }
            }
        }
    }
}
