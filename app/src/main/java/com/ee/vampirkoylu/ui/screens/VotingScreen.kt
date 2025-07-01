package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
 * Oylama ekranı
 *
 * @param activePlayer Aktif oyuncu
 * @param players Tüm oyuncu listesi
 * @param onVote Oylama yapıldığında çağrılacak fonksiyon
 * @param onSkipVote Oylamayı atla butonuna basıldığında çağrılacak fonksiyon
 */
@Composable
fun VotingScreen(
    activePlayer: Player,
    players: List<Player>,
    onVote: (Int) -> Unit,
    onSkipVote: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }
    var selectedPlayerId by remember { mutableStateOf<Int?>(null) }
    
    // Oy kullanıldığında veya atlandığında çağrılacak fonksiyon
    val handleVote = { targetId: Int? ->
        if (targetId != null) {
            onVote(targetId)
        } else {
            onSkipVote()
        }
        // Sonraki oyuncu için görünümü sıfırla
        revealed = false
        selectedPlayerId = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan - morning_vote_day_bg kullanımı
        Image(
            painter = painterResource(id = R.drawable.morning_vote_day_bg),
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
                // Ana oylama ekranı
                // Başlık
                Text(
                    text = stringResource(R.string.voting_phase),
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                // Oylama bilgi metni
                Text(
                    text = stringResource(id = R.string.voting_instruction),
                    fontSize = 16.sp,
                    fontFamily = PixelFont,
                    color = shine_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Oyuncular grid'i göster (kendisi hariç ve ölmemiş olanlar)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(players.filter { it.id != activePlayer.id && it.isAlive && !it.isDying }) { player ->
                        PlayerSelectionItem(
                            name = player.name,
                            isSelected = selectedPlayerId == player.id,
                            isAlive = player.isAlive,
                            onSelect = { 
                                selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                            }
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // "Oy verme" butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.skip_vote),
                        onClick = { handleVote(null) },
                        imageId = R.drawable.button_brown,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    
                    // Onayla butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.confirm),
                        onClick = { 
                            selectedPlayerId?.let { handleVote(it) }
                        },
                        imageId = R.drawable.button_red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        //enabled = selectedPlayerId != null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VotingScreenPreview() {
    val previewPlayers = listOf(
        Player(0, "Emir", PlayerRole.VAMPIRE),
        Player(1, "Hakan", PlayerRole.VILLAGER),
        Player(2, "Zeynep", PlayerRole.VILLAGER),
        Player(3, "Cem", PlayerRole.VILLAGER),
        Player(4, "Umut", PlayerRole.VILLAGER)
    )
    
    VotingScreen(
        activePlayer = previewPlayers[0],
        players = previewPlayers,
        onVote = {},
        onSkipVote = {}
    )
} 