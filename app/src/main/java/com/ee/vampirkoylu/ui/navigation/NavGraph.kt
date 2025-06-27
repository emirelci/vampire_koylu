package com.ee.vampirkoylu.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.screens.GameSetupScreen
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.screens.HomeScreen
import com.ee.vampirkoylu.ui.screens.MainScreenBackground
import com.ee.vampirkoylu.ui.theme.PixelFont
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
                    onSettingsChange = { playerCount, vampireCount ->
                        gameViewModel.updateSettings(playerCount, vampireCount)
                    },
                    onStartGame = { playerNames ->
                        gameViewModel.startGame(playerNames)
                        navController.navigate(Screen.Game.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Game.route) {
                // Oyun ekranı henüz oluşturulmadı
                Text("Oyun Ekranı", color = Color.White)
            }
            
            composable(Screen.Rules.route) {
                // Kurallar ekranı
                MainScreenBackground {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.rules_title),
                            fontSize = 36.sp,
                            fontFamily = PixelFont,
                            color = Color(0xFFF0E68C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.rules_text),
                                fontSize = 16.sp,
                                fontFamily = PixelFont,
                                color = Color(0xFFF0E68C),
                                overflow = TextOverflow.Visible,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        PixelArtButton(
                            text = stringResource(id = R.string.back_to_menu),
                            onClick = { navController.navigateUp() },
                            imageId = R.drawable.button_brown,
                            modifier = Modifier.padding(vertical = 16.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
} 