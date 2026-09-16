package com.b47tech.cricketscore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
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
import com.b47tech.cricketscore.ui.theme.*

@Composable
fun ScoringKeypad(
    onRunClick: (Int) -> Unit,
    onWideClick: (extraRuns: Int) -> Unit,
    onNoBallClick: (runsOffBat: Int, extraRuns: Int) -> Unit,
    onByeClick: (runs: Int, isLegBye: Boolean) -> Unit,
    onWicketClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWideDialog by remember { mutableStateOf(false) }
    var showNoBallDialog by remember { mutableStateOf(false) }
    var showByeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 0, 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreButton(
                label = "0",
                subLabel = "Dot",
                bgColor = DarkSurfaceVariant,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(0) }
            )
            ScoreButton(
                label = "1",
                bgColor = CricketGreenDark,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(1) }
            )
            ScoreButton(
                label = "2",
                bgColor = CricketGreenDark,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(2) }
            )
            ScoreButton(
                label = "3",
                bgColor = CricketGreenDark,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(3) }
            )
        }

        // Row 2: 4 (Four), 6 (Six), OUT (Wicket), UNDO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreButton(
                label = "4",
                subLabel = "Four",
                bgColor = BoundaryFour,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(4) }
            )
            ScoreButton(
                label = "6",
                subLabel = "Six",
                bgColor = BoundarySix,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { onRunClick(6) }
            )
            ScoreButton(
                label = "OUT",
                subLabel = "Wicket",
                bgColor = WicketRed,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = onWicketClick
            )
            ScoreButton(
                label = "UNDO",
                icon = Icons.AutoMirrored.Filled.Undo,
                bgColor = DarkSurfaceVariant,
                textColor = CricketGold,
                modifier = Modifier.weight(1f),
                onClick = onUndoClick
            )
        }

        // Row 3: Extras (WD, NB, BYE, LB)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScoreButton(
                label = "WD",
                subLabel = "Wide",
                bgColor = ExtraAmber,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { showWideDialog = true }
            )
            ScoreButton(
                label = "NB",
                subLabel = "No Ball",
                bgColor = ExtraAmber,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { showNoBallDialog = true }
            )
            ScoreButton(
                label = "BYE",
                bgColor = DarkSurfaceVariant,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = { showByeDialog = true }
            )
            ScoreButton(
                label = "LB",
                subLabel = "Leg Bye",
                bgColor = DarkSurfaceVariant,
                textColor = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = {
                    // Quick 1 Leg Bye default or open dialog
                    onByeClick(1, true)
                }
            )
        }
    }

    // Wide Custom Runs Dialog
    if (showWideDialog) {
        Dialog(onDismissRequest = { showWideDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Wide Delivery Extras",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total wide runs to award (including 1 penalty):",
                        color = TextMuted,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 2, 3, 5).forEach { totalRuns ->
                            Button(
                                onClick = {
                                    onWideClick(totalRuns - 1)
                                    showWideDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ExtraAmber)
                            ) {
                                Text(text = "$totalRuns", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // No Ball Custom Runs Dialog
    if (showNoBallDialog) {
        Dialog(onDismissRequest = { showNoBallDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No Ball (+1 penalty)",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Runs scored by batter off No Ball:",
                        color = TextMuted,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0, 1, 2, 4, 6).forEach { offBat ->
                            Button(
                                onClick = {
                                    onNoBallClick(offBat, 0)
                                    showNoBallDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (offBat == 4) BoundaryFour else if (offBat == 6) BoundarySix else ExtraAmber
                                )
                            ) {
                                Text(text = if (offBat == 0) "Nb" else "Nb+$offBat", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Bye Custom Dialog
    if (showByeDialog) {
        Dialog(onDismissRequest = { showByeDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Byes Scored",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 2, 3, 4).forEach { runs ->
                            Button(
                                onClick = {
                                    onByeClick(runs, false)
                                    showByeDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Text(text = "$runs", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreButton(
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = if (label.length > 3) 12.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    lineHeight = 10.sp
                )
            }
        }
    }
}
