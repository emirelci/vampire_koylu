package com.ee.vampirkoylu.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import java.util.Locale

object LanguageManager {
    private const val LANGUAGE_KEY = "app_language"
    
    fun setLanguage(context: Context, languageCode: String) {
        Log.d("LanguageManager", "setLanguage called with: $languageCode, Android version: ${Build.VERSION.SDK_INT}")
        
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPref.edit().putString(LANGUAGE_KEY, languageCode).apply()
        
        // Her zaman eski API'yi kullan - daha güvenilir
        Log.d("LanguageManager", "Using legacy API for all Android versions")
        applyLanguageOldApi(context, languageCode)
        
        // Activity'yi yeniden başlat
        if (context is Activity) {
            Log.d("LanguageManager", "Recreating activity")
            context.recreate()
        }
    }
    
    fun getCurrentLanguage(context: Context): String {
        // Her zaman SharedPreferences'tan al
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val lang = sharedPref.getString(LANGUAGE_KEY, getSystemLanguage()) ?: getSystemLanguage()
        Log.d("LanguageManager", "getCurrentLanguage: $lang")
        return lang
    }
    
    private fun getSystemLanguage(): String {
        return if (Locale.getDefault().language == "tr") "tr" else "en"
    }
    
    private fun applyLanguageOldApi(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        
        // Modern approach for applying locale
        context.createConfigurationContext(configuration)
        
        // For backwards compatibility
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    
    fun applyLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ için yeni API kullanılıyor, ek bir şey yapmaya gerek yok
            return
        } else {
            applyLanguageOldApi(context, languageCode)
        }
    }
    
    fun initializeLanguage(context: Context) {
        val savedLanguage = getCurrentLanguage(context)
        Log.d("LanguageManager", "initializeLanguage with: $savedLanguage")
        applyLanguageOldApi(context, savedLanguage)
    }
    
    // Utility function to get localized context
    fun getLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
