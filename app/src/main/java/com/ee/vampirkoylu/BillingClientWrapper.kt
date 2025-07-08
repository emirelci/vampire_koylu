import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.*
import com.ee.vampirkoylu.StoreManager
import com.ee.vampirkoylu.util.PurchaseValidator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class BillingClientWrapper(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val storeManager: StoreManager
) {

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products = _products.asStateFlow()

    private val _isPlusUser = MutableStateFlow(false)
    val isPlusUser = _isPlusUser.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                coroutineScope.launch { handlePurchase(purchase) }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            showToast("Purchase canceled")
        } else {
            showToast("Purchase failed")
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().enablePrepaidPlans().build())        .enableAutoServiceReconnection()
        .build()

    init {
        coroutineScope.launch {
            storeManager.isPlusUser.collect { _isPlusUser.value = it }
        }
    }

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isConnected.value = true
                    coroutineScope.launch {
                        queryProducts()
                        queryPurchases()
                    }
                } else {
                    _isConnected.value = false
                    showToast("Billing setup failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                _isConnected.value = false
                showToast("Billing service disconnected")
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

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        try {
            val result = billingClient.queryProductDetails(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                result.productDetailsList?.let {
                    _products.value = it
                }
            } else {
                showToast("Product query failed")
            }
        } catch (e: Exception) {
            showToast("Error querying products")
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) {
            showToast("Billing client not ready for purchase")
            return
        }

        val offerToken = productDetails.oneTimePurchaseOfferDetails?.offerToken ?: run {
            showToast("Product error")
            return
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .setIsOfferPersonalized(false)
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            showToast("Billing flow failed")
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            if (!PurchaseValidator.verifyPurchase(purchase)) {
                showToast("Purchase verification failed")
                return
            }
            try {
                val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                val result = billingClient.acknowledgePurchase(acknowledgeParams)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    storeManager.setPlusUser(true)
                    _isPlusUser.value = true
                } else {
                    showToast("Acknowledgment failed")
                }
            } catch (e: Exception) {
                showToast("Error handling purchase")
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            showToast("Purchase pending")
        }
    }

    private suspend fun queryPurchases() {
        try {
            val result = billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            val valid = result.purchasesList.any {
                it.products.contains("plus_package") &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED &&
                        PurchaseValidator.verifyPurchase(it)
            }

            _isPlusUser.value = valid
            storeManager.setPlusUser(valid)

            for (purchase in result.purchasesList) {
                if (purchase.products.contains("plus_package")) {
                    handlePurchase(purchase)
                }
            }

        } catch (e: Exception) {
            showToast("Error querying purchases")
        }
    }

    fun disconnect() {
        billingClient.endConnection()
        _isConnected.value = false
    }
}
