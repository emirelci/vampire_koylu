package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.Player
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

@Composable
fun JudgementScreen(
    activePlayer: Player,
    accusedPlayer: Player,
    onVote: (Boolean) -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.morning_vote_day_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!revealed) {
                Text(
                    text = stringResource(R.string.pass_to_header, activePlayer.name),
                    fontSize = 24.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(16.dp)
                )
                PixelArtButton(
                    text = stringResource(id = R.string.show_role),
                    onClick = { revealed = true },
                    imageId = R.drawable.button_orange,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                Text(
                    text = accusedPlayer.name,
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PixelArtButton(
                        text = stringResource(id = R.string.vote_guilty),
                        onClick = { onVote(true) },
                        imageId = R.drawable.button_red,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    PixelArtButton(
                        text = stringResource(id = R.string.vote_innocent),
                        onClick = { onVote(false) },
                        imageId = R.drawable.button_brown,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
