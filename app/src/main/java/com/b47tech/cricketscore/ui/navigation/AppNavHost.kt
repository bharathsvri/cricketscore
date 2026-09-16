package com.b47tech.cricketscore.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.b47tech.cricketscore.core.engine.CricketEngine
import com.b47tech.cricketscore.core.engine.Team
import com.b47tech.cricketscore.data.repository.CricketRepository
import com.b47tech.cricketscore.ui.screens.history.MatchHistoryScreen
import com.b47tech.cricketscore.ui.screens.history.MatchHistoryViewModel
import com.b47tech.cricketscore.ui.screens.home.HomeScreen
import com.b47tech.cricketscore.ui.screens.home.HomeViewModel
import com.b47tech.cricketscore.ui.screens.matchsetup.MatchSetupScreen
import com.b47tech.cricketscore.ui.screens.matchsetup.MatchSetupViewModel
import com.b47tech.cricketscore.ui.screens.openers.OpeningSelectionScreen
import com.b47tech.cricketscore.ui.screens.scorecard.ScorecardScreen
import com.b47tech.cricketscore.ui.screens.scorecard.ScorecardViewModel
import com.b47tech.cricketscore.ui.screens.scoring.ScoringScreen
import com.b47tech.cricketscore.ui.screens.scoring.ScoringViewModel
import com.b47tech.cricketscore.ui.screens.settings.SettingsScreen
import com.b47tech.cricketscore.ui.screens.stats.PlayerStatsScreen
import com.b47tech.cricketscore.ui.screens.stats.PlayerStatsViewModel
import com.b47tech.cricketscore.ui.screens.toss.TossScreen
import com.b47tech.cricketscore.ui.screens.toss.TossViewModel

