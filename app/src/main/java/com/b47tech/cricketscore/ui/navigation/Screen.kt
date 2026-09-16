package com.b47tech.cricketscore.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MatchSetup : Screen("match_setup")
    object Toss : Screen("toss")
    object OpeningSelection : Screen("opening_selection")
    object Scoring : Screen("scoring")
    object Scorecard : Screen("scorecard/{matchId}") {
        fun createRoute(matchId: String) = "scorecard/$matchId"
    }
    object History : Screen("history")
    object PlayerStats : Screen("player_stats")
    object Settings : Screen("settings")
}
