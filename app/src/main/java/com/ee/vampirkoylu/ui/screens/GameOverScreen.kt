package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.GameResult
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

@Composable
fun GameOverScreen(
    gameResult: GameResult,
    onBackToMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.morning_vote_day_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.game_over),
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val winTextId = when (gameResult.winningRole) {
                    PlayerRole.VAMPIRE -> R.string.vampires_win
                    PlayerRole.SERIAL_KILLER -> R.string.serial_killer_win
                    else -> R.string.villagers_win
                }

                Text(
                    text = stringResource(id = winTextId),
                    fontSize = 20.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                val survivors = gameResult.alivePlayers.joinToString(", ") { it.name }
                if (survivors.isNotBlank()) {
                    Text(
                        text = stringResource(id = R.string.surviving_players, survivors),
                        fontSize = 18.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }

                PixelArtButton(
                    text = stringResource(id = R.string.back_to_menu),
                    onClick = onBackToMenu,
                    imageId = R.drawable.button_brown,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 200.dp)
                )
            }
        }
    }
}