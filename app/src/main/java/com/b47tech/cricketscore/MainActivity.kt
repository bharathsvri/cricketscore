package com.b47tech.cricketscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.b47tech.cricketscore.ui.navigation.AppNavHost
import com.b47tech.cricketscore.ui.theme.B47CricketScoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CricketApplication
        val repository = app.repository
        val settingsRepository = app.settingsRepository

        setContent {
            val settings by settingsRepository.settings.collectAsState()
            val isDark = when (settings.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            B47CricketScoreTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    repository = repository,
                    settingsRepository = settingsRepository
                )
            }
        }
    }
}
