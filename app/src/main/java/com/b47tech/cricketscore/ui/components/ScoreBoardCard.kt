package com.b47tech.cricketscore.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.ui.theme.*
import java.util.Locale

@Composable
fun ScoreBoardCard(
    battingTeamName: String,
    bowlingTeamName: String,
    totalRuns: Int,
    totalWickets: Int,
    completedOversString: String,
    totalOvers: Int,
    crr: Double,
    rrr: Double,
    isSecondInnings: Boolean,
    targetScore: Int?,
    runsNeeded: Int,
    ballsRemaining: Int,
    isFreeHit: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Team Name and Innings Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = battingTeamName,
                        style = MaterialTheme.typography.titleLarge,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSecondInnings) "2nd Innings vs $bowlingTeamName" else "1st Innings vs $bowlingTeamName",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                // Free Hit Badge
                AnimatedVisibility(
                    visible = isFreeHit,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = CricketGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "⚡ FREE HIT",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = DarkBg,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Big Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Score: 142/3
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$totalRuns",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        lineHeight = 46.sp
                    )
                    Text(
                        text = " / $totalWickets",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = WicketRed,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Overs: (14.2 / 20)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "OVERS",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$completedOversString / $totalOvers",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenLight
                    )
                }
            }

            // Rates Row: CRR & RRR / Target
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CRR: ${String.format(Locale.US, "%.2f", crr)}",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (isSecondInnings && targetScore != null) {
                        Text(
                            text = "Target: $targetScore",
                            color = CricketGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "RRR: ${String.format(Locale.US, "%.2f", rrr)}",
                            color = if (rrr > 10.0) WicketRed else CricketGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Need text for 2nd innings
            if (isSecondInnings && targetScore != null) {
                Text(
                    text = "Need $runsNeeded runs in $ballsRemaining balls",
                    color = CricketGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
