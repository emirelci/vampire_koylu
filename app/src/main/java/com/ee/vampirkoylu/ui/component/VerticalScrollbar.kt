package com.ee.vampirkoylu.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Simple vertical scrollbar that displays a draggable thumb representing
 * the current scroll position. Designed for use with [ScrollState].
 */
@Composable
fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.width(8.dp).fillMaxHeight()) {
        val density = LocalDensity.current
        val containerHeightPx = with(density) { maxHeight.toPx() }
        val maxScroll = scrollState.maxValue.toFloat()
        if (maxScroll > 0f) {
            val proportion = containerHeightPx / (containerHeightPx + maxScroll)
            val thumbHeightPx = containerHeightPx * proportion
            val maxThumbOffset = containerHeightPx - thumbHeightPx
            val thumbOffsetPx = (scrollState.value / maxScroll) * maxThumbOffset
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, thumbOffsetPx.roundToInt()) }
                    .width(4.dp)
                    .height(with(density) { thumbHeightPx.toDp() })
                    .background(Color.Gray.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
            )
        }
    }
}