package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

/**
 * Bir sonraki oyuncuya geçiş ekranı
 *
 * @param playerName Sıradaki oyuncunun adı
 * @param onContinue Devam et butonuna tıklandığında çağrılacak fonksiyon
 */
@Composable
fun NextPlayerScreen(
    playerName: String,
    onContinue: () -> Unit
) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Başlık
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
                    .padding(16.dp)
            )

            // Açıklama
            BasicText(
                text = stringResource(id = R.string.pass_to, playerName),
                modifier = Modifier
                    .padding(bottom = 64.dp, top = 12.dp)
                    .fillMaxWidth(),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = PixelFont,
                    color = Beige,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            )

            // Devam et butonu
            PixelArtButton(
                text = stringResource(id = R.string.show_role),
                onClick = onContinue,
                imageId = R.drawable.button_orange,
                fontSize = 14.sp,
                color = DarkBlue
            )

            // Uyarı metni
            Text(
                text = stringResource(id = R.string.pass_warning),
                fontSize = 10.sp,
                fontFamily = PixelFont,
                color = Beige,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NextPlayerScreenPreview() {
    NextPlayerScreen(
        playerName = "Emir",
        onContinue = {}
    )
} 