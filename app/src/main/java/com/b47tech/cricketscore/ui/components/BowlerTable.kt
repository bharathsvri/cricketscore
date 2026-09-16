package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.BowlerStats
import com.b47tech.cricketscore.ui.theme.*
import java.util.Locale

@Composable
fun BowlerTable(
    bowler: BowlerStats?,
    onChangeBowler: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "Bowler",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "O",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "M",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "R",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "W",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "ECON",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.8f)
                )
            }

            HorizontalDivider(color = DarkSurfaceVariant, thickness = 1.dp)

            if (bowler != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bowler.playerName,
                        color = CricketGreenLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.8f)
                    )
                    Text(
                        text = bowler.oversString,
                        color = TextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "${bowler.maidens}",
                        color = TextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(0.5f)
                    )
                    Text(
                        text = "${bowler.runsConceded}",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "${bowler.wickets}",
                        color = WicketRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f", bowler.economyRate),
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(0.8f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No bowler selected",
                        color = CricketGold,
                        fontSize = 13.sp
                    )
                    TextButton(onClick = onChangeBowler) {
                        Text(text = "Select Bowler", color = CricketGoldLight)
                    }
                }
            }
        }
    }
}
