package com.ee.vampirkoylu

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.ee.vampirkoylu.ui.theme.VampirKoyluTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var billingClientWrapper: BillingClientWrapper
    private lateinit var storeManager: StoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClientWrapper = BillingClientWrapper(this, lifecycleScope)
        storeManager = StoreManager(this)

        lifecycleScope.launch {
            billingClientWrapper.isPlusUser.collect { isPlusUser ->
                if (isPlusUser) {
                    storeManager.setPlusUser(true)
                }
            }
        }

        setContent {
            VampirKoyluTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        billingClientWrapper = billingClientWrapper,
                        storeManager = storeManager,
                        activity = this
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billingClientWrapper.startConnection()
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
