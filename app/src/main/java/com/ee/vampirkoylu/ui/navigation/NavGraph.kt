package com.ee.vampirkoylu.ui.navigation


import BillingClientWrapper
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ee.vampirkoylu.model.GamePhase
import com.ee.vampirkoylu.StoreManager
import com.ee.vampirkoylu.ui.screens.GameSetupScreen
import com.ee.vampirkoylu.ui.screens.HomeScreen
import com.ee.vampirkoylu.ui.screens.MainScreenBackground
import com.ee.vampirkoylu.ui.screens.MeetingDayScreen
import com.ee.vampirkoylu.ui.screens.DayVoteResultScreen
import com.ee.vampirkoylu.ui.screens.GameOverScreen
import com.ee.vampirkoylu.ui.screens.JudgementResultScreen
import com.ee.vampirkoylu.ui.screens.JudgementScreen
import com.ee.vampirkoylu.ui.screens.NightActionScreen
import com.ee.vampirkoylu.ui.screens.NightResultsScreen
import com.ee.vampirkoylu.ui.screens.RoleRevealScreen
import com.ee.vampirkoylu.ui.screens.RuleScreen
import com.ee.vampirkoylu.ui.screens.VotingScreen
import com.ee.vampirkoylu.viewmodel.GameViewModel
import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

object NavGraph {

    @Composable
    fun SetupNavGraph(
        navController: NavHostController,
        billingClientWrapper: BillingClientWrapper,
        storeManager: StoreManager,
        activity: Activity,
        gameViewModel: GameViewModel = viewModel()
    ) {

        LaunchedEffect(Unit) {
            billingClientWrapper.startConnection()
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        if (!billingClientWrapper.isConnected.value) {
                            billingClientWrapper.startConnection()
                        }
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        billingClientWrapper.disconnect()
                    }

                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                MainScreenBackground {
                    HomeScreen(
                        navController = navController,
                        billingClientWrapper = billingClientWrapper,
                        storeManager = storeManager,
                        activity = activity
                    )
                }
            }

            composable(Screen.Setup.route) {
                val settings by gameViewModel.settings.collectAsState()
                val isPlusUser by storeManager.isPlusUser.collectAsState(initial = false)
                println("isPlusUser: $isPlusUser")
                GameSetupScreen(
                    settings = settings,
                    navController = navController,
                    isPlusUser = isPlusUser,
                    onSettingsChange = { playerCount, vampireCount, sheriffCount, watcherCount, serialKillerCount, doctorCount, seerCount, saboteurCount, autopsirCount, veteranCount, madmanCount, wizardCount ->
                        gameViewModel.updateSettings(
                            playerCount,
                            vampireCount,
                            sheriffCount,
                            watcherCount,
                            serialKillerCount,
                            doctorCount,
                            seerCount,
                            saboteurCount,
                            autopsirCount,
                            veteranCount,
                            madmanCount,
                            wizardCount
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
                arguments = listOf(androidx.navigation.navArgument("index") {
                    type = androidx.navigation.NavType.IntType
                })
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
                                    popUpTo(Screen.RoleReveal.createRoute(index)) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(Screen.MeetingDay.route) {
                                    popUpTo(Screen.RoleReveal.createRoute(index)) {
                                        inclusive = true
                                    }
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
                                onTargetSelected = { ids ->
                                    gameViewModel.selectNightTarget(ids)
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
                                onVote = { targetId, sab ->
                                    gameViewModel.vote(targetId, sab)

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
                            onFinish = {
                                if (accused == null) {
                                    gameViewModel.skipAccusation()
                                } else {
                                    gameViewModel.startJudgement()
                                }
                            }
                        )
                    }

                    GamePhase.JUDGEMENT -> {
                        val accused = players.find { it.id == gameState.accusedId }
                        if (accused != null) {
                            activePlayer?.let { currentPlayer ->
                                JudgementScreen(
                                    activePlayer = currentPlayer,
                                    accusedPlayer = accused,
                                    onVote = { choice ->
                                        gameViewModel.submitJudgementVote(choice)
                                    }
                                )
                            }
                        }
                    }

                    GamePhase.VOTE_RESULT -> {
                        val accused = players.find { it.id == gameState.accusedId }
                        println("accused: suçlanan $accused")
                        JudgementResultScreen(
                            accusedPlayer = accused,
                            eliminated = gameState.lastEliminated == gameState.accusedId,
                            onContinue = {
                                gameViewModel.proceed()
                                navController.navigate(Screen.Game.route) {
                                    popUpTo(Screen.Game.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    GamePhase.GAME_OVER -> {
                        LaunchedEffect(Unit) {
                            navController.navigate(Screen.GameOver.route) {
                                popUpTo(Screen.Game.route) { inclusive = true }
                            }
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

            composable(Screen.GameOver.route) {
                val gameState by gameViewModel.gameState.collectAsState()
                val isPlusUser by storeManager.isPlusUser.collectAsState(initial = false)

                gameState.gameResult?.let { result ->
                    GameOverScreen(
                        gameResult = result,
                        isPlusUser = isPlusUser,
                        onBackToMenu = {
                            gameViewModel.resetGame()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

        }
    }
} 