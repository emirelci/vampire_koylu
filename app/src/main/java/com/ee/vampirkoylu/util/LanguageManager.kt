package com.ee.vampirkoylu.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager
import java.util.Locale

object LanguageManager {
    private const val LANGUAGE_KEY = "app_language"
    
    fun setLanguage(context: Context, languageCode: String) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPref.edit().putString(LANGUAGE_KEY, languageCode).apply()
        
        // Activity'yi yeniden başlat
        if (context is Activity) {
            context.recreate()
        }
    }
    
    fun getCurrentLanguage(context: Context): String {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getString(LANGUAGE_KEY, getSystemLanguage()) ?: getSystemLanguage()
    }
    
    private fun getSystemLanguage(): String {
        return if (Locale.getDefault().language == "tr") "tr" else "en"
    }
    
    fun applyLanguage(context: Context, languageCode: String) {
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
    
    fun initializeLanguage(context: Context) {
        val savedLanguage = getCurrentLanguage(context)
        applyLanguage(context, savedLanguage)
    }
    
    // Utility function to get localized context
    fun getLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
