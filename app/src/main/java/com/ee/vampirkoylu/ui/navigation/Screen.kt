package com.ee.vampirkoylu.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Setup : Screen("setup")
    object Game : Screen("game")
    object MeetingDay : Screen("meeting_day")
    object DayVoteResult : Screen("day_vote_result")
    object Judgement : Screen("judgement")
    object Rules : Screen("rules")
    object GameOver : Screen("game_over")
    object RoleReveal : Screen("reveal/{index}") {
        fun createRoute(index: Int) = "reveal/$index"
    }
}