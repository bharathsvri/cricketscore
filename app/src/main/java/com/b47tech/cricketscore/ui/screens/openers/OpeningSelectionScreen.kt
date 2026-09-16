package com.b47tech.cricketscore.ui.screens.openers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.core.engine.Player
import com.b47tech.cricketscore.core.engine.Team
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OpeningSelectionScreen(
    battingTeam: Team,
    bowlingTeam: Team,
    inningsNumber: Int,
    onBack: () -> Unit,
    onStartScoring: (strikerId: String, nonStrikerId: String, bowlerId: String) -> Unit
) {
    var selectedStrikerId by remember {
        mutableStateOf(battingTeam.players.getOrNull(0)?.id ?: "")
    }
    var selectedNonStrikerId by remember {
        mutableStateOf(battingTeam.players.getOrNull(1)?.id ?: "")
    }
    var selectedBowlerId by remember {
        mutableStateOf(bowlingTeam.players.getOrNull(0)?.id ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (inningsNumber == 1) "Select Openers (1st Innings)" else "Select Openers (2nd Innings)",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                },
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
                    val isValid = selectedStrikerId.isNotEmpty() &&
                            selectedNonStrikerId.isNotEmpty() &&
                            selectedBowlerId.isNotEmpty() &&
                            selectedStrikerId != selectedNonStrikerId

                    Button(
                        onClick = {
                            if (isValid) {
                                onStartScoring(selectedStrikerId, selectedNonStrikerId, selectedBowlerId)
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Start Scoring",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Striker Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Opening Striker (*) - ${battingTeam.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        battingTeam.players.forEach { player ->
                            val isSelected = selectedStrikerId == player.id
                            val isOtherSelected = selectedNonStrikerId == player.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isOtherSelected) {
                                        selectedStrikerId = player.id
                                    },
                                color = if (isSelected) CricketGreenPrimary else if (isOtherSelected) DarkSurfaceVariant.copy(alpha = 0.4f) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = player.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = if (isOtherSelected) TextMuted else TextWhite,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Non-Striker Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Opening Non-Striker - ${battingTeam.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        battingTeam.players.forEach { player ->
                            val isSelected = selectedNonStrikerId == player.id
                            val isOtherSelected = selectedStrikerId == player.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = !isOtherSelected) {
                                        selectedNonStrikerId = player.id
                                    },
                                color = if (isSelected) CricketGreenPrimary else if (isOtherSelected) DarkSurfaceVariant.copy(alpha = 0.4f) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = player.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = if (isOtherSelected) TextMuted else TextWhite,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Opening Bowler Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Opening Bowler - ${bowlingTeam.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = CricketGold,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bowlingTeam.players.forEach { player ->
                            val isSelected = selectedBowlerId == player.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedBowlerId = player.id },
                                color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = player.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = TextWhite,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
