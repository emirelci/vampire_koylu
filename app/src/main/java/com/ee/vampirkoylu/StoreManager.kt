package com.ee.vampirkoylu

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        val IS_PLUS_USER = booleanPreferencesKey("is_plus_user")
    }

    val isPlusUser: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_PLUS_USER] ?: false
        }

    suspend fun setPlusUser(isPlus: Boolean) {
        context.dataStore.edit { settings ->
            settings[IS_PLUS_USER] = isPlus
        }
    }
}
