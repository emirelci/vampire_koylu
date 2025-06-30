package com.ee.vampirkoylu.util

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val activity = LocalContext.current as Activity
    return calculateWindowSizeClass(activity).widthSizeClass
}
