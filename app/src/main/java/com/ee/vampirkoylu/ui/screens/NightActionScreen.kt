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
    onTargetSelected: (Int) -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    var selectedPlayerId by remember { mutableStateOf<Int?>(null) }
    
    // Hedef seçildiğinde çağrılacak fonksiyon
    val handleTargetSelected = { targetId: Int ->
        // Önce hedefi bildir
        onTargetSelected(targetId)
        // Sonra revealed durumunu false yap ki bir sonraki oyuncu için "Sıra:Oyuncu" ekranı gösterilsin
        revealed = false
        // Seçilen oyuncuyu sıfırla
        selectedPlayerId = null
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
            if (revealed) {
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
                
                // Rol bazlı bilgi metni
                Text(
                    text = getRoleNightTip(activePlayer.role),
                    fontSize = 16.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (activePlayer.role == PlayerRole.VILLAGER) {
                    // Köylü için görünüm - Grid gösterme, sadece devam et butonu göster
                    Spacer(modifier = Modifier.weight(1f))
                    // Devam et butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.continue_game),
                        onClick = { handleTargetSelected(-1) }, // Köylüler için -1 gönder
                        imageId = R.drawable.button_red,
                        fontSize = 20.sp
                    )
                } else {
                    // Diğer roller için oyuncu grid'i göster
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(players.filter { it.id != activePlayer.id }) { player ->
                            PlayerSelectionItem(
                                name = player.name,
                                isSelected = selectedPlayerId == player.id,
                                isAlive = player.isAlive,
                                onSelect = { 
                                    if (player.isAlive) {
                                        selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                                    }
                                }
                            )
                        }
                    }
                    
                    // Onaylama butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.confirm),
                        onClick = {
                            if(selectedPlayerId != null){
                                selectedPlayerId?.let { handleTargetSelected(it) }
                            }else{
                                handleTargetSelected(-1)
                            }
                        },
                        imageId = R.drawable.button_red,
                        modifier = Modifier.padding(top = 24.dp),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
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
        onTargetSelected = {}
    )
}