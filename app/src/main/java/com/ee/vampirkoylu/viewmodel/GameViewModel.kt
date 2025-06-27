package com.ee.vampirkoylu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ee.vampirkoylu.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()
    
    // Aktif oyuncu (şu anda sıra kimde)
    private val _activePlayer = MutableStateFlow<Player?>(null)
    val activePlayer: StateFlow<Player?> = _activePlayer.asStateFlow()
    
    /**
     * Oyun ayarlarını günceller
     */
    fun updateSettings(
        playerCount: Int,
        vampireCount: Int,
        sheriffCount: Int = 0,
        watcherCount: Int = 0,
        serialKillerCount: Int = 0,
        doctorCount: Int = 0
    ) {
        _settings.update { 
            it.copy(
                playerCount = playerCount.coerceIn(4, 15), // Min 4, max 15 oyuncu
                vampireCount = vampireCount.coerceIn(1, playerCount / 3), // En fazla oyuncu sayısının 1/3'ü kadar vampir
                sheriffCount = sheriffCount.coerceIn(0, 1), // En fazla 1 şerif
                watcherCount = watcherCount.coerceIn(0, 1), // En fazla 1 gözcü
                serialKillerCount = serialKillerCount.coerceIn(0, 1), // En fazla 1 seri katil
                doctorCount = doctorCount.coerceIn(0, 1) // En fazla 1 doktor
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
            val shuffledRoles = createRoles(_settings.value.playerCount, _settings.value.vampireCount)
            
            // Oyuncuları oluştur
            val newPlayers = playerNames.take(_settings.value.playerCount).mapIndexed { index, name ->
                Player(
                    id = index,
                    name = name,
                    role = shuffledRoles[index]
                )
            }
            
            // Oyun durumunu başlangıç fazına ayarla
            _players.value = newPlayers
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
    fun selectNightTarget(targetId: Int) {
        // Hedef kendisi olamaz
        if (targetId == _activePlayer.value?.id) {
            return
        }
        
        val activePlayer = _activePlayer.value ?: return
        
        // Aktif oyuncunun rolüne göre hedefi ayarla
        when (activePlayer.role) {
            PlayerRole.VAMPIRE -> {
                _gameState.update { 
                    it.copy(nightTarget = targetId)
                }
                
                // Ziyareti kaydet
                val visit = NightVisit(activePlayer.id, targetId)
                addNightVisit(visit)
                
                // Sıradaki vampire geç veya fazı tamamla
                if (allVampiresActed()) {
                    // Seri katil, Şerif, Gözcü veya Doktor var mı kontrol et
                    checkNextSpecialRole()
                } else {
                    nextVampire()
                }
            }
            PlayerRole.SERIAL_KILLER -> {
                _gameState.update { 
                    it.copy(serialKillerTarget = targetId)
                }
                
                // Ziyareti kaydet
                val visit = NightVisit(activePlayer.id, targetId)
                addNightVisit(visit)
                
                // Şerif, Gözcü veya Doktor var mı kontrol et
                checkNextSpecialRole()
            }
            PlayerRole.SHERIFF -> {
                _gameState.update { 
                    it.copy(sheriffTarget = targetId)
                }
                
                // Ziyareti kaydet
                val visit = NightVisit(activePlayer.id, targetId)
                addNightVisit(visit)
                
                // Suçlu olup olmadığını kontrol et
                val targetPlayer = _players.value.find { it.id == targetId }
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
                checkNextSpecialRole()
            }
            PlayerRole.WATCHER -> {
                _gameState.update { 
                    it.copy(watcherTarget = targetId)
                }
                
                // Ziyaret kaydet - Gözcü konumdan izlediği için ziyaret etmiş sayılmaz
                
                // Gözcü sonucunu gece sonunda oluşturacağız
                
                // Doktor var mı kontrol et
                checkNextSpecialRole()
            }
            PlayerRole.DOCTOR -> {
                _gameState.update { 
                    it.copy(doctorTarget = targetId)
                }
                
                // Ziyareti kaydet
                val visit = NightVisit(activePlayer.id, targetId)
                addNightVisit(visit)
                
                // Gece fazını tamamla
                finishNightPhase()
            }
            else -> {
                // Diğer roller için bir işlem yapılmaz
            }
        }
    }
    
    /**
     * Gündüz fazında oyuncu oyu
     */
    fun vote(targetId: Int) {
        // Kendine oy veremez
        if (targetId == _activePlayer.value?.id) {
            return
        }
        
        // Oyları güncelle
        val currentVotes = _gameState.value.votingResults.toMutableMap()
        currentVotes[targetId] = (currentVotes[targetId] ?: 0) + 1
        
        _gameState.update { 
            it.copy(votingResults = currentVotes)
        }
        
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
        when (_gameState.value.currentPhase) {
            GamePhase.DAY -> proceedToVoting()
            GamePhase.VOTE_RESULT -> {
                // Oyun bitti mi kontrol et
                if (checkGameOver()) {
                    proceedToGameOver()
                } else {
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
        _players.value = emptyList()
        _activePlayer.value = null
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
        doctorCount: Int = _settings.value.doctorCount
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
        
        // Kalan oyuncular için köylü rollerini ekle
        val specialRoleCount = vampireCount + sheriffCount + watcherCount + serialKillerCount + doctorCount
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
        _gameState.update { 
            it.copy(
                currentPhase = GamePhase.DAY,
                lastEliminated = null,  // Artık birden fazla ölüm olabileceği için
                // Gece hedeflerini temizle
                nightTarget = null,
                doctorTarget = null,
                serialKillerTarget = null,
                sheriffTarget = null,
                watcherTarget = null,
                nightVisits = emptyList() // Ziyaretleri sıfırla
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
    private fun proceedToVoting() {
        _gameState.update { 
            it.copy(
                currentPhase = GamePhase.VOTING,
                votingResults = emptyMap()
            )
        }
        
        // İlk canlı oyuncuyu aktif yap
        _activePlayer.value = _players.value.find { it.isAlive }
    }
    
    /**
     * Oylama sonucunu uygula
     */
    private fun proceedToVoteResult() {
        // En çok oy alanı bul
        val votes = _gameState.value.votingResults
        if (votes.isEmpty()) {
            // Hiç oy yoksa sonraki güne geç
            proceedToNight()
            return
        }
        
        val mostVoted = votes.maxByOrNull { it.value }?.key
        if (mostVoted != null) {
            updatePlayerStatus(mostVoted, isAlive = false)
            
            _gameState.update { 
                it.copy(
                    currentPhase = GamePhase.VOTE_RESULT,
                    lastEliminated = mostVoted,
                    votingResults = emptyMap()
                )
            }
        }
    }
    
    /**
     * Yeni geceye geç
     */
    private fun proceedToNight() {
        _gameState.update { 
            it.copy(
                currentPhase = GamePhase.NIGHT,
                currentDay = it.currentDay + 1,
                lastEliminated = null
            )
        }
        
        // İlk vampiri aktif yap
        val vampire = _players.value.find { it.isAlive && it.role == PlayerRole.VAMPIRE }
        
        if (vampire != null) {
            _activePlayer.value = vampire
        } else {
            // Vampir yoksa seri katil, şerif, gözcü veya doktor var mı kontrol et
            checkNextSpecialRole()
        }
    }
    
    /**
     * Oyun bitişine geç
     */
    private fun proceedToGameOver() {
        val vampiresWin = _players.value.none { it.isAlive && (it.role == PlayerRole.VILLAGER || it.role == PlayerRole.SHERIFF || it.role == PlayerRole.WATCHER || it.role == PlayerRole.DOCTOR) }
        val villagersWin = _players.value.none { it.isAlive && (it.role == PlayerRole.VAMPIRE || it.role == PlayerRole.SERIAL_KILLER) }
        val serialKillerWin = _players.value.count { it.isAlive } == 1 && _players.value.any { it.isAlive && it.role == PlayerRole.SERIAL_KILLER }
        
        if (vampiresWin || villagersWin || serialKillerWin) {
            val winningRole = when {
                serialKillerWin -> PlayerRole.SERIAL_KILLER
                vampiresWin -> PlayerRole.VAMPIRE
                else -> PlayerRole.VILLAGER
            }
            
            val gameResult = GameResult(
                winningRole = winningRole,
                alivePlayers = _players.value.filter { it.isAlive }
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
    private fun updatePlayerStatus(playerId: Int, isAlive: Boolean) {
        val updatedPlayers = _players.value.map { player ->
            if (player.id == playerId) {
                player.copy(isAlive = isAlive, isRevealed = true)
            } else {
                player
            }
        }
        
        _players.value = updatedPlayers
        _gameState.update { 
            it.copy(players = updatedPlayers)
        }
    }
    
    /**
     * Sıradaki vampire geç
     */
    private fun nextVampire() {
        val vampires = _players.value.filter { it.isAlive && it.role == PlayerRole.VAMPIRE }
        val currentIndex = vampires.indexOfFirst { it.id == _activePlayer.value?.id }
        
        if (currentIndex < vampires.size - 1) {
            _activePlayer.value = vampires[currentIndex + 1]
        }
    }
    
    /**
     * Sıradaki oyuncuya geç
     */
    private fun nextPlayer() {
        val alivePlayers = _players.value.filter { it.isAlive }
        val currentIndex = alivePlayers.indexOfFirst { it.id == _activePlayer.value?.id }
        
        if (currentIndex < alivePlayers.size - 1) {
            _activePlayer.value = alivePlayers[currentIndex + 1]
        }
    }
    
    /**
     * Tüm vampirler harekete geçti mi?
     */
    private fun allVampiresActed(): Boolean {
        return _activePlayer.value?.role == PlayerRole.VAMPIRE && 
               _players.value.none { it.isAlive && it.role == PlayerRole.VAMPIRE && it.id > (_activePlayer.value?.id ?: -1) }
    }
    
    /**
     * Tüm oyuncular oy verdi mi?
     */
    private fun allPlayersVoted(): Boolean {
        val alivePlayerCount = _players.value.count { it.isAlive }
        return _gameState.value.votingResults.values.sum() >= alivePlayerCount
    }
    
    /**
     * Oyun bitti mi kontrol et
     */
    private fun checkGameOver(): Boolean {
        // Köylü takımında kimse kalmadı mı? (Köylü, Şerif, Gözcü, Doktor)
        val noVillageTeam = _players.value.none { 
            it.isAlive && (it.role == PlayerRole.VILLAGER || it.role == PlayerRole.SHERIFF || 
                          it.role == PlayerRole.WATCHER || it.role == PlayerRole.DOCTOR)
        }
        
        // Vampir kalmadı mı?
        val noVampires = _players.value.none { it.isAlive && it.role == PlayerRole.VAMPIRE }
        
        // Seri katil kazanma durumu: Sadece bir kişi hayatta ve o seri katil
        val serialKillerWin = _players.value.count { it.isAlive } == 1 && 
                             _players.value.any { it.isAlive && it.role == PlayerRole.SERIAL_KILLER }
        
        return noVillageTeam || noVampires || serialKillerWin
    }
    
    // Ziyareti kaydeden yardımcı fonksiyon
    private fun addNightVisit(visit: NightVisit) {
        _gameState.update { 
            it.copy(nightVisits = it.nightVisits + visit)
        }
    }
    
    // Sonraki özel rolü kontrol eder
    private fun checkNextSpecialRole() {
        val alivePlayers = _players.value.filter { it.isAlive }
        
        // Seri katil
        if (_gameState.value.serialKillerTarget == null) {
            val serialKiller = alivePlayers.find { it.role == PlayerRole.SERIAL_KILLER }
            if (serialKiller != null) {
                _activePlayer.value = serialKiller
                return
            }
        }
        
        // Şerif
        if (_gameState.value.sheriffTarget == null) {
            val sheriff = alivePlayers.find { it.role == PlayerRole.SHERIFF }
            if (sheriff != null) {
                _activePlayer.value = sheriff
                return
            }
        }
        
        // Gözcü
        if (_gameState.value.watcherTarget == null) {
            val watcher = alivePlayers.find { it.role == PlayerRole.WATCHER }
            if (watcher != null) {
                _activePlayer.value = watcher
                return
            }
        }
        
        // Doktor
        if (_gameState.value.doctorTarget == null) {
            val doctor = alivePlayers.find { it.role == PlayerRole.DOCTOR }
            if (doctor != null) {
                _activePlayer.value = doctor
                return
            }
        }
        
        // Tüm özel roller tamamlandı, gece fazını bitir
        finishNightPhase()
    }
    
    // Gece fazını tamamlayıp sonuçları hesaplar
    private fun finishNightPhase() {
        // Gözcü sonuçlarını hesapla
        val watcherTarget = _gameState.value.watcherTarget
        if (watcherTarget != null) {
            // Hedef eve gelen ziyaretçileri bul
            val visitors = _gameState.value.nightVisits
                .filter { it.targetId == watcherTarget }
                .map { it.visitorId }
                
            if (visitors.isNotEmpty()) {
                val observation = WatcherObservation(
                    targetId = watcherTarget,
                    visitorIds = visitors
                )
                
                _gameState.update { 
                    it.copy(watcherResults = it.watcherResults + observation)
                }
            }
        }
        
        // Ölümleri hesapla
        processNightKills()
        
        // Gündüz fazına geç
        proceedToDay()
    }
    
    // Gece ölümlerini hesapla
    private fun processNightKills() {
        val vampireTarget = _gameState.value.nightTarget
        val doctorTarget = _gameState.value.doctorTarget
        val serialKillerTarget = _gameState.value.serialKillerTarget
        
        // Vampir kurbanı
        if (vampireTarget != null && vampireTarget != doctorTarget) {
            updatePlayerStatus(vampireTarget, isAlive = false)
        }
        
        // Seri katil kurbanı
        if (serialKillerTarget != null && serialKillerTarget != doctorTarget) {
            updatePlayerStatus(serialKillerTarget, isAlive = false)
        }
    }
} 