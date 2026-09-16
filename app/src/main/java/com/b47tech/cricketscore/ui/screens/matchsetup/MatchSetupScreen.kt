package com.b47tech.cricketscore.ui.screens.matchsetup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchSetupScreen(
    viewModel: MatchSetupViewModel,
    onBack: () -> Unit,
    onProceedToToss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showTeamAPlayers by remember { mutableStateOf(false) }
    var showTeamBPlayers by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Setup", fontWeight = FontWeight.Bold, color = TextWhite) },
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
                    Button(
                        onClick = {
                            if (viewModel.validate()) {
                                onProceedToToss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Proceed to Toss",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Validation error alert (if any)
            if (state.validationError != null) {
                item {
                    Surface(
                        color = WicketRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WicketRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.validationError!!,
                            color = WicketRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Match Info & Teams Card
            item {
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
                            text = "Match Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = state.matchName,
                            onValueChange = { viewModel.updateMatchName(it) },
                            label = { Text("Match Title (Optional, e.g. Sunday Derby)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CricketGold,
                                focusedLabelColor = CricketGold,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )

                        OutlinedTextField(
                            value = state.teamAName,
                            onValueChange = { viewModel.updateTeamAName(it) },
                            label = { Text("Team 1 Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CricketGold,
                                focusedLabelColor = CricketGold,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )

                        OutlinedTextField(
                            value = state.teamBName,
                            onValueChange = { viewModel.updateTeamBName(it) },
                            label = { Text("Team 2 Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CricketGold,
                                focusedLabelColor = CricketGold,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Overs Selection Card
            item {
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
                            text = "Number of Overs: ${state.overs}",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )

                        // Overs Quick Select Chips: 1, 2, 5, 10, 15, 20
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 2, 5, 10, 15, 20).forEach { overOption ->
                                val isSelected = !state.isCustomOvers && state.overs == overOption
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateOvers(overOption) },
                                    color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$overOption",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = TextWhite,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Overs Option
                        OutlinedTextField(
                            value = if (state.isCustomOvers) state.customOversText else "",
                            onValueChange = { viewModel.setCustomOvers(it) },
                            label = { Text("Custom Overs (e.g. 8, 25, 50)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CricketGold,
                                unfocusedBorderColor = DarkSurfaceVariant
                            )
                        )

                        HorizontalDivider(color = DarkSurfaceVariant)

                        Text(
                            text = "Ball Type",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Tennis", "Leather").forEach { ballType ->
                                val isSelected = state.ballType.equals(ballType, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateBallType(ballType) },
                                    color = if (isSelected) CricketGold else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ballType,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) DarkBg else TextWhite,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Team 1 Player Roster Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTeamAPlayers = !showTeamAPlayers }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${state.teamAName} Players (${state.teamAPlayers.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap to expand and edit roster",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = if (showTeamAPlayers) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = CricketGold
                            )
                        }

                        if (showTeamAPlayers) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                state.teamAPlayers.forEachIndexed { index, player ->
                                    PlayerEditorRow(
                                        player = player,
                                        onPlayerChange = { viewModel.updatePlayerA(index, it) },
                                        onDelete = { viewModel.removePlayerFromTeamA(index) },
                                        canDelete = state.teamAPlayers.size > 2
                                    )
                                }

                                Button(
                                    onClick = { viewModel.addPlayerToTeamA() },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = CricketGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Player to ${state.teamAName}", color = CricketGold)
                                }
                            }
                        }
                    }
                }
            }

            // Team 2 Player Roster Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTeamBPlayers = !showTeamBPlayers }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${state.teamBName} Players (${state.teamBPlayers.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap to expand and edit roster",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(
                                imageVector = if (showTeamBPlayers) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = CricketGold
                            )
                        }

                        if (showTeamBPlayers) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                state.teamBPlayers.forEachIndexed { index, player ->
                                    PlayerEditorRow(
                                        player = player,
                                        onPlayerChange = { viewModel.updatePlayerB(index, it) },
                                        onDelete = { viewModel.removePlayerFromTeamB(index) },
                                        canDelete = state.teamBPlayers.size > 2
                                    )
                                }

                                Button(
                                    onClick = { viewModel.addPlayerToTeamB() },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = CricketGold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Player to ${state.teamBName}", color = CricketGold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerEditorRow(
    player: PlayerInput,
    onPlayerChange: (PlayerInput) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = player.name,
                    onValueChange = { onPlayerChange(player.copy(name = it)) },
                    label = { Text("Name") },
                    modifier = Modifier.weight(2f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CricketGold,
                        unfocusedBorderColor = DarkSurface
                    )
                )

                OutlinedTextField(
                    value = player.jerseyNumber,
                    onValueChange = { onPlayerChange(player.copy(jerseyNumber = it)) },
                    label = { Text("#") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CricketGold,
                        unfocusedBorderColor = DarkSurface
                    )
                )

                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = WicketRed)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = player.isCaptain,
                    onClick = { onPlayerChange(player.copy(isCaptain = !player.isCaptain)) },
                    label = { Text(if (player.isCaptain) "Captain (C)" else "Set (C)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CricketGold,
                        selectedLabelColor = DarkBg
                    )
                )

                FilterChip(
                    selected = player.isWicketKeeper,
                    onClick = { onPlayerChange(player.copy(isWicketKeeper = !player.isWicketKeeper)) },
                    label = { Text(if (player.isWicketKeeper) "Keeper (WK)" else "Set (WK)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CricketGreenLight,
                        selectedLabelColor = TextWhite
                    )
                )
            }
        }
    }
}
