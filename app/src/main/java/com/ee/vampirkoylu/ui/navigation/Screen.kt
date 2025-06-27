package com.ee.vampirkoylu.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Setup : Screen("setup")
    object Game : Screen("game")
    object Rules : Screen("rules")
    object RoleReveal : Screen("reveal/{index}") {
        fun createRoute(index: Int) = "reveal/$index"
    }
}