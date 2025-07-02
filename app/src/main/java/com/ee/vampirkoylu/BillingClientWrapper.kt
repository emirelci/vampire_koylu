package com.ee.vampirkoylu

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingClientWrapper(private val context: Context, private val coroutineScope: CoroutineScope) {

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products = _products.asStateFlow()

    private val _isPlusUser = MutableStateFlow(false)
    val isPlusUser = _isPlusUser.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                coroutineScope.launch {
                    handlePurchase(purchase)
                }
            }
        } else {
            // Handle other error codes from purchasesUpdatedListener.
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().build())
        .build()

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    coroutineScope.launch {
                        queryProducts()
                        queryPurchases()
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private suspend fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("plus_package")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        productDetailsResult.productDetailsList?.let {
            _products.value = it
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams)
                }
            }
            _isPlusUser.value = true
        }
    }

    private suspend fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val purchasesResult = billingClient.queryPurchasesAsync(params)

        // Process the result
        for (purchase in purchasesResult.purchasesList) {
            handlePurchase(purchase)
        }
    }
}
