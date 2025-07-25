package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.Player
import com.ee.vampirkoylu.model.PlayerRole
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.component.PlayerSelectionItem
import com.ee.vampirkoylu.ui.component.SelectionState
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

/**
 * Gece eylemini gerçekleştirmek için kullanılan ekran
 *
 * @param activePlayer Aktif oyuncu
 * @param players Tüm oyuncu listesi
 * @param onTargetSelected Hedef oyuncu seçildiğinde çağrılacak fonksiyon
 */
@Composable
fun NightActionScreen(
    activePlayer: Player,
    players: List<Player>,
    displayRole: PlayerRole = activePlayer.role,
    onTargetSelected: (List<Int>) -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    var selectedPlayerIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    
    // Hedef seçildiğinde çağrılacak fonksiyon
    val handleTargetSelected = { ids: List<Int> ->
        // Önce hedefi bildir
        onTargetSelected(ids)
        // Sonra revealed durumunu false yap ki bir sonraki oyuncu için "Sıra:Oyuncu" ekranı gösterilsin
        revealed = false
        // Seçilen oyuncuyu sıfırla
        selectedPlayerIds = emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan
        Image(
            painter = painterResource(id = R.drawable.night_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // İçerik
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!revealed) {
                PassDeviceScreen(
                    playerName = activePlayer.name,
                    onReady = { revealed = true }
                )
            } else {
                // Ana gece aksiyon ekranı - Oyuncu seçimi
                // Başlık
                Text(
                    text = activePlayer.name,
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = getRoleName(displayRole),
                    fontSize = 20.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Rol bazlı bilgi metni
                Text(
                    text = getRoleNightTip(displayRole),
                    fontSize = 16.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (displayRole == PlayerRole.VILLAGER) {
                    // Köylü için görünüm - Grid gösterme, sadece devam et butonu göster
                    Spacer(modifier = Modifier.weight(1f))
                    // Devam et butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.continue_game),
                        onClick = { handleTargetSelected(emptyList()) }, // Köylüler için -1 gönder
                        imageId = R.drawable.button_red,
                        fontSize = 20.sp
                    )
                } else {
                    when (displayRole) {
                        PlayerRole.VETERAN -> {
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PixelArtButton(
                                    text = stringResource(id = R.string.veteran_wake_up),
                                    onClick = { handleTargetSelected(listOf(activePlayer.id)) },
                                    imageId = R.drawable.button_red,
                                    fontSize = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                PixelArtButton(
                                    text = stringResource(id = R.string.veteran_sleep),
                                    onClick = { handleTargetSelected(emptyList()) },
                                    imageId = R.drawable.button_brown,
                                    fontSize = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        else -> {
                            val showPlayers = when (displayRole) {
                                PlayerRole.AUTOPSIR -> players.filter { !it.isAlive }
                                else -> players.filter { it.id != activePlayer.id }
                            }

                            if (displayRole == PlayerRole.AUTOPSIR && showPlayers.isEmpty()) {
                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = stringResource(id = R.string.no_dead_players),
                                    fontSize = 18.sp,
                                    fontFamily = PixelFont,
                                    color = shine_gold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )

                                PixelArtButton(
                                    text = stringResource(id = R.string.continue_game),
                                    onClick = { handleTargetSelected(emptyList()) },
                                    imageId = R.drawable.button_red,
                                    fontSize = 20.sp
                                )
                            }else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    items(showPlayers) { player ->
                                        val selected = selectedPlayerIds.contains(player.id)
                                        PlayerSelectionItem(
                                            name = player.name,
                                            selectionState = if (selected) SelectionState.VOTE else SelectionState.NONE,
                                            isAlive = player.isAlive,
                                            allowDeadSelection = displayRole == PlayerRole.AUTOPSIR,
                                            onSelect = {
                                                if (player.isAlive || displayRole == PlayerRole.AUTOPSIR) {
                                                    selectedPlayerIds =
                                                        if (selected) selectedPlayerIds - player.id else {
                                                            if (displayRole == PlayerRole.WIZARD) {
                                                                if (selectedPlayerIds.size < 2) selectedPlayerIds + player.id else selectedPlayerIds
                                                            } else {
                                                                listOf(player.id)
                                                            }
                                                        }
                                                }
                                            }
                                        )
                                    }
                                }

                                PixelArtButton(
                                    text = stringResource(id = R.string.confirm),
                                    onClick = { handleTargetSelected(selectedPlayerIds) },
                                    imageId = R.drawable.button_red,
                                    modifier = Modifier.padding(top = 24.dp),
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Role göre görüntülenecek rol adını döndürür
 */
@Composable
private fun getRoleName(role: PlayerRole): String {
    val stringResId = when (role) {
        PlayerRole.VAMPIRE -> R.string.vampire
        PlayerRole.VILLAGER -> R.string.villager
        PlayerRole.SHERIFF -> R.string.sheriff
        PlayerRole.WATCHER -> R.string.watcher
        PlayerRole.SERIAL_KILLER -> R.string.serial_killer
        PlayerRole.DOCTOR -> R.string.doctor
        PlayerRole.SEER -> R.string.seer
        PlayerRole.VOTE_SABOTEUR -> R.string.vote_saboteur
        PlayerRole.AUTOPSIR -> R.string.autopsir
        PlayerRole.VETERAN -> R.string.veteran
        PlayerRole.MADMAN -> R.string.madman
        PlayerRole.WIZARD -> R.string.wizard
    }
    return stringResource(id = stringResId)
}

/**
 * Role göre gece eylem ipucunu döndürür
 */
@Composable
private fun getRoleNightTip(role: PlayerRole): String {
    val stringResId = when (role) {
        PlayerRole.VAMPIRE -> R.string.vampire_night_tip
        PlayerRole.VILLAGER -> R.string.villager_night_tip
        PlayerRole.SHERIFF -> R.string.sheriff_night_tip
        PlayerRole.WATCHER -> R.string.watcher_night_tip
        PlayerRole.SERIAL_KILLER -> R.string.serial_killer_night_tip
        PlayerRole.DOCTOR -> R.string.doctor_night_tip
        PlayerRole.SEER -> R.string.seer_night_tip
        PlayerRole.VOTE_SABOTEUR -> R.string.vote_saboteur_night_tip
        PlayerRole.AUTOPSIR -> R.string.autopsir_night_tip
        PlayerRole.VETERAN -> R.string.veteran_night_tip
        PlayerRole.MADMAN -> R.string.madman_night_tip
        PlayerRole.WIZARD -> R.string.wizard_night_tip
    }
    return stringResource(id = stringResId)
}

@Preview(showBackground = true)
@Composable
fun NightActionScreenPreview() {
    val previewPlayers = listOf(
        Player(0, "Emir", PlayerRole.VAMPIRE),
        Player(1, "Hakan", PlayerRole.VILLAGER),
        Player(2, "Zeynep", PlayerRole.WATCHER),
        Player(3, "Cem", PlayerRole.SHERIFF),
        Player(4, "Umut", PlayerRole.VILLAGER),
        Player(5, "Emir", PlayerRole.VILLAGER, isAlive = false)
    )
    
    NightActionScreen(
        activePlayer = previewPlayers[1],
        players = previewPlayers,
        displayRole = previewPlayers[1].role,
        onTargetSelected = { _ -> }
    )
}