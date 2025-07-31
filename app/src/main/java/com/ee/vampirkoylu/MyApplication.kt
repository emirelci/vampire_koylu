package com.ee.vampirkoylu

import android.app.Application
import com.ee.vampirkoylu.util.LanguageManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Uygulama başlarken dil ayarını uygula
        LanguageManager.initializeLanguage(this)
    }
}
