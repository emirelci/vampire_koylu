package com.ee.vampirkoylu.model

import androidx.compose.runtime.Immutable

/**
 * Oyuncunun rolünü temsil eden enum sınıfı
 */
enum class PlayerRole {
    VAMPIRE, // Vampir
    VILLAGER // Köylü
}

/**
 * Oyun aşamasını temsil eden enum sınıfı
 */
enum class GamePhase {
    SETUP,       // Oyun kurulumu
    NIGHT,       // Gece fazı
    DAY,         // Gündüz fazı
    VOTING,      // Oylama fazı
    VOTE_RESULT, // Oylama sonucu
    GAME_OVER    // Oyun sonu
}

/**
 * Oyuncu bilgilerini içeren data class
 */
@Immutable
data class Player(
    val id: Int,
    val name: String,
    val role: PlayerRole,
    val isAlive: Boolean = true,
    val isRevealed: Boolean = false // Rolü açığa çıktı mı?
)

/**
 * Oyun durumunu temsil eden data class
 */
@Immutable
data class GameState(
    val players: List<Player> = emptyList(),
    val currentPhase: GamePhase = GamePhase.SETUP,
    val currentDay: Int = 1,
    val nightTarget: Int? = null, // Gece hedef seçilen oyuncu ID'si
    val votingResults: Map<Int, Int> = emptyMap(), // Key: Oy verilen ID, Value: Oy sayısı
    val lastEliminated: Int? = null, // Son elenen oyuncu ID'si
    val gameResult: GameResult? = null // Oyun sonucu
)

/**
 * Oyun sonucunu temsil eden data class
 */
@Immutable
data class GameResult(
    val winningRole: PlayerRole,
    val alivePlayers: List<Player>
)

/**
 * Oyun ayarlarını temsil eden data class
 */
@Immutable
data class GameSettings(
    val playerCount: Int = 4,
    val vampireCount: Int = 1
) 