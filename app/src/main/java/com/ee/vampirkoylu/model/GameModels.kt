package com.ee.vampirkoylu.model

import androidx.compose.runtime.Immutable

/**
 * Oyuncunun rolünü temsil eden enum sınıfı
 */
enum class PlayerRole {
    VAMPIRE,     // Vampir
    VILLAGER,    // Köylü  
    SHERIFF,     // Şerif
    WATCHER,     // Gözcü
    SERIAL_KILLER, // Seri Katil
    DOCTOR,       // Doktor
    // Plus paketinde açılan roller
    VOTE_SABOTEUR, // Oylama Sabotajcısı (Sahtekar)
    AUTOPSIR,      // Ölü Gözlemcisi
    VETERAN,       // Nöbetçi
    MADMAN,        // Deli
    WIZARD         // Büyücü (Yer Değiştirici)
}

/**
 * Suçluluk durumunu temsil eden enum sınıfı
 */
enum class GuiltStatus {
    GUILTY,      // Suçlu (Vampir, Seri Katil)
    INNOCENT     // Masum (Köylü, Şerif, Gözcü, Doktor)
}

/**
 * Oyun aşamasını temsil eden enum sınıfı
 */
enum class GamePhase {
    SETUP,       // Oyun kurulumu
    NIGHT,       // Gece fazı
    NIGHT_RESULT, // Gece sonucu fazı
    DAY,         // Gündüz fazı
    VOTING,      // Oylama fazı
    DAY_VOTE_RESULT, // Gündüz oylama sonucu
    JUDGEMENT,   // Yargılama fazı
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
    val isDying: Boolean = false,  // Bu turda öldürüldü ama sonuçlar fazında hala aktif
)

/**
 * Gece ziyareti bilgisi
 * Kimin kimi ziyaret ettiği bilgisini tutar
 */
@Immutable
data class NightVisit(
    val visitorId: Int,    // Ziyaret eden oyuncunun ID'si
    val targetId: Int      // Ziyaret edilen oyuncunun ID'si
)

/**
 * Şerif inceleme sonucu
 */
@Immutable
data class SheriffInvestigation(
    val targetId: Int?,         // İncelenen kişinin ID'si
    val result: GuiltStatus    // İnceleme sonucu (Suçlu/Masum)
)

/**
 * Gözcü izleme sonucu
 */
@Immutable
data class WatcherObservation(
    val targetId: Int,             // İzlenen kişinin ID'si
    val visitorIds: List<Int>      // Ziyaretçilerin ID'leri
)

/**
 * Otopsir raporu
 */
@Immutable
data class AutopsirReport(
    val targetId: Int,          // İncelenen ölü oyuncunun ID'si
    val role: PlayerRole        // Oyuncunun gerçek rolü
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
    val doctorTarget: Int? = null, // Doktorun koruduğu oyuncu ID'si
    val serialKillerTarget: Int? = null, // Seri katilin hedefi
    val sheriffTarget: Int? = null, // Şerifin incelediği kişi
    val watcherTarget: Int? = null, // Gözcünün izlediği kişi
    val nightVisits: List<NightVisit> = emptyList(), // Gece ziyaretleri
    val sheriffResults: List<SheriffInvestigation> = emptyList(), // Şerif sonuçları
    val watcherResults: List<WatcherObservation> = emptyList(), // Gözcü sonuçları
    val autopsirResults: List<AutopsirReport> = emptyList(), // Otopsir sonuçları
    val veteranAlertIds: Set<Int> = emptySet(), // Bu gece uyanık kalan nöbetçiler
    val wizardSwap: Pair<Int, Int>? = null, // Büyücünün yerini değiştirdiği oyuncular
    val voteSabotageTarget: Int? = null, // Sahtekarın oyunu iptal edeceği oyuncu
    val votingResults: Map<Int, Int> = emptyMap(), // Key: Oy verilen ID, Value: Oy sayısı
    val lastEliminated: Int? = null, // Son elenen oyuncu ID'si
    val accusedId: Int? = null, // Oylama sonucunda suçlanan oyuncu
    val judgementVotes: Map<Int, Boolean> = emptyMap(), // Yargılama oyları
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
    val vampireCount: Int = 1,
    val sheriffCount: Int = 0,
    val watcherCount: Int = 0,
    val serialKillerCount: Int = 0,
    val doctorCount: Int = 0,

    // Plus paketindeki yeni rollerin sayıları
    val voteSaboteurCount: Int = 0,
    val autopsirCount: Int = 0,
    val veteranCount: Int = 0,
    val madmanCount: Int = 0,
    val wizardCount: Int = 0
) 