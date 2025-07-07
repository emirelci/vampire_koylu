package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.ui.theme.PixelFont

@Composable
fun RoleInfoItem(imageRes: Int, nameRes: Int, descRes: Int) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.widthIn(max = 150.dp)) {
            Text(
                text = stringResource(id = nameRes),
                fontFamily = PixelFont,
                color = Color(0xFFF0E68C),
                fontSize = 14.sp
            )
            Text(
                text = stringResource(id = descRes),
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}