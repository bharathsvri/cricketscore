package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.BallEvent
import com.b47tech.cricketscore.core.engine.ExtraType
import com.b47tech.cricketscore.ui.theme.*

@Composable
fun ThisOverTimeline(
    deliveries: List<BallEvent>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "This Over:",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                modifier = Modifier.padding(end = 10.dp)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (deliveries.isEmpty()) {
                    Text(
                        text = "Over starting...",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    deliveries.forEach { ball ->
                        BallChip(ball = ball)
                    }
                }
            }

            // Total in this over
            val runsInOver = deliveries.sumOf { it.totalRunsOnBall }
            Text(
                text = "= $runsInOver",
                color = CricketGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun BallChip(ball: BallEvent) {
    val bgColor = when {
        ball.isWicket -> WicketRed
        ball.runsOffBat == 6 -> BoundarySix
        ball.runsOffBat == 4 -> BoundaryFour
        ball.extraType == ExtraType.WIDE || ball.extraType == ExtraType.NO_BALL -> ExtraAmber
        ball.runsOffBat == 0 && ball.extraRuns == 0 -> DotGray
        else -> CricketGreenPrimary
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ball.getShortLabel(),
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = if (ball.getShortLabel().length > 2) 9.sp else 12.sp
        )
    }
}
