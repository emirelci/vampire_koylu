package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.component.PlayerAvatar
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

@Composable
fun RoleRevealScreen(
    playerName: String,
    role: PlayerRole,
    onNext: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.night_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!revealed) {

                PassDeviceScreen(
                    playerName = playerName,
                    onReady = { revealed = true }
                )

            } else {

                BasicText(
                    text = stringResource(R.string.pass_to_header, playerName),
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = PixelFont,
                        color = shine_gold,
                        textAlign = TextAlign.Center
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                )

                Text(
                    text = stringResource(id = R.string.pass_continue),
                    fontSize = 12.sp,
                    fontFamily = PixelFont,
                    color = Beige,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                )

                PlayerAvatar(
                    role = role,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // Rol bilgisi
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).size(136.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.role_info),
                            fontSize = 16.sp,
                            fontFamily = PixelFont,
                            color = shine_gold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Text(
                            text = getRoleInfo(role),
                            fontSize = 12.sp,
                            fontFamily = PixelFont,
                            color = Beige,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                PixelArtButton(
                    text = stringResource(id = R.string.done),
                    onClick = onNext,
                    imageId = R.drawable.button_brown,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun getRoleInfo(role: PlayerRole): String {
    return stringResource(
        id = when (role) {
            PlayerRole.VAMPIRE -> R.string.vampire_info
            PlayerRole.VILLAGER -> R.string.villager_info
            PlayerRole.SHERIFF -> R.string.sheriff_info
            PlayerRole.WATCHER -> R.string.watcher_info
            PlayerRole.SERIAL_KILLER -> R.string.serial_killer_info
            PlayerRole.DOCTOR -> R.string.doctor_info
            PlayerRole.VOTE_SABOTEUR -> R.string.vote_saboteur_info
            PlayerRole.AUTOPSIR -> R.string.autopsir_info
            PlayerRole.VETERAN -> R.string.veteran_info
            PlayerRole.MADMAN -> R.string.madman_info
            PlayerRole.WIZARD -> R.string.wizard_info
        }
    )
}

@Preview(showSystemUi = true, showBackground = true, device = PIXEL_4)
@Composable
fun previewScreen() {
    RoleRevealScreen("Emir", PlayerRole.MADMAN) {

    }
}