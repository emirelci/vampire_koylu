package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.theme.Gold
import com.ee.vampirkoylu.ui.theme.PixelFont

@Composable
fun ModeSelector(
    modes: List<Int>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val canDecrease = selectedIndex > 0
        IconButton(
            onClick = { if (canDecrease) onIndexChange(selectedIndex - 1) },
            modifier = Modifier.size(28.dp)
        ) {
            Image(
                painterResource(R.drawable.text_block),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.wrapContentSize(),
                contentDescription = "Previous mode",
            )
            Text(
                text = "<",
                fontSize = 18.sp,
                color = if (canDecrease) Gold else Gold.copy(alpha = 0.5f)
            )
        }

        Text(
            text = stringResource(id = modes[selectedIndex]),
            fontSize = 16.sp,
            fontFamily = PixelFont,
            color = Gold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        val canIncrease = selectedIndex < modes.lastIndex
        IconButton(
            onClick = { if (canIncrease) onIndexChange(selectedIndex + 1) },
            modifier = Modifier.size(28.dp)
        ) {
            Image(
                painterResource(R.drawable.text_block),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.wrapContentSize(),
                contentDescription = "Next mode",
            )
            Text(
                text = ">",
                fontSize = 18.sp,
                color = if (canIncrease) Gold else Gold.copy(alpha = 0.5f)
            )
        }
    }
}
