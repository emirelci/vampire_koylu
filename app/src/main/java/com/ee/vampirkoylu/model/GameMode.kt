package com.ee.vampirkoylu.model

enum class GameMode(val nameRes: Int, val settings: GameSettings, val plusOnly: Boolean = false) {
    CLASSIC(
        com.ee.vampirkoylu.R.string.classic_mode,
        GameSettings(
            playerCount = 6,
            vampireCount = 1,
            sheriffCount = 1,
            watcherCount = 0,
            serialKillerCount = 0,
            doctorCount = 1
        )
    ),
    CHAOS(
        com.ee.vampirkoylu.R.string.chaos_mode,
        GameSettings(
            playerCount = 8,
            vampireCount = 2,
            sheriffCount = 1,
            watcherCount = 1,
            serialKillerCount = 1,
            doctorCount = 1
        )
    ),
    CUSTOM(
        com.ee.vampirkoylu.R.string.custom_mode,
        GameSettings()
    ),
    EXTREME(
        com.ee.vampirkoylu.R.string.extreme_mode,
        GameSettings(
            playerCount = 10,
            vampireCount = 2,
            sheriffCount = 1,
            watcherCount = 1,
            serialKillerCount = 1,
            doctorCount = 1,
            voteSaboteurCount = 1,
            autopsirCount = 0,
            veteranCount = 1,
            madmanCount = 1,
            wizardCount = 0
        ),
        true
    ),
    ULTIMATE(
        com.ee.vampirkoylu.R.string.ultimate_mode,
        GameSettings(
            playerCount = 12,
            vampireCount = 2,
            sheriffCount = 1,
            watcherCount = 1,
            serialKillerCount = 1,
            doctorCount = 1,
            voteSaboteurCount = 1,
            autopsirCount = 1,
            veteranCount = 1,
            madmanCount = 1,
            wizardCount = 1
        ),
        true
    );
}
