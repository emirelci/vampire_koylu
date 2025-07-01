package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.Player
import com.ee.vampirkoylu.model.PlayerRole
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
                .padding(16.dp, bottom = 124.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (!revealed) {
                PassDeviceScreen(
                    activePlayer.name,
                    onReady = { revealed = true }
                )
            } else {
                Text(
                    text = accusedPlayer.name,
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Image(
                    painter = painterResource(R.drawable.execution),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )

                Spacer(Modifier.height(36.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PixelArtButton(
                        text = stringResource(id = R.string.vote_guilty),
                        onClick = {
                            revealed = false
                            onVote(true)
                        },
                        imageId = R.drawable.button_red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    PixelArtButton(
                        text = stringResource(id = R.string.vote_innocent),
                        onClick = {
                            revealed = false
                            onVote(false)
                        },
                        imageId = R.drawable.button_brown,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start =  8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun previewBox(){
    val player = Player(0, "Emir", PlayerRole.SHERIFF)

    JudgementScreen(
        player,
        accusedPlayer = player,
        {}
    )

}
