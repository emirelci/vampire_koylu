package com.ee.vampirkoylu.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator

// WindowWidthSizeClass'ı elle tanımlayalım (kararlı sürüm)
enum class WindowWidthSizeClass {
    Compact,    // 0dp - 600dp: telefon
    Medium,     // 600dp - 840dp: küçük tablet
    Expanded    // 840dp+: büyük tablet/katlanabilir
}

// WindowHeightSizeClass'ı elle tanımlayalım (kararlı sürüm) 
enum class WindowHeightSizeClass {
    Compact,    // 0dp - 480dp: küçük ekran
    Medium,     // 480dp - 900dp: normal
    Expanded    // 900dp+: geniş ekran
}

// Kendi WindowSizeClass sınıfımızı oluşturalım
data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
)

/**
 * Kararlı API kullanarak ekran genişlik sınıfını hesapla
 */
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth.value < 600f -> WindowWidthSizeClass.Compact
        screenWidth.value < 840f -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
}

/**
 * Kararlı API kullanarak tam pencere boyut sınıfını hesapla
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val widthSizeClass = when {
        screenWidth.value < 600f -> WindowWidthSizeClass.Compact
        screenWidth.value < 840f -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
    
    val heightSizeClass = when {
        screenHeight.value < 480f -> WindowHeightSizeClass.Compact
        screenHeight.value < 900f -> WindowHeightSizeClass.Medium
        else -> WindowHeightSizeClass.Expanded
    }
    
    return WindowSizeClass(widthSizeClass, heightSizeClass)
}

/**
 * WindowMetricsCalculator kullanarak boyut hesaplama (modern kararlı API)
 */
@Composable
fun rememberWindowMetrics(): WindowMetrics {
    val context = LocalContext.current
    val calculator = remember { WindowMetricsCalculator.getOrCreate() }
    return calculator.computeCurrentWindowMetrics(context)
}

/**
 * Modern API kullanarak ekran boyutunu dp cinsinden hesapla
 */
@Composable
fun rememberScreenSizeDp(): Pair<Float, Float> {
    val windowMetrics = rememberWindowMetrics()
    val configuration = LocalConfiguration.current
    val density = configuration.densityDpi / 160f
    
    val widthDp = windowMetrics.bounds.width() / density
    val heightDp = windowMetrics.bounds.height() / density
    
    return Pair(widthDp, heightDp)
}

/**
 * Responsive tasarım için breakpoint kontrolü
 */
@Composable
fun isCompactScreen(): Boolean {
    return rememberWindowWidthSizeClass() == WindowWidthSizeClass.Compact
}

@Composable
fun isMediumScreen(): Boolean {
    return rememberWindowWidthSizeClass() == WindowWidthSizeClass.Medium
}

@Composable
fun isExpandedScreen(): Boolean {
    return rememberWindowWidthSizeClass() == WindowWidthSizeClass.Expanded
}
