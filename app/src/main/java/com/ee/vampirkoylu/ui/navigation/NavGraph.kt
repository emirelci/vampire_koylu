package com.ee.vampirkoylu.ui.navigation


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ee.vampirkoylu.ui.screens.GameSetupScreen
import com.ee.vampirkoylu.ui.screens.HomeScreen
import com.ee.vampirkoylu.ui.screens.MainScreenBackground
import com.ee.vampirkoylu.ui.screens.RoleRevealScreen
import com.ee.vampirkoylu.ui.screens.RuleScreen
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
                                navController.navigate(Screen.Game.route) {
                                    popUpTo(Screen.RoleReveal.createRoute(index)) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
            
            composable(Screen.Game.route) {
                // Oyun ekranı henüz oluşturulmadı
                Text("Oyun Ekranı", color = Color.White)
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