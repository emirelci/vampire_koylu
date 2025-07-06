package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.Player
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold
import kotlinx.coroutines.delay

@Composable
fun DayVoteResultScreen(
    accusedPlayer: Player?,
    onFinish: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(180) }

    if (accusedPlayer != null) {
        LaunchedEffect(Unit) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            onFinish()
        }
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

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
                    text = stringResource(id = R.string.day_vote_result_title),
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    lineHeight = 36.sp,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (accusedPlayer != null) {
                    Text(
                        text = stringResource(id = R.string.accused_player, accusedPlayer.name),
                        fontSize = 20.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        modifier = Modifier.padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 36.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        modifier = Modifier.padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    PixelArtButton(
                        text = stringResource(id = R.string.start_judgement),
                        onClick = { onFinish() },
                        imageId = R.drawable.button_brown,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 264.dp)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.no_accused_player),
                        fontSize = 20.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    PixelArtButton(
                        text = stringResource(id = R.string.proceed_to_night),
                        onClick = { onFinish() },
                        imageId = R.drawable.button_brown,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun previewBoxss(){
    val player = Player(0, "Emir", PlayerRole.SHERIFF)

    DayVoteResultScreen(
        player,
        {}
    )

}
