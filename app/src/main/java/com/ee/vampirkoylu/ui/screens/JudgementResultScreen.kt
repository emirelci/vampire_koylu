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

@Composable
fun JudgementResultScreen(
    accusedPlayer: Player?,
    eliminated: Boolean,
    onContinue: () -> Unit
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
                    text = stringResource(id = R.string.judgement_result_title),
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    lineHeight = 36.sp,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                accusedPlayer?.let { player ->
                    Text(
                        text = if (eliminated) {
                            stringResource(id = R.string.judgement_result_guilty, player.name)
                        } else {
                            stringResource(id = R.string.judgement_result_innocent, player.name)
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }


                PixelArtButton(
                    text = stringResource(id = R.string.continue_game),
                    onClick = onContinue,
                    imageId = R.drawable.button_brown,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 286.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun previewBoxs(){
    val player = Player(0, "Emir", PlayerRole.SHERIFF)

    JudgementResultScreen(
        player,
        eliminated = false,
        {}
    )

}