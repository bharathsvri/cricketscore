package com.b47tech.cricketscore.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.b47tech.cricketscore.ui.components.AdBanner
import com.b47tech.cricketscore.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Rules", fontWeight = FontWeight.Bold, color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CricketGreenDark)
            )
        },
        bottomBar = {
            AdBanner()
        },
        containerColor = DarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // App Identity Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = "App Icon",
                        tint = CricketGold,
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        text = "B47 Cricket Score",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Version 1.0.0 (Production Release)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Surface(
                        color = CricketGreenPrimary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Developed by B47 Tech",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 1. Scoring Rules Configuration
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Scoring Rules", tint = CricketGold)
                        Text(
                            text = "Cricket Scoring Rules",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    SettingsSwitchRow(
                        title = "Wide Deliveries",
                        subtitle = "Allow recording Wides with +1 penalty",
                        checked = settings.isWideEnabled,
                        onCheckedChange = { viewModel.setWideEnabled(it) }
                    )

                    HorizontalDivider(color = DarkSurfaceVariant)

                    SettingsSwitchRow(
                        title = "No Ball Deliveries",
                        subtitle = "Allow recording No Balls with +1 penalty",
                        checked = settings.isNoBallEnabled,
                        onCheckedChange = { viewModel.setNoBallEnabled(it) }
                    )

                    HorizontalDivider(color = DarkSurfaceVariant)

                    SettingsSwitchRow(
                        title = "Free Hit Rule",
                        subtitle = "Award Free Hit on next legal delivery after a No Ball",
                        checked = settings.isFreeHitEnabled,
                        onCheckedChange = { viewModel.setFreeHitEnabled(it) }
                    )

                    HorizontalDivider(color = DarkSurfaceVariant)

                    SettingsSwitchRow(
                        title = "Byes",
                        subtitle = "Record runs scored off byes",
                        checked = settings.isByeEnabled,
                        onCheckedChange = { viewModel.setByeEnabled(it) }
                    )

                    HorizontalDivider(color = DarkSurfaceVariant)

                    SettingsSwitchRow(
                        title = "Leg Byes",
                        subtitle = "Record runs scored off leg byes",
                        checked = settings.isLegByeEnabled,
                        onCheckedChange = { viewModel.setLegByeEnabled(it) }
                    )
                }
            }

            // 2. Appearance Configuration
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Appearance", tint = CricketGold)
                        Text(
                            text = "Appearance",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "DARK" to "Dark",
                            "LIGHT" to "Light",
                            "SYSTEM" to "System"
                        ).forEach { (mode, label) ->
                            val isSelected = settings.themeMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setThemeMode(mode) },
                                color = if (isSelected) CricketGreenPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = TextWhite,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Feedback (Sound & Vibration)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Feedback", tint = CricketGold)
                        Text(
                            text = "Sound & Vibration Feedback",
                            style = MaterialTheme.typography.titleMedium,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    SettingsSwitchRow(
                        title = "Sound Effects",
                        subtitle = "Audio cues on boundaries and wickets",
                        checked = settings.isSoundEnabled,
                        onCheckedChange = { viewModel.setSoundEnabled(it) }
                    )

                    HorizontalDivider(color = DarkSurfaceVariant)

                    SettingsSwitchRow(
                        title = "Haptic Vibration",
                        subtitle = "Tactile feedback when tapping scoring buttons",
                        checked = settings.isVibrationEnabled,
                        onCheckedChange = { viewModel.setVibrationEnabled(it) }
                    )
                }
            }

            // 4. Privacy & Data Safety
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Privacy", tint = CricketGreenLight)
                        Text(
                            text = "Privacy & Offline Promise",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "All match data and statistics are stored 100% locally on your device. B47 Cricket Score never transmits your personal data or match scores to external servers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = CricketGreenPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}
