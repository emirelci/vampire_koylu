package com.ee.vampirkoylu.ui.navigation


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.GamePhase
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.screens.GameSetupScreen
import com.ee.vampirkoylu.ui.screens.HomeScreen
import com.ee.vampirkoylu.ui.screens.MainScreenBackground
import com.ee.vampirkoylu.ui.screens.MeetingDayScreen
import com.ee.vampirkoylu.ui.screens.DayVoteResultScreen
import com.ee.vampirkoylu.ui.screens.JudgementScreen
import com.ee.vampirkoylu.ui.screens.NightActionScreen
import com.ee.vampirkoylu.ui.screens.NightResultsScreen
import com.ee.vampirkoylu.ui.screens.RoleRevealScreen
import com.ee.vampirkoylu.ui.screens.RuleScreen
import com.ee.vampirkoylu.ui.screens.VotingScreen
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold
import com.ee.vampirkoylu.viewmodel.GameViewModel

object NavGraph {
    
    @Composable
    fun SetupNavGraph(
        navController: NavHostController,
        gameViewModel: GameViewModel = viewModel()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                MainScreenBackground {
                    HomeScreen(navController = navController)
                }
            }
            
            composable(Screen.Setup.route) {
                val settings by gameViewModel.settings.collectAsState()
                GameSetupScreen(
                    settings = settings,
                    navController = navController,
                    onSettingsChange = { playerCount, vampireCount, sheriffCount, watcherCount, serialKillerCount, doctorCount ->
                        gameViewModel.updateSettings(
                            playerCount, 
                            vampireCount, 
                            sheriffCount, 
                            watcherCount, 
                            serialKillerCount, 
                            doctorCount
                        )
                    },
                    onStartGame = { playerNames ->
                        gameViewModel.startGame(playerNames)
                        navController.navigate(Screen.RoleReveal.createRoute(0)) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.RoleReveal.route,
                arguments = listOf(androidx.navigation.navArgument("index") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index") ?: 0
                val players by gameViewModel.players.collectAsState()
                val player = players.getOrNull(index)
                if (player != null) {
                    RoleRevealScreen(
                        playerName = player.name,
                        role = player.role,
                        onNext = {
                            if (index + 1 < players.size) {
                                navController.navigate(Screen.RoleReveal.createRoute(index + 1)) {
                                    popUpTo(Screen.RoleReveal.createRoute(index)) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.MeetingDay.route) {
                                    popUpTo(Screen.RoleReveal.createRoute(index)) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }

            composable(Screen.MeetingDay.route) {
                val players by gameViewModel.players.collectAsState()
                val gameState by gameViewModel.gameState.collectAsState()
                val deadPlayers by gameViewModel.deadPlayers.collectAsState()
                
                MeetingDayScreen(
                    onFinish = {
                        // İlk gün için direkt gece fazına geç
                        println("CurrentDay: " + gameState.currentDay)
                        if (gameState.currentDay == 1) {
                            // İlk gün sonrası direkt gece fazına geç (oylama yok)
                            gameViewModel.proceed() // Gece fazına geç
                            navController.navigate(Screen.Game.route) {
                                popUpTo(Screen.MeetingDay.route) { inclusive = true }
                            }
                        } else {
                            // Sonraki günler için oylamaya geç
                            gameViewModel.proceedToVoting()
                            navController.navigate(Screen.Game.route) {
                                popUpTo(Screen.MeetingDay.route) { inclusive = true }
                            }
                        }
                    },
                    players = players,
                    currentDay = gameState.currentDay,
                    deadPlayers = deadPlayers
                )
            }
            
            composable(Screen.Game.route) {
                val activePlayer by gameViewModel.activePlayer.collectAsState()
                val players by gameViewModel.players.collectAsState()
                val gameState by gameViewModel.gameState.collectAsState()
                
                // Oyun fazına göre farklı ekranlar göster
                when (gameState.currentPhase) {
                    GamePhase.NIGHT -> {
                        // Aktif oyuncu null olmadığında ekranı göster
                        activePlayer?.let { currentPlayer ->
                            NightActionScreen(
                                activePlayer = currentPlayer,
                                players = players,
                                onTargetSelected = { targetId ->
                                    // Hedef seçildiğinde GameViewModel'e bildir
                                    gameViewModel.selectNightTarget(targetId)
                                }
                            )
                        } ?: Text(
                            "Gece aksiyonu bekleniyor...",
                            color = Color.White
                        )
                    }
                    GamePhase.NIGHT_RESULT -> {
                        // Gece sonuçları gösteriliyor
                        activePlayer?.let { currentPlayer ->
                            NightResultsScreen(
                                player = currentPlayer,
                                allPlayers = players,
                                gameState = gameState,
                                onContinue = {
                                    // Sıradaki oyuncuya geç veya gündüz fazına geç
                                    gameViewModel.proceedToNextNightResult()
                                    
                                    // Faz değişimini tekrar kontrol et - lazy state collection
                                    val updatedGameState = gameViewModel.gameState.value
                                    if (updatedGameState.currentPhase == GamePhase.DAY) {
                                        navController.navigate(Screen.MeetingDay.route) {
                                            popUpTo(Screen.Game.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        } ?: Text(
                            "Gece sonuçları bekleniyor...",
                            color = Color.White
                        )
                    }
                    GamePhase.VOTING -> {
                        // Oylama ekranı
                        activePlayer?.let { currentPlayer ->
                            VotingScreen(
                                activePlayer = currentPlayer,
                                players = players,
                                onVote = { targetId ->
                                    gameViewModel.vote(targetId)

                                    // Oylama fazı değiştiyse sonuçlara git
                                    if (gameState.currentPhase == GamePhase.VOTE_RESULT) {
                                        navController.navigate(Screen.MeetingDay.route) {
                                            popUpTo(Screen.Game.route) { inclusive = true }
                                        }
                                    }
                                },
                                onSkipVote = {
                                    gameViewModel.skipVote()

                                    // Oylama fazı değiştiyse sonuçlara git
                                    if (gameState.currentPhase == GamePhase.VOTE_RESULT) {
                                        navController.navigate(Screen.MeetingDay.route) {
                                            popUpTo(Screen.Game.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        } ?: Text(
                            "Oylama bekleniyor...",
                            color = Color.White
                        )
                    }
                    GamePhase.DAY_VOTE_RESULT -> {
                        val accused = players.find { it.id == gameState.accusedId }
                        DayVoteResultScreen(
                            accusedPlayer = accused,
                            onFinish = { gameViewModel.startJudgement() }
                        )
                    }
                    GamePhase.JUDGEMENT -> {
                        val accused = players.find { it.id == gameState.accusedId }
                        if (accused != null && activePlayer != null) {
                            JudgementScreen(
                                activePlayer = activePlayer,
                                accusedPlayer = accused,
                                onVote = { choice ->
                                    gameViewModel.submitJudgementVote(choice)
                                    if (gameState.currentPhase == GamePhase.VOTE_RESULT) {
                                        navController.navigate(Screen.MeetingDay.route) {
                                            popUpTo(Screen.Game.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    else -> {
                        // Diğer fazlar için gece aksiyonu gösterme
                        Text(
                            "Oyun devam ediyor...",
                            color = Color.White
                        )
                    }
                }
            }
            
            composable(Screen.Rules.route) {
                // Kurallar ekranı
                MainScreenBackground {
                    RuleScreen(navController)
                }
            }
        }
    }
} 