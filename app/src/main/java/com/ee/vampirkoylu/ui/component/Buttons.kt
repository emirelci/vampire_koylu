package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.theme.PixelFont
/*
@Composable
fun PixelArtButton(
    text: String,
    onClick: () -> Unit,
    imageId: Int,
    color: Color = Color(0xFFF0E68C),
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .width(260.dp) // görselin doğal oranına göre ayarlanabilir
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageId), // ← doğru drawable adı olmalı
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = PixelFont,
            color = color,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp
        )
    }
}

 */


@Composable
fun PixelArtButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    imageId: Int = R.drawable.button_brown, // arka plan görseli
    color: Color = Color(0xFFF0E68C),
    width: Dp = 260.dp,
    height: Dp = 80.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(width)
            .height(height),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = color
        ),
        contentPadding = PaddingValues(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Text(
                text = text,
                fontSize = fontSize,
                fontFamily = PixelFont,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
