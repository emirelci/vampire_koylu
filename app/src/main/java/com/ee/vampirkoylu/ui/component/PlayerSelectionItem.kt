package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

enum class SelectionState { NONE, VOTE, SABOTAGE }
import kotlin.random.Random

// Kullanılabilecek tüm avatar resimleri - role göre sınıflandırılmamış
private val allAvatarResources = listOf(
    R.drawable.doctor1,
    R.drawable.serial_killer1,
    R.drawable.sheriff1,
    R.drawable.sheriff2,
    R.drawable.villager1,
    R.drawable.villager2, 
    R.drawable.villager3,
    R.drawable.villager4,
    R.drawable.watcher1,
    R.drawable.vampir_logo
)

/**
 * Rastgele avatar resmi seçer
 */
private fun getRandomAvatarResId(): Int {
    return allAvatarResources[Random.nextInt(allAvatarResources.size)]
}

/**
 * Oyuncu seçim öğesi komponenti
 * @param name Oyuncu adı
 * @param selectionState Seçim durumu
 * @param onSelect Seçildiğinde çağrılacak fonksiyon
 * @param modifier Modifier
 */
@Composable
fun PlayerSelectionItem(
    name: String,
    selectionState: SelectionState = SelectionState.NONE,
    isAlive: Boolean = true,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarResId = remember(name) { getRandomAvatarResId() }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(8.dp)
    ) {
        // Avatar çerçevesi ve içerik
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFF996515),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = isAlive) { onSelect() }
        ) {
            // Border (çerçeve)
            Image(
                painter = painterResource(id = R.drawable.player_avatar_empty),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            
            // Avatar resmi
            Image(
                painter = painterResource(id = avatarResId),
                contentDescription = "Oyuncu avatarı: $name",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                alpha = if (isAlive) 1f else 0.5f
            )
        }
        
        // İsim
        Text(
            text = name,
            fontFamily = PixelFont,
            fontSize = 20.sp,
            color = shine_gold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        // Seçim kutusu
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(enabled = isAlive) { onSelect() }
                .background(
                    if (selectionState != SelectionState.NONE) Color.Red else Color(0xFF1A1A2E)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF996515),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when (selectionState) {
                SelectionState.VOTE -> Text("✓", color = Color.White, fontSize = 24.sp, fontFamily = PixelFont)
                SelectionState.SABOTAGE -> Text("O", color = Color.White, fontSize = 24.sp, fontFamily = PixelFont)
                else -> {}
            }
            
            if (!isAlive) {
                // Oyuncu ölmüşse X işareti
                Text(
                    text = "X",
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontFamily = PixelFont
                )
            }
        }
    }
}