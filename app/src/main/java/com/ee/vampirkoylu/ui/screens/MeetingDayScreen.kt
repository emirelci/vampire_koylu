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
fun MeetingDayScreen(
    onFinish: () -> Unit,
    players: List<Player>,
    currentDay: Int,
    deadPlayers: List<Player> = emptyList()
) {
    // İlk gün 60 saniye, sonraki günler 5 dakika (300 saniye)
    val initialTime = if (currentDay == 1) 60 else 300
    var timeLeft by remember { mutableStateOf(initialTime) }
    var skipped by remember { mutableStateOf(false) }

    val isFirstDay = currentDay == 1

    LaunchedEffect(Unit) {
        // Her gün için geri sayım
        while (timeLeft > 0 && !skipped) {
            delay(1000)
            timeLeft--
        }
        if (!skipped) {
            onFinish()
        }
    }

    // Zamanı dakika:saniye formatında göstermek için yardımcı fonksiyon
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return if (minutes > 0) {
            String.format("%d:%02d", minutes, remainingSeconds)
        } else {
            seconds.toString()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.morning_day_bg),
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
                if (isFirstDay) {
                    // İlk gün ekranı - 60 saniye geri sayım
                    Text(
                        text = stringResource(id = R.string.meeting_day_title),
                        fontSize = 28.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.countdown_hint),
                        fontSize = 14.sp,
                        fontFamily = PixelFont,
                        color = Color(0xFFF0E68C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 48.sp,
                        fontFamily = PixelFont,
                        color = Color(0xFFF0E68C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    PixelArtButton(
                        text = stringResource(id = R.string.skip_meeting),
                        onClick = {
                            skipped = true
                            onFinish()
                        },
                        imageId = R.drawable.button_brown,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                } else {
                    // Sonraki günler - 5 dakika geri sayım + ölenleri göster
                    Text(
                        text = stringResource(id = R.string.day_started),
                        fontSize = 28.sp,
                        fontFamily = PixelFont,
                        lineHeight = 36.sp,
                        color = shine_gold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Geri sayım
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 36.sp,
                        fontFamily = PixelFont,
                        color = Color(0xFFF0E68C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ölen oyuncular bilgisi
                    if (deadPlayers.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_one_died),
                            fontSize = 18.sp,
                            fontFamily = PixelFont,
                            color = Color(0xFFF0E68C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else if (deadPlayers.size == 1) {
                        Text(
                            text = stringResource(id = R.string.player_died_at_night, deadPlayers.first().name),
                            fontSize = 18.sp,
                            fontFamily = PixelFont,
                            color = Color(0xFFF0E68C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else {
                        // Birden fazla kişi ölmüşse
                        val deadNames = deadPlayers.joinToString(", ") { it.name }
                        Text(
                            text = stringResource(id = R.string.multiple_players_died, deadNames),
                            fontSize = 18.sp,
                            fontFamily = PixelFont,
                            color = Color(0xFFF0E68C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }

                    // Oylamaya geç butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.proceed_to_voting),
                        onClick = {
                            skipped = true
                            onFinish()
                        },
                        imageId = R.drawable.button_brown,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun previewBoxx(){
    val players = listOf(
        Player(id = 1, name = "Emir", role = PlayerRole.VILLAGER, isAlive = true),
        Player(id = 2, name = "Ayşe", role = PlayerRole.SHERIFF, isAlive = true),
        Player(id = 3, name = "Mehmet", role = PlayerRole.VAMPIRE, isAlive = false),
    )
    MeetingDayScreen(
        {},
        players,
        currentDay = 1,
    )

}