package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.theme.Gold
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

@Composable
fun PlayerAvatar(
    role: PlayerRole,
    modifier: Modifier = Modifier,
    showRole: Boolean = true
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Çerçeve
        Image(
            painter = painterResource(id = R.drawable.player_avatar_empty),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // "ROLÜN" yazısı
            Text(
                text = stringResource(id = R.string.your_role),
                fontFamily = PixelFont,
                fontSize = 18.sp,
                color = shine_gold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            // Rol görseli
            if (showRole) {
                Box(
                    modifier = Modifier
                        .width(85.dp)
                        .height(85.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (role) {
                        PlayerRole.VAMPIRE -> {
                            Image(
                                painter = painterResource(id = R.drawable.vampir_logo),
                                contentDescription = stringResource(id = R.string.vampire),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                        PlayerRole.VILLAGER -> {
                            Image(
                                painter = painterResource(id = R.drawable.villager4),
                                contentDescription = stringResource(id = R.string.villager),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                        PlayerRole.SHERIFF -> {
                            Image(
                                painter = painterResource(id = R.drawable.sheriff1),
                                contentDescription = stringResource(id = R.string.sheriff),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                        PlayerRole.WATCHER -> {
                            Image(
                                painter = painterResource(id = R.drawable.watcher1),
                                contentDescription = stringResource(id = R.string.watcher),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                        PlayerRole.SERIAL_KILLER -> {
                            Image(
                                painter = painterResource(id = R.drawable.serial_killer1),
                                contentDescription = stringResource(id = R.string.serial_killer),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.DOCTOR -> {
                            Image(
                                painter = painterResource(id = R.drawable.doctor1),
                                contentDescription = stringResource(id = R.string.doctor),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.SEER -> {
                            Image(
                                painter = painterResource(id = R.drawable.kahin),
                                contentDescription = stringResource(id = R.string.seer),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.AUTOPSIR -> {
                            Image(
                                painter = painterResource(id = R.drawable.otopsier),
                                contentDescription = stringResource(id = R.string.autopsir),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.VETERAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.nobetci),
                                contentDescription = stringResource(id = R.string.veteran),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.WIZARD -> {
                            Image(
                                painter = painterResource(id = R.drawable.transporter),
                                contentDescription = stringResource(id = R.string.wizard),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.MADMAN -> {
                            Image(
                                painter = painterResource(id = R.drawable.deli),
                                contentDescription = stringResource(id = R.string.madman),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }

                        PlayerRole.VOTE_SABOTEUR-> {
                            Image(
                                painter = painterResource(id = R.drawable.sahtekar),
                                contentDescription = stringResource(id = R.string.vote_saboteur),
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                    }
                }
            }
            
            // Rol adı
            if (showRole) {
                Text(
                    text = when (role) {
                        PlayerRole.VAMPIRE -> stringResource(id = R.string.vampire)
                        PlayerRole.VILLAGER -> stringResource(id = R.string.villager)
                        PlayerRole.SHERIFF -> stringResource(id = R.string.sheriff)
                        PlayerRole.WATCHER -> stringResource(id = R.string.watcher)
                        PlayerRole.SERIAL_KILLER -> stringResource(id = R.string.serial_killer)
                        PlayerRole.DOCTOR -> stringResource(id = R.string.doctor)
                        PlayerRole.SEER -> stringResource(id = R.string.seer)
                        PlayerRole.VOTE_SABOTEUR -> stringResource(id = R.string.vote_saboteur)
                        PlayerRole.AUTOPSIR -> stringResource(id = R.string.autopsir)
                        PlayerRole.VETERAN -> stringResource(id = R.string.veteran)
                        PlayerRole.MADMAN -> stringResource(id = R.string.madman)
                        PlayerRole.WIZARD -> stringResource(id = R.string.wizard)
                    },
                    fontFamily = PixelFont,
                    fontSize = 16.sp,
                    color = when (role) {
                        PlayerRole.VAMPIRE -> Color.Red
                        PlayerRole.SERIAL_KILLER -> Color.Red
                        else -> shine_gold
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}