package com.ee.vampirkoylu

import BillingClientWrapper
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.ui.navigation.NavGraph
import com.ee.vampirkoylu.ui.theme.VampirKoyluTheme
import com.ee.vampirkoylu.util.AdvertisingIdUtils
import com.ee.vampirkoylu.util.LanguageManager
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var billingClientWrapper: BillingClientWrapper
    private lateinit var storeManager: StoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dil ayarını uygula
        LanguageManager.initializeLanguage(this)
        val currentLang = LanguageManager.getCurrentLanguage(this)
        Log.d("MainActivity", "Current language: $currentLang")

        // Modern edge-to-edge implementation
        enableEdgeToEdge()
        
        // Ensure portrait orientation on all devices
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Configure window for edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Configure system bars for dark theme
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

        actionBar?.hide()

        MobileAds.initialize(this)

        // StoreManager'ı önce oluştur
        storeManager = StoreManager(this)

        // BillingClientWrapper'ı storeManager ile birlikte oluştur
        billingClientWrapper = BillingClientWrapper(this, lifecycleScope, storeManager)

        lifecycleScope.launch {
            AdvertisingIdUtils.getAdvertisingId(this@MainActivity)
        }

        setContent {
            VampirKoyluTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(), // Handle system bars padding
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // NavGraph'ı kullan
                    NavGraph.SetupNavGraph(
                        navController = navController,
                        billingClientWrapper = billingClientWrapper,
                        storeManager = storeManager,
                        activity = this@MainActivity
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Bağlantı NavGraph'ta otomatik olarak yönetiliyor
        // Ama yine de manuel olarak da başlatabilirsiniz
        if (!billingClientWrapper.isConnected.value) {
            billingClientWrapper.startConnection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingClientWrapper.disconnect()
    }
}