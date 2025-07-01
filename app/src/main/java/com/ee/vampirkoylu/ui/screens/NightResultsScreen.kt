package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
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
import com.ee.vampirkoylu.model.*
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

/**
 * Gece sonuçlarını gösteren ekran
 * Bu ekranda oyuncu rolüne göre gece sonuçları gösterilir
 * Köylüler için sadece "Gece bitti" mesajı gösterilir
 * Özel roller için kendi rollerine özel sonuçlar gösterilir
 *
 * @param player Aktif oyuncu
 * @param allPlayers Tüm oyuncular listesi
 * @param gameState Oyun durumu
 * @param onContinue Devam et butonuna tıklandığında çağrılacak fonksiyon
 */
@Composable
fun NightResultsScreen(
    player: Player,
    allPlayers: List<Player>,
    gameState: GameState,
    onContinue: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    val handleContinue = {
        revealed = false
        onContinue()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan
        Image(
            painter = painterResource(id = R.drawable.night_info_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // İçerik
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!revealed) {
                // İlk ekran - Oyuncu adını göster ve hazırım butonu
                PassDeviceScreen(
                    playerName = player.name,
                    onReady = { revealed = true }
                )
            } else {
                // Gece Sonuçları Ekranı
                
                // Başlık
                Text(
                    text = stringResource(R.string.night_results_header),
                    fontSize = 24.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, top = 68.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Oyuncu ölü veya ölmekte mi kontrolü
                if (!player.isAlive || player.isDying) {
                    // Ölü veya ölmekte olan oyuncular için özel mesaj
                    DisplayDeadPlayerMessage(player.name)
                } else {
                    // Canlı oyuncular için rol bazlı mesajlar
                    when (player.role) {
                        PlayerRole.SHERIFF -> {
                            DisplaySheriffResults(player.id, gameState.sheriffResults, allPlayers)
                        }
                        PlayerRole.WATCHER -> {
                            DisplayWatcherResults(player.id, gameState.watcherResults, allPlayers)
                        }
                        else -> {
                            // Köylü, Vampir, Seri Katil ve Doktor için sadece bilgi mesajı göster
                            Text(
                                text = stringResource(id = R.string.night_results_villager),
                                fontSize = 16.sp,
                                fontFamily = PixelFont,
                                color = Beige,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 48.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Devam et butonu
                PixelArtButton(
                    text = stringResource(id = R.string.ready_for_new_day),
                    onClick = handleContinue,
                    imageId = R.drawable.button_orange,
                    fontSize = 14.sp,
                    color = DarkBlue,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

/**
 * Ölmüş oyuncular için mesaj gösterir
 */
@Composable
fun DisplayDeadPlayerMessage(playerName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        // Ölüm bildirimi
        Text(
            text = stringResource(id = R.string.night_results_dead_player, playerName),
            fontSize = 18.sp,
            fontFamily = PixelFont,
            color = shine_gold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Detay mesajı
        Text(
            text = stringResource(id = R.string.night_results_dead_message),
            fontSize = 14.sp,
            fontFamily = PixelFont,
            color = Beige,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
    }
}

/**
 * Şerif sonuçlarını gösterir
 */
@Composable
fun DisplaySheriffResults(
    sheriffId: Int,
    sheriffResults: List<SheriffInvestigation>,
    allPlayers: List<Player>
) {
    // Son güne ait şerif incelemesini bul
    val latestResult = sheriffResults.lastOrNull()
    
    if (latestResult != null) {
        val targetPlayer = allPlayers.find { it.id == latestResult.targetId }
        if (targetPlayer != null) {
            val resultText = if (latestResult.result == GuiltStatus.GUILTY) {
                stringResource(id = R.string.sheriff_result_guilty)
            } else {
                stringResource(id = R.string.sheriff_result_innocent)
            }
            
            Text(
                text = stringResource(
                    id = R.string.night_results_sheriff,
                    targetPlayer.name,
                    resultText
                ),
                fontSize = 16.sp,
                fontFamily = PixelFont,
                lineHeight = 24.sp,
                color = if (latestResult.result == GuiltStatus.GUILTY) shine_gold else Beige,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top =  48.dp)
            )
        }
    }
}

/**
 * Gözcü sonuçlarını gösterir
 */
@Composable
fun DisplayWatcherResults(
    watcherId: Int,
    watcherResults: List<WatcherObservation>,
    allPlayers: List<Player>
) {
    // Son güne ait gözcü gözlemini bul
    val latestObservation = watcherResults.lastOrNull()
    
    if (latestObservation != null) {
        val targetPlayer = allPlayers.find { it.id == latestObservation.targetId }
        if (targetPlayer != null) {
            // Hedefi kim ziyaret etti
            Text(
                text = stringResource(id = R.string.night_results_watcher, targetPlayer.name),
                fontSize = 16.sp,
                fontFamily = PixelFont,
                color = Beige,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Ziyaretçileri göster
            if (latestObservation.visitorIds.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.night_results_watcher_novisit),
                    fontSize = 14.sp,
                    fontFamily = PixelFont,
                    color = Beige,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else if (latestObservation.visitorIds.size == 1) {
                val visitor = allPlayers.find { it.id == latestObservation.visitorIds.first() }
                if (visitor != null) {
                    Text(
                        text = stringResource(id = R.string.night_results_watcher_visit, visitor.name),
                        fontSize = 14.sp,
                        fontFamily = PixelFont,
                        color = Beige,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            } else {
                val visitorNames = latestObservation.visitorIds.mapNotNull { id ->
                    allPlayers.find { it.id == id }?.name
                }.joinToString(", ")
                
                Text(
                    text = stringResource(
                        id = R.string.night_results_watcher_visits,
                        latestObservation.visitorIds.size,
                        visitorNames
                    ),
                    fontSize = 14.sp,
                    fontFamily = PixelFont,
                    color = Beige,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NightResultsScreenPreview() {
    val player = Player(0, "Emir", PlayerRole.VILLAGER)
    val allPlayers = listOf(
        player,
        Player(1, "Ahmet", PlayerRole.VAMPIRE),
        Player(2, "Mehmet", PlayerRole.VILLAGER)
    )
    val gameState = GameState(
        players = allPlayers,
        sheriffResults = listOf(
            SheriffInvestigation(1, GuiltStatus.GUILTY)
        )
    )
    
    NightResultsScreen(
        player = player,
        allPlayers = allPlayers,
        gameState = gameState,
        onContinue = {}
    )
} 