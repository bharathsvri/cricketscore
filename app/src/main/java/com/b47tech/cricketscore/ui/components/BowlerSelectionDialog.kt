package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.ui.theme.*

@Composable
fun BowlerSelectionDialog(
    bowlers: List<Player>,
    previousBowlerId: String?,
    overNumber: Int,
    onBowlerSelected: (Player) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Must select a bowler to proceed */ }
    ) {
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select Bowler for Over $overNumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = CricketGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Choose who will bowl this over. The previous bowler cannot bowl consecutive overs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bowlers) { bowler ->
                        val isPreviousBowler = (bowler.id == previousBowlerId) && (bowlers.size > 1)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isPreviousBowler) {
                                    onBowlerSelected(bowler)
                                },
                            color = if (isPreviousBowler) DarkSurfaceVariant.copy(alpha = 0.5f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bowler.name,
                                    color = if (isPreviousBowler) TextMuted else TextWhite,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                if (isPreviousBowler) {
                                    Text(
                                        text = "Just Bowled",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    Text(
                                        text = "Select",
                                        color = CricketGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
