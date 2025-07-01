package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.PixelFont

@Composable
fun RoleCountSelector(
    title: String,
    count: Int,
    maxCount: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontFamily = PixelFont,
            color = Beige,
            modifier = Modifier.weight(1f)
        )

        IncreaseDecreaseCount(
            count = count,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            canIncrease = count < maxCount,
            canDecrease = count > 0,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}