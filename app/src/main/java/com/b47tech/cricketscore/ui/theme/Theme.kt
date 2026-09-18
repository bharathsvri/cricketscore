package com.b47tech.cricketscore.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CricketGreenLight,
    onPrimary = TextWhite,
    primaryContainer = CricketGreenPrimary,
    onPrimaryContainer = TextWhite,
    secondary = CricketGold,
    onSecondary = DarkBg,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = CricketGoldLight,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = CricketGreenPrimary,
    onPrimary = TextWhite,
    primaryContainer = CricketGreenLight,
    onPrimaryContainer = TextWhite,
    secondary = CricketGold,
    onSecondary = DarkBg,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted
)

@Composable
fun B47CricketScoreTheme(
    darkTheme: Boolean = true, // Default to rich dark stadium scoreboard theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            if (android.os.Build.VERSION.SDK_INT < 35) {
                window.statusBarColor = CricketGreenDark.toArgb()
                window.navigationBarColor = DarkBg.toArgb()
            }
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
