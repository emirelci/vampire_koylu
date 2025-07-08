package com.ee.vampirkoylu.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.GameResult
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun GameOverScreen(
    gameResult: GameResult,
    isPlusUser: Boolean,
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current
    //Gerçek canlı reklam kimliğim: ca-app-pub-3681703552429739/9932303057
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    var adShown by remember { mutableStateOf(false) }

    LaunchedEffect(isPlusUser) {
        if (!isPlusUser) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        val activity = context as? Activity
                        if (activity != null && !adShown) {
                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    interstitialAd = null
                                    adShown = true
                                }
                            }
                            ad.show(activity)
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null
                    }
                }
            )
        }
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
                    text = stringResource(id = R.string.game_over),
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
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
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                val survivors = gameResult.alivePlayers.joinToString(", ") { it.name }
                if (survivors.isNotBlank()) {
                    Text(
                        text = stringResource(id = R.string.surviving_players, survivors),
                        fontSize = 18.sp,
                        fontFamily = PixelFont,
                        color = shine_gold,
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = TextAlign.Center
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