import com.b47tech.cricketscore.data.local.SettingsRepository
import com.b47tech.cricketscore.ui.screens.settings.SettingsViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: CricketRepository,
    settingsRepository: SettingsRepository
) {
    // Shared setup state during match creation flow
    var currentTeamA by remember { mutableStateOf<Team?>(null) }
    var currentTeamB by remember { mutableStateOf<Team?>(null) }
    var currentOvers by remember { mutableStateOf(20) }
    var currentPlayersCount by remember { mutableStateOf(11) }
    var currentBallType by remember { mutableStateOf("Leather") }
    var currentTossWinnerId by remember { mutableStateOf("teamA") }
    var currentTossDecision by remember { mutableStateOf("BAT") }
    var isInnings2Setup by remember { mutableStateOf(false) }

    val matchSetupViewModel: MatchSetupViewModel = viewModel()
    val tossViewModel: TossViewModel = viewModel()
    val scoringViewModel: ScoringViewModel = viewModel(factory = ScoringViewModel.Factory(repository))

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 1. Home
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = homeViewModel,
                onStartNewMatch = {
                    navController.navigate(Screen.MatchSetup.route)
                },
                onResumeMatch = { matchId ->
                    scoringViewModel.loadMatchById(matchId)
                    navController.navigate(Screen.Scoring.route)
                },
                onViewScorecard = { matchId ->
                    navController.navigate(Screen.Scorecard.createRoute(matchId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.PlayerStats.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // 2. Match Setup
        composable(Screen.MatchSetup.route) {
            MatchSetupScreen(
                viewModel = matchSetupViewModel,
                onBack = { navController.popBackStack() },
                onProceedToToss = {
                    val (tA, tB) = matchSetupViewModel.buildTeams()
                    val s = matchSetupViewModel.uiState.value
                    currentTeamA = tA
                    currentTeamB = tB
                    currentOvers = s.overs
                    currentPlayersCount = s.playersCount
                    currentBallType = s.ballType
                    navController.navigate(Screen.Toss.route)
                }
            )
        }

        // 3. Toss
        composable(Screen.Toss.route) {
            val teamA = currentTeamA ?: return@composable
            val teamB = currentTeamB ?: return@composable
            TossScreen(
                teamA = teamA,
                teamB = teamB,
                viewModel = tossViewModel,
                onBack = { navController.popBackStack() },
                onProceedToOpeners = { winnerId, decision ->
                    currentTossWinnerId = winnerId
                    currentTossDecision = decision
                    isInnings2Setup = false
                    navController.navigate(Screen.OpeningSelection.route)
                }
            )
        }

        // 4. Openers Selection (1st or 2nd Innings)
        composable(Screen.OpeningSelection.route) {
            if (isInnings2Setup) {
                val eng = scoringViewModel.engine ?: return@composable
                OpeningSelectionScreen(
                    battingTeam = eng.bowlingFirstTeam,
                    bowlingTeam = eng.battingFirstTeam,
                    inningsNumber = 2,
                    onBack = { navController.popBackStack() },
                    onStartScoring = { strikerId, nonStrikerId, bowlerId ->
                        scoringViewModel.startSecondInnings(strikerId, nonStrikerId, bowlerId)
                        navController.navigate(Screen.Scoring.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            } else {
                val teamA = currentTeamA ?: return@composable
                val teamB = currentTeamB ?: return@composable

                // Determine batting first team
                val tossWinnerIsTeamA = (currentTossWinnerId == teamA.id)
                val battingTeam = if (currentTossDecision.equals("BAT", ignoreCase = true)) {
                    if (tossWinnerIsTeamA) teamA else teamB
                } else {
                    if (tossWinnerIsTeamA) teamB else teamA
                }
                val bowlingTeam = if (battingTeam.id == teamA.id) teamB else teamA

                OpeningSelectionScreen(
                    battingTeam = battingTeam,
                    bowlingTeam = bowlingTeam,
                    inningsNumber = 1,
                    onBack = { navController.popBackStack() },
                    onStartScoring = { strikerId, nonStrikerId, bowlerId ->
                        val engine = CricketEngine(
                            teamA = teamA,
                            teamB = teamB,
                            totalOvers = currentOvers,
                            playersPerTeam = currentPlayersCount,
                            ballType = currentBallType,
                            tossWinnerId = currentTossWinnerId,
                            tossDecision = currentTossDecision
                        )
                        engine.startFirstInnings(strikerId, nonStrikerId, bowlerId)
                        scoringViewModel.attachEngine(engine)
                        navController.navigate(Screen.Scoring.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }

        // 5. Live Scoring
        composable(Screen.Scoring.route) {
            ScoringScreen(
                viewModel = scoringViewModel,
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToScorecard = { matchId ->
                    navController.navigate(Screen.Scorecard.createRoute(matchId))
                },
                onNavigateToSecondInningsOpeners = {
                    isInnings2Setup = true
                    navController.navigate(Screen.OpeningSelection.route)
                }
            )
        }

        // 6. Scorecard
        composable(
            route = Screen.Scorecard.route,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            val scorecardViewModel: ScorecardViewModel = viewModel(factory = ScorecardViewModel.Factory(repository))
            ScorecardScreen(
                matchId = matchId,
                viewModel = scorecardViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 7. Match History
        composable(Screen.History.route) {
            val historyViewModel: MatchHistoryViewModel = viewModel(factory = MatchHistoryViewModel.Factory(repository))
            MatchHistoryScreen(
                viewModel = historyViewModel,
                onBack = { navController.popBackStack() },
                onResumeMatch = { matchId ->
                    scoringViewModel.loadMatchById(matchId)
                    navController.navigate(Screen.Scoring.route)
                },
                onViewScorecard = { matchId ->
                    navController.navigate(Screen.Scorecard.createRoute(matchId))
                }
            )
        }

        // 8. Player Career Stats
        composable(Screen.PlayerStats.route) {
            val statsViewModel: PlayerStatsViewModel = viewModel(factory = PlayerStatsViewModel.Factory(repository))
            PlayerStatsScreen(
                viewModel = statsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 9. Settings / About
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(settingsRepository))
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
