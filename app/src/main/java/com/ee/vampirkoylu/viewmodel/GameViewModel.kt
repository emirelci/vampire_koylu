package com.ee.vampirkoylu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee.vampirkoylu.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    // Oyun durumunu tutan state flow
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Oyun ayarlarını tutan state flow
    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    // Oyuncu listesi
    val players: StateFlow<List<Player>> =
        _gameState.map { it.players }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Aktif oyuncu (şu anda sıra kimde)
    private val _activePlayer = MutableStateFlow<Player?>(null)
    val activePlayer: StateFlow<Player?> = _activePlayer.asStateFlow()

    // Son turda öldürülen oyuncular
    private val _deadPlayers = MutableStateFlow<List<Player>>(emptyList())
    val deadPlayers: StateFlow<List<Player>> = _deadPlayers.asStateFlow()

    private val votedPlayers = mutableSetOf<Int>()
    private var skipVoteCount = 0
    /**
     * Oyun ayarlarını günceller
     */
    fun updateSettings(
        playerCount: Int,
        vampireCount: Int,
        sheriffCount: Int = 0,
        watcherCount: Int = 0,
        serialKillerCount: Int = 0,
        doctorCount: Int = 0,
        seerCount: Int = 0,
        voteSaboteurCount: Int = 0,
        autopsirCount: Int = 0,
        veteranCount: Int = 0,
        madmanCount: Int = 0,
        wizardCount: Int = 0
    ) {
        _settings.update {
            it.copy(
                playerCount = playerCount.coerceIn(4, 15), // Min 4, max 15 oyuncu
                vampireCount = vampireCount.coerceIn(
                    1,
                    playerCount / 3
                ), // En fazla oyuncu sayısının 1/3'ü kadar vampir
                sheriffCount = sheriffCount.coerceIn(0, 1), // En fazla 1 şerif
                watcherCount = watcherCount.coerceIn(0, playerCount), // Gözcü sayısı sınırı
                serialKillerCount = serialKillerCount.coerceIn(0, 1), // En fazla 1 seri katil
                doctorCount = doctorCount.coerceIn(0, playerCount), // Doktor sayısı sınırı
                seerCount = seerCount.coerceIn(0, playerCount),
                voteSaboteurCount = voteSaboteurCount.coerceIn(0, 1),
                autopsirCount = autopsirCount.coerceIn(0, playerCount),
                veteranCount = veteranCount.coerceIn(0, 1),
                madmanCount = madmanCount.coerceIn(0, 1),
                wizardCount = wizardCount.coerceIn(0, playerCount) // Büyücü sayısı sınırı
            )
        }
    }

    /**
     * Oyuncuları ekler ve oyunu başlatır
     */
    fun startGame(playerNames: List<String>) {
        viewModelScope.launch {
            // Oyuncu sayısını kontrol et
            if (playerNames.size < _settings.value.playerCount) {
                return@launch
            }

            // Rolleri karıştır ve dağıt
            val shuffledRoles = createRoles(
                _settings.value.playerCount,
                _settings.value.vampireCount,
                _settings.value.sheriffCount,
                _settings.value.watcherCount,
                _settings.value.serialKillerCount,
                _settings.value.doctorCount,
                _settings.value.seerCount,
                _settings.value.voteSaboteurCount,
                _settings.value.autopsirCount,
                _settings.value.veteranCount,
                _settings.value.madmanCount,
                _settings.value.wizardCount
            )

            // Oyuncuları oluştur
            val newPlayers =
                playerNames.take(_settings.value.playerCount).mapIndexed { index, name ->
                    Player(
                        id = index,
                        name = name,
                        role = shuffledRoles[index]
                    )
                }

            // Oyun durumunu başlangıç fazına ayarla
            _gameState.update {
                it.copy(
                    players = newPlayers,
                    currentPhase = GamePhase.NIGHT,
                    currentDay = 1
                )
            }

            // İlk aktif oyuncuyu ayarla
            _activePlayer.value = newPlayers.first()
        }
    }

    /**
     * Gece fazında vampirin hedef seçmesi
     */
    fun selectNightTarget(targetIds: List<Int>) {
        // Aktif oyuncuyu al
        val activePlayer = _activePlayer.value ?: return

        // Bazı roller hedef gerektirmez
        when (activePlayer.role) {
            PlayerRole.VAMPIRE -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null) {
                    // Hedef seçilmediyse hiçbir saldırı gerçekleşmez
                    checkNextSpecialRole()
                } else if (targetId != activePlayer.id) {
                    // Hedef seçildiyse saldırıyı kaydet
                    setTargetWithVisit(targetId, activePlayer) { it.copy(nightTarget = targetId) }
                } else {
                    // Kendini hedeflemek geçersiz, sıradaki role geç
                    checkNextSpecialRole()
                }
            }

            PlayerRole.SERIAL_KILLER -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null || targetId == activePlayer.id) {
                    // Hedef seçilmediyse veya kendini seçtiyse hiçbir eylem yok
                    checkNextSpecialRole()
                } else {
                    setTargetWithVisit(targetId, activePlayer) { it.copy(serialKillerTarget = targetId) }
                }
            }

            PlayerRole.SHERIFF -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null || targetId == activePlayer.id) {
                    checkNextSpecialRole()
                } else {
                    setTargetWithVisit(targetId, activePlayer) { it.copy(sheriffTarget = targetId) }
                }
                // Suçlu olup olmadığını kontrol et
                val targetPlayer = _gameState.value.players.find { it.id == targetId }

                if (targetPlayer != null) {
                    val isGuilty = targetPlayer.role == PlayerRole.VAMPIRE ||
                            targetPlayer.role == PlayerRole.SERIAL_KILLER

                    val investigation = SheriffInvestigation(
                        targetId = targetId,
                        result = if (isGuilty) GuiltStatus.GUILTY else GuiltStatus.INNOCENT
                    )

                    _gameState.update {
                        it.copy(sheriffResults = it.sheriffResults + investigation)
                    }
                }
                // Gözcü veya Doktor var mı kontrol et
            }

            PlayerRole.WATCHER -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null || targetId == activePlayer.id) {
                    checkNextSpecialRole()
                } else {
                    setWatcherTarget(activePlayer.id, targetId)
                }
            }

            PlayerRole.DOCTOR -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null || targetId == activePlayer.id) {
                    checkNextSpecialRole()
                } else {
                    setDoctorTarget(activePlayer.id, targetId)
                }
            }

            PlayerRole.SEER -> {
                val targetId = targetIds.firstOrNull()
                if (targetId == null || targetId == activePlayer.id) {
                    checkNextSpecialRole()
                } else {
                    val targetPlayer = _gameState.value.players.find { it.id == targetId }
                    if (targetPlayer != null) {
                        val vision = SeerVision(targetId, targetPlayer.role)
                        val current = _gameState.value.seerResults.toMutableMap()
                        val list = current[activePlayer.id].orEmpty() + vision
                        current[activePlayer.id] = list
                        _gameState.update { it.copy(seerResults = current) }
                    }
                    setTargetWithVisit(targetId, activePlayer) { it }
                }
            }

            PlayerRole.VILLAGER -> {
                // Köylü için özel bir hedef seçimi yok, sadece bir sonraki role geç
                checkNextSpecialRole()
            }

            PlayerRole.VOTE_SABOTEUR -> {
                // Sahtekarın gece eylemi yok
                checkNextSpecialRole()
            }

            PlayerRole.AUTOPSIR -> {
                val targetId = targetIds.firstOrNull()
                if (targetId != null) {
                    val targetPlayer = _gameState.value.players.find { it.id == targetId && !it.isAlive }
                    if (targetPlayer != null) {
                        val report = AutopsirReport(targetId, targetPlayer.role)
                        _gameState.update { it.copy(autopsirResults = it.autopsirResults + report) }
                    }
                }
                checkNextSpecialRole()
            }

            PlayerRole.VETERAN -> {
                if (targetIds.isNotEmpty()) {
                    _gameState.update { it.copy(veteranAlertIds = it.veteranAlertIds + activePlayer.id) }
                }
                checkNextSpecialRole()
            }

            PlayerRole.MADMAN -> {
                // Deli için özel bir eylem yok
                checkNextSpecialRole()
            }

            PlayerRole.WIZARD -> {
                if (targetIds.size == 2) {
                    _gameState.update {
                        it.copy(
                            wizardSwaps = it.wizardSwaps + (activePlayer.id to Pair(targetIds[0], targetIds[1]))
                        )
                    }
                }
                checkNextSpecialRole()
            }

            else -> {
                // Diğer roller için bir işlem yapılmaz
            }
        }
    }

    /**
     * Gündüz fazında oyuncu oyu
     */
    fun vote(targetId: Int, sabotage: Boolean = false) {
        // Aktif oyuncuyu kontrol et
        val activePlayer = _activePlayer.value ?: return

        // Ölmekte olan veya ölü oyuncular oy veremez
        if (!activePlayer.isAlive || activePlayer.isDying) {
            nextPlayer()
            return
        }

        // Vote sabotage özelliği
        if (sabotage && activePlayer.role == PlayerRole.VOTE_SABOTEUR) {
            _gameState.update { it.copy(voteSabotageTarget = targetId) }
            votedPlayers.add(activePlayer.id)
            if (allPlayersVoted()) {
                proceedToVoteResult()
            } else {
                nextPlayer()
            }
            return
        }

        // Sabotajlanan oyuncunun oyu geçersiz sayılır
        if (_gameState.value.voteSabotageTarget == activePlayer.id) {
            votedPlayers.add(activePlayer.id)
            if (allPlayersVoted()) {
                proceedToVoteResult()
            } else {
                nextPlayer()
            }
            return
        }

        // Kendine oy veremez
        if (targetId == activePlayer.id) {
            return
        }

        // Oyları güncelle
        val currentVotes = _gameState.value.votingResults.toMutableMap()
        currentVotes[targetId] = (currentVotes[targetId] ?: 0) + 1

        _gameState.update {
            it.copy(votingResults = currentVotes)
        }

        votedPlayers.add(activePlayer.id)

        // Tüm oyuncular oy verdiyse sonuç fazına geç
        if (allPlayersVoted()) {
            proceedToVoteResult()
        } else {
            // Sıradaki oyuncuya geç
            nextPlayer()
        }
    }

    /**
     * Sonraki aşamaya geç
     */
    fun proceed() {
        println("GameState: " + _gameState.value.currentPhase)

        when (_gameState.value.currentPhase) {
            GamePhase.DAY -> proceedToVoting()
            GamePhase.VOTE_RESULT -> {
                // Oyun bitti mi kontrol et
                if (checkGameOver()) {
                    println("proceedToGameOver()")
                    proceedToGameOver()
                } else {
                    println("proceedToNight()")
                    proceedToNight()
                }
            }

            GamePhase.GAME_OVER -> resetGame()
            else -> {} // Diğer durumlar için bir şey yapma
        }
    }

    /**
     * Oyunu sıfırla
     */
    fun resetGame() {
        _gameState.value = GameState()
        _activePlayer.value = null
        votedPlayers.clear()
        skipVoteCount = 0
    }

    // ---- Yardımcı Metodlar ----

    /**
     * Rolleri oluştur ve karıştır
     */
    private fun createRoles(
        playerCount: Int,
        vampireCount: Int,
        sheriffCount: Int = _settings.value.sheriffCount,
        watcherCount: Int = _settings.value.watcherCount,
        serialKillerCount: Int = _settings.value.serialKillerCount,
        doctorCount: Int = _settings.value.doctorCount,
        seerCount: Int = _settings.value.seerCount,
        voteSaboteurCount: Int = _settings.value.voteSaboteurCount,
        autopsirCount: Int = _settings.value.autopsirCount,
        veteranCount: Int = _settings.value.veteranCount,
        madmanCount: Int = _settings.value.madmanCount,
        wizardCount: Int = _settings.value.wizardCount
    ): List<PlayerRole> {
        val roles = mutableListOf<PlayerRole>()

        // Vampir rollerini ekle
        repeat(vampireCount) {
            roles.add(PlayerRole.VAMPIRE)
        }

        // Özel rolleri ekle
        repeat(sheriffCount) {
            roles.add(PlayerRole.SHERIFF)
        }

        repeat(watcherCount) {
            roles.add(PlayerRole.WATCHER)
        }

        repeat(serialKillerCount) {
            roles.add(PlayerRole.SERIAL_KILLER)
        }

        repeat(doctorCount) {
            roles.add(PlayerRole.DOCTOR)
        }

        repeat(seerCount) { roles.add(PlayerRole.SEER) }

        repeat(voteSaboteurCount) { roles.add(PlayerRole.VOTE_SABOTEUR) }
        repeat(autopsirCount) { roles.add(PlayerRole.AUTOPSIR) }
        repeat(veteranCount) { roles.add(PlayerRole.VETERAN) }
        repeat(madmanCount) { roles.add(PlayerRole.MADMAN) }
        repeat(wizardCount) { roles.add(PlayerRole.WIZARD) }

        // Kalan oyuncular için köylü rollerini ekle
        val specialRoleCount =
            vampireCount + sheriffCount + watcherCount + serialKillerCount + doctorCount + seerCount +
                    voteSaboteurCount + autopsirCount + veteranCount + madmanCount + wizardCount

        repeat(playerCount - specialRoleCount) {
            roles.add(PlayerRole.VILLAGER)
        }

        // Rolleri karıştır
        return roles.shuffled()
    }

    /**
     * Gece fazından gündüz fazına geç
     */
    private fun proceedToDay() {
        // Ölmekte olan oyuncuları belirle (deadPlayers listesini güncelle)
        val dyingPlayers = _gameState.value.players.filter { it.isDying }
        _deadPlayers.value = dyingPlayers

        // isDying olan oyuncuları ölü olarak işaretle
        val updatedPlayers = _gameState.value.players.map { player ->
            if (player.isDying) {
                player.copy(isAlive = false, isDying = false)
            } else {
                player
            }
        }


        _gameState.update {
            it.copy(
                players = updatedPlayers,
                currentPhase = GamePhase.DAY,
                currentDay = it.currentDay + 1,
                lastEliminated = null,  // Artık birden fazla ölüm olabileceği için
                // Gece hedeflerini temizle
                nightTarget = null,
                doctorTargets = emptyMap(),
                serialKillerTarget = null,
                sheriffTarget = null,
                watcherTargets = emptyMap(),
                nightVisits = emptyList(), // Ziyaretleri sıfırla
                veteranAlertIds = emptySet(),
                wizardSwaps = emptyMap(),
                voteSabotageTarget = null
            )
        }

        // Oyun bitti mi kontrol et
        if (checkGameOver()) {
            proceedToGameOver()
        }
    }

    /**
     * Gündüz fazından oylama fazına geç
     */
    fun proceedToVoting() {
        _gameState.update {
            it.copy(
                currentPhase = GamePhase.VOTING,
                votingResults = emptyMap(),
                voteSabotageTarget = null
            )
        }

        votedPlayers.clear()
        skipVoteCount = 0

        // İlk canlı oyuncuyu aktif yap
        val firstAlivePlayer = _gameState.value.players.find { it.isAlive && !it.isDying }
        if (firstAlivePlayer != null) {
            _activePlayer.value = firstAlivePlayer
        }
    }

    /**
     * Oylama sonucunu uygula
     */
    private fun proceedToVoteResult() {
        // En çok oy alanı bul
        val votes = _gameState.value.votingResults
        val totalPlayers = _gameState.value.players.count { it.isAlive && !it.isDying }

        if (skipVoteCount > totalPlayers / 2) {
            _gameState.update {
                it.copy(
                    currentPhase = GamePhase.DAY_VOTE_RESULT,
                    accusedId = null,
                    votingResults = emptyMap()
                )
            }
            return
        }

        if (votes.isEmpty()) {
            _gameState.update {
                it.copy(
                    currentPhase = GamePhase.DAY_VOTE_RESULT,
                    accusedId = null,
                    votingResults = emptyMap()
                )
            }
            // Hiç oy yoksa sonraki güne geç
            return
        }

        val maxVotes = votes.values.maxOrNull()
        val topCandidates = votes.filter { it.value == maxVotes }.keys
        val accused = if (topCandidates.size == 1) topCandidates.first() else null

        _gameState.update {
            it.copy(
                currentPhase = GamePhase.DAY_VOTE_RESULT,
                accusedId = accused,
                votingResults = emptyMap()
            )
        }

    }

    fun skipAccusation() {
        proceedToNight()
    }

    /**
     * Yeni geceye geç
     */
    private fun proceedToNight() {
        _gameState.update {
            it.copy(
                currentPhase = GamePhase.NIGHT,
                // currentDay = it.currentDay + 1, geri alınabilir..
                lastEliminated = null,
                accusedId = null
            )
        }

        votedPlayers.clear()
        skipVoteCount = 0

        // İlk hayattaki oyuncuyu aktif yap
        val firstAlivePlayer = _gameState.value.players.firstOrNull { it.isAlive }

        if (firstAlivePlayer != null) {
            _activePlayer.value = firstAlivePlayer
        } else {
            // Vampir yoksa seri katil, şerif, gözcü veya doktor var mı kontrol et
            checkNextSpecialRole()
        }
    }

    /**
     * Oyun bitişine geç
     */
    private fun proceedToGameOver() {
        val players = _gameState.value.players
        val vampiresWin =
            players.none {
                it.isAlive && (
                        it.role == PlayerRole.VILLAGER ||
                                it.role == PlayerRole.SHERIFF ||
                                it.role == PlayerRole.WATCHER ||
                                it.role == PlayerRole.DOCTOR ||
                                it.role == PlayerRole.VOTE_SABOTEUR ||
                                it.role == PlayerRole.AUTOPSIR ||
                                it.role == PlayerRole.VETERAN ||
                                it.role == PlayerRole.MADMAN ||
                                it.role == PlayerRole.WIZARD
                        )
            }
        val villagersWin =
            players.none { it.isAlive && (it.role == PlayerRole.VAMPIRE || it.role == PlayerRole.SERIAL_KILLER) }
        val serialKillerWin =
            players.count { it.isAlive } == 1 && players.any { it.isAlive && it.role == PlayerRole.SERIAL_KILLER }

        if (vampiresWin || villagersWin || serialKillerWin) {
            val winningRole = when {
                serialKillerWin -> PlayerRole.SERIAL_KILLER
                vampiresWin -> PlayerRole.VAMPIRE
                else -> PlayerRole.VILLAGER
            }

            val gameResult = GameResult(
                winningRole = winningRole,
                alivePlayers = players.filter { it.isAlive }
            )

            _gameState.update {
                it.copy(
                    currentPhase = GamePhase.GAME_OVER,
                    gameResult = gameResult
                )
            }
        }
    }

    /**
     * Oyuncu durumunu güncelle
     */
    private fun updatePlayerStatus(playerId: Int, isAlive: Boolean, isDying: Boolean = false) {
        val updatedPlayers = _gameState.value.players.map { player ->
            if (player.id == playerId) {
                player.copy(isAlive = isAlive, isDying = isDying)
            } else {
                player
            }
        }

        _gameState.update {
            it.copy(players = updatedPlayers)
        }
    }


    /**
     * Sıradaki oyuncuya geç
     */
    private fun nextPlayer() {
        // Sadece hayatta olan ve ölmekte olmayan oyuncuları al
        val activePlayers = _gameState.value.players.filter { it.isAlive && !it.isDying }
        val currentIndex = activePlayers.indexOfFirst { it.id == _activePlayer.value?.id }

        if (currentIndex < activePlayers.size - 1) {
            _activePlayer.value = activePlayers[currentIndex + 1]
        }
    }

    /**
     * Tüm oyuncular oy verdi mi?
     */
    private fun allPlayersVoted(): Boolean {
        // Sadece hayatta olan ve ölmekte olmayan oyuncuların oy vermesini bekle
        val activePlayerCount = _gameState.value.players.count { it.isAlive && !it.isDying }
        println("ActivePlayerCount: " + activePlayerCount + " votedPlayers.size: " + votedPlayers.size)
        return votedPlayers.size >= activePlayerCount
    }

    /**
     * Oyun bitti mi kontrol et
     */
    private fun checkGameOver(): Boolean {
        // Köylü takımında kimse kalmadı mı? (Köylü, Şerif, Gözcü, Doktor)
        val players = _gameState.value.players
        val noVillageTeam = players.none {
            it.isAlive && (
                    it.role == PlayerRole.VILLAGER ||
                            it.role == PlayerRole.SHERIFF ||
                            it.role == PlayerRole.WATCHER ||
                            it.role == PlayerRole.DOCTOR ||
                            it.role == PlayerRole.VOTE_SABOTEUR ||
                            it.role == PlayerRole.AUTOPSIR ||
                            it.role == PlayerRole.VETERAN ||
                            it.role == PlayerRole.MADMAN ||
                            it.role == PlayerRole.WIZARD
                    )
        }

        // Vampir kalmadı mı?
        val noVampires = players.none { it.isAlive && it.role == PlayerRole.VAMPIRE }

        // Seri katil kazanma durumu: Sadece bir kişi hayatta ve o seri katil
        val serialKillerWin = players.count { it.isAlive } == 1 &&
                players.any { it.isAlive && it.role == PlayerRole.SERIAL_KILLER }

        return noVillageTeam || noVampires || serialKillerWin
    }

    // Gece hedefi belirleyip ziyareti kaydeden yardımcı fonksiyon
    private fun setTargetWithVisit(
        targetId: Int,
        activePlayer: Player,
        update: (GameState) -> GameState
    ) {
        _gameState.update(update)
        addNightVisit(NightVisit(activePlayer.id, targetId))
        checkNextSpecialRole()
    }

    // Gece hedefi belirleyip ziyaretsiz ilerleyen yardımcı fonksiyon
    private fun setTarget(targetId: Int, update: (GameState) -> GameState) {
        _gameState.update(update)
        checkNextSpecialRole()
    }

    // Gözcü hedefini güncelleyen yardımcı fonksiyon
    private fun setWatcherTarget(watcherId: Int, targetId: Int) {
        _gameState.update {
            it.copy(watcherTargets = it.watcherTargets + (watcherId to targetId))
        }
        checkNextSpecialRole()
    }

    // Doktor hedefini güncelleyen yardımcı fonksiyon
    private fun setDoctorTarget(doctorId: Int, targetId: Int) {
        _gameState.update {
            it.copy(doctorTargets = it.doctorTargets + (doctorId to targetId))
        }
        addNightVisit(NightVisit(doctorId, targetId))
        checkNextSpecialRole()
    }

    // Ziyareti kaydeden yardımcı fonksiyon
    private fun addNightVisit(visit: NightVisit) {
        _gameState.update {
            it.copy(nightVisits = it.nightVisits + visit)
        }
    }

    private fun applyWizardSwap(visits: List<NightVisit>): List<NightVisit> {
        val swaps = _gameState.value.wizardSwaps.values
        if (swaps.isEmpty()) return visits

        return swaps.fold(visits) { acc, swap ->
            acc.map { visit ->
                val newTarget = when (visit.targetId) {
                    swap.first -> swap.second
                    swap.second -> swap.first
                    else -> visit.targetId
                }
                visit.copy(targetId = newTarget)
            }
        }
    }

    // Sonraki özel rolü kontrol eder
    private fun checkNextSpecialRole() {
        // Sadece canlı ve ölmekte olmayan oyuncular
        val activePlayers = _gameState.value.players.filter { it.isAlive && !it.isDying }
        val currentPlayerId = _activePlayer.value?.id ?: -1
        println("DEBUG: checkNextSpecialRole - Mevcut oyuncu ID: $currentPlayerId")
        // Oyuncu listesinde sıradaki oyuncuyu bul
        val nextPlayer = activePlayers.find { it.id > currentPlayerId }

        if (nextPlayer != null) {
            println("DEBUG: checkNextSpecialRole - Sıradaki oyuncu: ${nextPlayer.name} (${nextPlayer.role}) - ID: ${nextPlayer.id}")
            // Sıradaki oyuncuyu aktif yap
            _activePlayer.value = nextPlayer
            return
        }
        println("DEBUG: checkNextSpecialRole - Sıradaki oyuncu bulunamadı, gece fazı tamamlanıyor")

        // Eğer sıradaki oyuncu yoksa (son oyuncuya gelinmişse), gece fazını tamamla
        finishNightPhase()
    }

    // Gece fazını tamamlayıp sonuçları hesaplar
    private fun finishNightPhase() {
        val visits = applyWizardSwap(_gameState.value.nightVisits)

        // Gözcü sonuçlarını hesapla
        val watcherTargets = _gameState.value.watcherTargets
        if (watcherTargets.isNotEmpty()) {
            val currentResults = _gameState.value.watcherResults.toMutableMap()
            watcherTargets.forEach { (watcherId, targetId) ->
                val visitors = visits.filter { it.targetId == targetId }
                    .map { it.visitorId }

                val observation = WatcherObservation(
                    targetId = targetId,
                    visitorIds = visitors
                )

                val list = currentResults[watcherId].orEmpty() + observation
                currentResults[watcherId] = list
            }

            _gameState.update { it.copy(watcherResults = currentResults) }
        }

        // Ölümleri hesapla
        processNightKills(visits)

        // Gece sonuçları fazına geç
        proceedToNightResults()
    }

    // Gece sonuçları fazına geç
    private fun proceedToNightResults() {
        _gameState.update {
            it.copy(currentPhase = GamePhase.NIGHT_RESULT)
        }

        // İlk oyuncuyu aktif yap (gece sonuçlarını göstermek için)
        // Tüm oyuncular (canlı veya ölü) arasından ilkini seç
        val alivePlayers = _gameState.value.players.filter { it.isAlive }
        _activePlayer.value = alivePlayers.firstOrNull()
    }

    fun proceedToNextNightResult() {
        val allPlayers = _gameState.value.players.filter { it.isAlive }
        println("allPlayers:$allPlayers")
        val currentIndex = allPlayers.indexOfFirst { it.id == _activePlayer.value?.id }
        println("CurrentIndex: $currentIndex")
        if (currentIndex < allPlayers.size - 1) {
            // Sıradaki oyuncuyu aktif yap (ölü oyuncular dahil)
            _activePlayer.value = allPlayers[currentIndex + 1]
        } else {
            // Tüm oyuncular gece sonuçlarını görmüşse gündüz fazına geç
            proceedToDay()
        }
    }

    // Gece ölümlerini hesapla
    private fun processNightKills(visits: List<NightVisit>) {
        var vampireTarget = _gameState.value.nightTarget
        var doctorTargets = _gameState.value.doctorTargets.values.toMutableSet()
        var serialKillerTarget = _gameState.value.serialKillerTarget

        // Büyücünün yer değiştirme etkisi
        _gameState.value.wizardSwaps.values.forEach { (first, second) ->
            fun swap(id: Int?): Int? = when (id) {
                first -> second
                second -> first
                else -> id
            }
            vampireTarget = swap(vampireTarget)
            serialKillerTarget = swap(serialKillerTarget)
            doctorTargets = doctorTargets.map { swap(it) ?: it }.toMutableSet()
        }

        // Vampir kurbanı
        if (vampireTarget != null && vampireTarget !in doctorTargets) {
            updatePlayerStatus(vampireTarget, isAlive = true, isDying = true)
        }

        // Seri katil kurbanı
        if (serialKillerTarget != null && serialKillerTarget !in doctorTargets) {
            updatePlayerStatus(serialKillerTarget, isAlive = true, isDying = true)
        }

        // Nöbetçi uyanıksa ziyaretçileri öldür
        _gameState.value.veteranAlertIds.forEach { vetId ->
            visits.filter { it.targetId == vetId }.forEach { visit ->
                updatePlayerStatus(visit.visitorId, isAlive = true, isDying = true)
            }
        }
    }

    /**
     * Oylamayı atla (hiç kimseye oy vermeden geç)
     */
    fun skipVote() {
        val active = _activePlayer.value ?: return

        // Oy kullanmayan oyuncuyu kaydet
        votedPlayers.add(active.id)
        skipVoteCount++

        // Sıradaki oyuncuya geç
        nextPlayer()

        // Tüm oyuncular oy verdiyse sonuç fazına geç
        if (allPlayersVoted()) {
            proceedToVoteResult()
        }
    }

    /**
     * 3 dakikalık geri sayım sonrası yargılama aşamasına geçer
     */
    fun startJudgement() {
        _gameState.update {
            it.copy(
                currentPhase = GamePhase.JUDGEMENT,
                judgementVotes = emptyMap()
            )
        }

        // İlk oylayan oyuncuyu ata (suçlanan hariç)
        val accused = _gameState.value.accusedId
        val first =
            _gameState.value.players.firstOrNull { it.isAlive && !it.isDying && it.id != accused }
        _activePlayer.value = first
    }

    /**
     * Oyuncuların suçlu/masum kararını kaydeder
     */
    fun submitJudgementVote(isGuilty: Boolean) {
        val accusedId = _gameState.value.accusedId ?: return
        val active = _activePlayer.value ?: return

        _gameState.update {
            it.copy(judgementVotes = it.judgementVotes + (active.id to isGuilty))
        }

        val voters =
            _gameState.value.players.filter { it.isAlive && !it.isDying && it.id != accusedId }
        val currentIndex = voters.indexOfFirst { it.id == active.id }
        if (currentIndex < voters.size - 1) {
            _activePlayer.value = voters[currentIndex + 1]
        } else {
            finalizeJudgement()
        }
    }

    // Yargılamayı sonuçlandırır
    private fun finalizeJudgement() {
        val accusedId = _gameState.value.accusedId ?: return
        val votes = _gameState.value.judgementVotes
        val guiltyCount = votes.values.count { it }
        val guilty = guiltyCount > votes.size / 2

        if (guilty) {
            updatePlayerStatus(accusedId, isAlive = false)
            _gameState.update { it.copy(lastEliminated = accusedId) }
        }

        _gameState.update {
            it.copy(
                currentPhase = GamePhase.VOTE_RESULT,
                judgementVotes = emptyMap()
            )
        }
    }
} 