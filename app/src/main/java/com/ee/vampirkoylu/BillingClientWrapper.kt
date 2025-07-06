import android.app.Activity
import android.content.Context
import android.util.Log
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

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                coroutineScope.launch { handlePurchase(purchase) }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i("BillingWrapper", "User canceled the purchase")
        } else {
            Log.e("BillingWrapper", "Purchase failed: ${billingResult.debugMessage}")
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
                    Log.e("BillingWrapper", "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                _isConnected.value = false
                Log.w("BillingWrapper", "Billing service disconnected")
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
                    Log.d("BillingWrapper", "Products loaded: ${it.size}")
                }
            } else {
                Log.e("BillingWrapper", "Product query failed: ${result.billingResult.debugMessage}")
            }
        } catch (e: Exception) {
            Log.e("BillingWrapper", "Exception in queryProducts", e)
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) {
            Log.w("BillingWrapper", "Billing client not ready for purchase")
            return
        }

        val offerToken = productDetails.oneTimePurchaseOfferDetails?.offerToken ?: run {
            Log.e("BillingWrapper", "No offer token found for product")
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
            Log.e("BillingWrapper", "Billing flow failed: ${result.debugMessage}")
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            if (!PurchaseValidator.verifyPurchase(purchase)) {
                Log.e("BillingWrapper", "Purchase signature verification failed")
                return
            }
            try {
                val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                val result = billingClient.acknowledgePurchase(acknowledgeParams)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingWrapper", "Purchase acknowledged")
                    storeManager.setPlusUser(true)
                    _isPlusUser.value = true
                } else {
                    Log.e("BillingWrapper", "Acknowledgment failed: ${result.debugMessage}")
                }
            } catch (e: Exception) {
                Log.e("BillingWrapper", "Exception in handlePurchase", e)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Log.i("BillingWrapper", "Purchase is pending")
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
            Log.e("BillingWrapper", "Exception in queryPurchases", e)
        }
    }

    fun disconnect() {
        billingClient.endConnection()
        _isConnected.value = false
    }
}
