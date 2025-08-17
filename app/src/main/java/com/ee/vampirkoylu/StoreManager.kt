package com.ee.vampirkoylu

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        val IS_PLUS_USER = booleanPreferencesKey("is_plus_user")
        val IS_TRIAL_AVAILABLE = booleanPreferencesKey("is_trial_available")
        val LAST_TRIAL_DATE = longPreferencesKey("last_trial_date")
        private const val TRIAL_COOLDOWN_MS = 7 * 24 * 60 * 60 * 1000L // 1 hafta
    }

    val isPlusUser: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_PLUS_USER] ?: false
        }

    val isTrialAvailable: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isPlusUser = preferences[IS_PLUS_USER] ?: false
            val isTrialAvailable = preferences[IS_TRIAL_AVAILABLE] ?: false
            
            // Plus user ise trial'a ihtiyacı yok
            if (isPlusUser) return@map false
            
            // Plus user değil ve trial hakkı var
            !isPlusUser && isTrialAvailable
        }

    val isTrialUsed: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_TRIAL_AVAILABLE] ?: false
        }

    val isPlusUserOrTrialUsed: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isPlusUser = preferences[IS_PLUS_USER] ?: false
            val isTrialAvailable = preferences[IS_TRIAL_AVAILABLE] ?: false
            
            // Plus user ise veya trial hakkı varsa premium özellikleri kullanabilir
            isPlusUser || isTrialAvailable
        }

    suspend fun setPlusUser(isPlus: Boolean) {
        context.dataStore.edit { settings ->
            settings[IS_PLUS_USER] = isPlus
        }
    }

    suspend fun grantTrialRight() {
        // Reklam izlendikten sonra trial hakkı verilir
        context.dataStore.edit { settings ->
            settings[IS_TRIAL_AVAILABLE] = true
        }
    }

    suspend fun useTrialRight() {
        // GameSetupScreen'de oyun başlatıldığında trial hakkı tüketilir
        val currentTime = System.currentTimeMillis()
        context.dataStore.edit { settings ->
            settings[IS_TRIAL_AVAILABLE] = false
            settings[LAST_TRIAL_DATE] = currentTime
        }
    }

    suspend fun resetTrialUsage() {
        // Bu fonksiyon artık kullanılmayacak çünkü trial tek kullanımlık
        // Sadece eski compatibility için bırakıyoruz
    }

    suspend fun canShowTrialOffer(): Boolean {
        val preferences = context.dataStore.data.first()
        val isPlusUser = preferences[IS_PLUS_USER] ?: false
        val lastTrialDate = preferences[LAST_TRIAL_DATE] ?: 0L
        val isTrialAvailable = preferences[IS_TRIAL_AVAILABLE] ?: false
        val currentTime = System.currentTimeMillis()
        
        // Plus user değil ve (hiç trial kullanmamış veya 1 hafta geçmiş) ve şu an trial hakkı yok
        return !isPlusUser && 
               (lastTrialDate == 0L || (currentTime - lastTrialDate) >= TRIAL_COOLDOWN_MS) && 
               !isTrialAvailable
    }
}
