package com.ee.vampirkoylu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.ui.navigation.NavGraph
import com.ee.vampirkoylu.ui.screens.HomeScreen
import com.ee.vampirkoylu.ui.screens.MainScreenBackground
import com.ee.vampirkoylu.ui.theme.VampirKoyluTheme
import com.ee.vampirkoylu.util.rememberWindowWidthSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val widthSizeClass = rememberWindowWidthSizeClass()
            VampirKoyluTheme(widthSizeClass = widthSizeClass) {
                val navController = rememberNavController()
                NavGraph.SetupNavGraph(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    VampirKoyluTheme(widthSizeClass = androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact) {
        MainScreenBackground {
            HomeScreen(navController = rememberNavController())
        }
    }
}