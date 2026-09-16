package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.core.engine.WicketType
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WicketDialog(
    striker: Player?,
    nonStriker: Player?,
    fielders: List<Player>,
    availableNextBatters: List<Player>,
    isFreeHit: Boolean,
    onDismiss: () -> Unit,
    onConfirmWicket: (
        wicketType: WicketType,
        dismissedPlayerId: String,
        fielderId: String?,
        runsCompleted: Int,
        nextBatsmanId: String?
    ) -> Unit
) {
    var selectedWicketType by remember {
        mutableStateOf(if (isFreeHit) WicketType.RUN_OUT_STRIKER else WicketType.BOWLED)
    }
    var dismissedPlayerId by remember {
        mutableStateOf(striker?.id ?: "")
    }
    var selectedFielderId by remember { mutableStateOf<String?>(null) }
    var runsCompleted by remember { mutableStateOf(0) }
    var selectedNextBatterId by remember {
        mutableStateOf(availableNextBatters.firstOrNull()?.id)
    }

    val availableWicketTypes = if (isFreeHit) {
        listOf(WicketType.RUN_OUT_STRIKER, WicketType.RUN_OUT_NON_STRIKER)
    } else {
        listOf(
            WicketType.BOWLED,
            WicketType.CAUGHT,
            WicketType.LBW,
            WicketType.RUN_OUT_STRIKER,
            WicketType.RUN_OUT_NON_STRIKER,
            WicketType.STUMPED,
            WicketType.HIT_WICKET,
            WicketType.RETIRED_HURT
        )
    }

    // Automatically synchronize dismissed player on run out selection
    LaunchedEffect(selectedWicketType) {
        if (selectedWicketType == WicketType.RUN_OUT_STRIKER) {
            dismissedPlayerId = striker?.id ?: ""
        } else if (selectedWicketType == WicketType.RUN_OUT_NON_STRIKER) {
            dismissedPlayerId = nonStriker?.id ?: ""
        } else {
            dismissedPlayerId = striker?.id ?: ""
        }
    }

    Dialog(onDismissRequest = onDismiss) {
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fall of Wicket",
                        style = MaterialTheme.typography.titleLarge,
                        color = WicketRed,
                        fontWeight = FontWeight.Bold
                    )
                    if (isFreeHit) {
                        Surface(
                            color = CricketGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "FREE HIT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = DarkBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Wicket Type Chips
                Text(
                    text = "Method of Dismissal",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextWhite
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableWicketTypes.forEach { type ->
                        val isSelected = (selectedWicketType == type)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedWicketType = type },
                            color = if (isSelected) WicketRed else DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = when (type) {
                                    WicketType.BOWLED -> "Bowled"
                                    WicketType.CAUGHT -> "Caught"
                                    WicketType.LBW -> "LBW"
                                    WicketType.RUN_OUT_STRIKER -> "Run Out (Striker)"
                                    WicketType.RUN_OUT_NON_STRIKER -> "Run Out (Non-Striker)"
                                    WicketType.STUMPED -> "Stumped"
                                    WicketType.HIT_WICKET -> "Hit Wicket"
                                    WicketType.RETIRED_HURT -> "Retired Hurt"
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Fielder Selection (for Caught, Run Out, Stumped)
                if (selectedWicketType == WicketType.CAUGHT ||
                    selectedWicketType == WicketType.STUMPED ||
                    selectedWicketType == WicketType.RUN_OUT_STRIKER ||
                    selectedWicketType == WicketType.RUN_OUT_NON_STRIKER
                ) {
                    Text(
                        text = if (selectedWicketType == WicketType.CAUGHT) "Catcher"
                        else if (selectedWicketType == WicketType.STUMPED) "Wicket Keeper"
                        else "Fielder (Thrower / Assist)",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextWhite
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fielders.forEach { fielder ->
                            val isSelected = selectedFielderId == fielder.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedFielderId = fielder.id },
                                color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = fielder.name,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = TextWhite,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Next Batsman (if available)
                if (availableNextBatters.isNotEmpty()) {
                    Text(
                        text = "Next Incoming Batsman",
                        style = MaterialTheme.typography.labelLarge,
                        color = CricketGoldLight
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableNextBatters.forEach { nextBatter ->
                            val isSelected = selectedNextBatterId == nextBatter.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedNextBatterId = nextBatter.id },
                                color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = nextBatter.name,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirmWicket(
                                selectedWicketType,
                                dismissedPlayerId,
                                selectedFielderId,
                                runsCompleted,
                                selectedNextBatterId
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WicketRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Out!", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
