package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.PixelFont

@Composable
fun RuleScreen(navController: NavHostController) {
    Column(
    modifier = Modifier
    .fillMaxSize()
    .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.rules_title),
            fontSize = 36.sp,
            fontFamily = PixelFont,
            color = Color(0xFFF0E68C),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
            )
        ) {
            Text(
                text = stringResource(id = R.string.rules_text),
                fontSize = 16.sp,
                fontFamily = PixelFont,
                color = Color(0xFFF0E68C),
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            )
        }

        PixelArtButton(
            text = stringResource(id = R.string.back_to_menu),
            onClick = { navController.navigateUp() },
            imageId = R.drawable.button_brown,
            modifier = Modifier.padding(vertical = 16.dp),
            fontSize = 14.sp
        )
    }
}