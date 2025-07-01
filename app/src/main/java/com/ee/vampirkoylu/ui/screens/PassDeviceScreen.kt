package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.DarkBlue
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold

/**
 * Shared prompt shown before revealing a player's role or performing an action.
 *
 * @param playerName Name of the next player.
 * @param onReady Called when the user confirms they are ready.
 * @param modifier Modifier for the container.
 */
@Composable
fun PassDeviceScreen(
    playerName: String,
    onReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        PixelArtButton(
            text = stringResource(id = R.string.show_role),
            onClick = onReady,
            imageId = R.drawable.button_orange,
            fontSize = 14.sp,
            color = DarkBlue
        )

        Text(
            text = stringResource(id = R.string.pass_warning),
            fontSize = 10.sp,
            fontFamily = PixelFont,
            color = Beige,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}