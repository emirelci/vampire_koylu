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
    fun updateSettings(playerCount: Int, vampireCount: Int) {
        _settings.update { 
            it.copy(
                playerCount = playerCount.coerceIn(4, 15), // Min 4, max 15 oyuncu
                vampireCount = vampireCount.coerceIn(1, playerCount / 3) // En fazla oyuncu sayısının 1/3'ü kadar vampir
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
        
        _gameState.update { 
            it.copy(nightTarget = targetId)
        }
        
        // Tüm vampirler hedef seçtiyse gündüz fazına geç
        if (allVampiresActed()) {
            proceedToDay()
        } else {
            // Sıradaki vampire geç
            nextVampire()
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
    private fun createRoles(playerCount: Int, vampireCount: Int): List<PlayerRole> {
        val roles = mutableListOf<PlayerRole>()
        
        // Vampir rollerini ekle
        repeat(vampireCount) {
            roles.add(PlayerRole.VAMPIRE)
        }
        
        // Köylü rollerini ekle
        repeat(playerCount - vampireCount) {
            roles.add(PlayerRole.VILLAGER)
        }
        
        // Rolleri karıştır
        return roles.shuffled()
    }
    
    /**
     * Gece fazından gündüz fazına geç
     */
    private fun proceedToDay() {
        // Gece hedefinin durumunu güncelle
        val targetId = _gameState.value.nightTarget
        if (targetId != null) {
            updatePlayerStatus(targetId, isAlive = false)
        }
        
        _gameState.update { 
            it.copy(
                currentPhase = GamePhase.DAY,
                lastEliminated = targetId,
                nightTarget = null
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
        _activePlayer.value = _players.value.find { it.isAlive && it.role == PlayerRole.VAMPIRE }
    }
    
    /**
     * Oyun bitişine geç
     */
    private fun proceedToGameOver() {
        val vampiresWin = _players.value.none { it.isAlive && it.role == PlayerRole.VILLAGER }
        val villagersWin = _players.value.none { it.isAlive && it.role == PlayerRole.VAMPIRE }
        
        if (vampiresWin || villagersWin) {
            val winningRole = if (vampiresWin) PlayerRole.VAMPIRE else PlayerRole.VILLAGER
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
        val noVampires = _players.value.none { it.isAlive && it.role == PlayerRole.VAMPIRE }
        val noVillagers = _players.value.none { it.isAlive && it.role == PlayerRole.VILLAGER }
        
        return noVampires || noVillagers
    }
} 