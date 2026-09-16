package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.BatterStats
import com.b47tech.cricketscore.ui.theme.*
import java.util.Locale

@Composable
fun BatsmenTable(
    striker: BatterStats?,
    nonStriker: BatterStats?,
    onSwapStrike: () -> Unit,
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
                    text = "Batter",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "R",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "B",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.6f)
                )
                Text(
                    text = "4s",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "6s",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "SR",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.8f)
                )

                // Swap Strike Icon Button
                IconButton(
                    onClick = onSwapStrike,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Strike",
                        tint = CricketGold
                    )
                }
            }

            HorizontalDivider(color = DarkSurfaceVariant, thickness = 1.dp)

            // Striker Row
            if (striker != null) {
                BatterRow(batter = striker, isStriker = true)
            } else {
                Text(
                    text = "Select Striker...",
                    color = CricketGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Non-Striker Row
            if (nonStriker != null) {
                BatterRow(batter = nonStriker, isStriker = false)
            } else {
                Text(
                    text = "Select Non-Striker...",
                    color = CricketGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BatterRow(
    batter: BatterStats,
    isStriker: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = batter.playerName,
                color = if (isStriker) CricketGoldLight else TextWhite,
                fontSize = 14.sp,
                fontWeight = if (isStriker) FontWeight.Bold else FontWeight.Medium
            )
            if (isStriker) {
                Text(
                    text = " *",
                    color = CricketGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Text(
            text = "${batter.runs}",
            color = if (isStriker) CricketGoldLight else TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = "${batter.ballsFaced}",
            color = TextWhite,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = "${batter.fours}",
            color = TextWhite,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = "${batter.sixes}",
            color = TextWhite,
            fontSize = 13.sp,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = String.format(Locale.US, "%.1f", batter.strikeRate),
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.8f)
        )

        Spacer(modifier = Modifier.width(24.dp))
    }
}
