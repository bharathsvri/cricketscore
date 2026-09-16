package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
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
fun MatchCompletedDialog(
    resultSummary: String,
    onViewScorecard: () -> Unit,
    onShare: () -> Unit,
    onGoHome: () -> Unit
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
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = CricketGold,
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "Match Finished!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CricketGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = resultSummary,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onViewScorecard,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "View Full Scorecard",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = CricketGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Scorecard",
                        color = CricketGold
                    )
                }

                TextButton(
                    onClick = onGoHome,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Return to Home",
                        color = TextMuted
                    )
                }
            }
        }
    }
}
