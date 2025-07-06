package com.ee.vampirkoylu

import BillingClientWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.ui.navigation.NavGraph
import com.ee.vampirkoylu.ui.theme.VampirKoyluTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private lateinit var billingClientWrapper: BillingClientWrapper
    private lateinit var storeManager: StoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()

        MobileAds.initialize(this)

        // StoreManager'ı önce oluştur
        storeManager = StoreManager(this)

        // BillingClientWrapper'ı storeManager ile birlikte oluştur
        billingClientWrapper = BillingClientWrapper(this, lifecycleScope, storeManager)


        setContent {
            VampirKoyluTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
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

@Composable
fun MainScreen(
    billingClientWrapper: BillingClientWrapper,
    storeManager: StoreManager,
    activity: MainActivity
) {
    val products by billingClientWrapper.products.collectAsState()
    val isPlusUser by storeManager.isPlusUser.collectAsState(initial = false)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = if (isPlusUser) "Plus User" else "Welcome!")

        if (!isPlusUser) {
            Button(onClick = {
                products.find { it.productId == "plus_package" }?.let {
                    billingClientWrapper.launchPurchaseFlow(activity, it)
                }
            }) {
                Text(text = "Buy Plus Package")
            }
        }
    }
}
