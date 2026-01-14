package br.com.webstorage.falaserio.domain.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.ktx.queryProductDetails
import com.android.billingclient.ktx.queryPurchasesAsync
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Gerenciador de compras in-app. Versão final usando as extensões KTX (o jeito certo).
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient
    private var purchaseCallback: ((Result<Purchase>) -> Unit)? = null
    private val purchaseMutex = Mutex()

    companion object {
        // Agora usa a configuração centralizada
        val INAPP_PRODUCTS get() = MonetizationConfig.INAPP_PRODUCT_IDS
        val SUBS_PRODUCTS get() = MonetizationConfig.SUBS_PRODUCT_IDS
    }

    init {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases() // Restaurado: o erro era um fantasma do cache.
            .build()
        connect()
    }

    private fun connect() {
        if (billingClient.isReady) return
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {}
            override fun onBillingServiceDisconnected() { connect() }
        })
    }
    
    private suspend fun ensureConnection(): Boolean {
        if (billingClient.isReady) return true
        return suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (continuation.isActive) continuation.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                }
                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive) continuation.resume(false)
                }
            })
        }
    }

    suspend fun getAvailableProducts(): List<ProductDetails> = withContext(Dispatchers.IO) {
        if (!ensureConnection()) return@withContext emptyList()

        val inAppParams = QueryProductDetailsParams.newBuilder().setProductList(
            INAPP_PRODUCTS.map { QueryProductDetailsParams.Product.newBuilder().setProductId(it).setProductType(BillingClient.ProductType.INAPP).build() }
        ).build()
        val subsParams = QueryProductDetailsParams.newBuilder().setProductList(
            SUBS_PRODUCTS.map { QueryProductDetailsParams.Product.newBuilder().setProductId(it).setProductType(BillingClient.ProductType.SUBS).build() }
        ).build()

        // Usando as funções suspend KTX diretamente - o jeito certo.
        val inAppProducts = billingClient.queryProductDetails(inAppParams).productDetailsList ?: emptyList()
        val subProducts = billingClient.queryProductDetails(subsParams).productDetailsList ?: emptyList()

        return@withContext inAppProducts + subProducts
    }

    suspend fun purchase(activity: Activity, productDetails: ProductDetails): Result<Purchase> = purchaseMutex.withLock {
        suspendCancellableCoroutine { continuation ->
            this.purchaseCallback = { result -> if (continuation.isActive) continuation.resume(result) }
            val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply { if (offerToken != null) setOfferToken(offerToken) }
                .build()
            val flowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(productParams)).build()
            billingClient.launchBillingFlow(activity, flowParams)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases?.forEach { handlePurchase(it) }
            BillingClient.BillingResponseCode.USER_CANCELED -> purchaseCallback?.invoke(Result.failure(Exception("Compra cancelada.")))
            else -> purchaseCallback?.invoke(Result.failure(Exception("Erro na compra: ${billingResult.debugMessage}")))
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val ackParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient.acknowledgePurchase(ackParams) { ackResult ->
                    if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        val productId = purchase.products.firstOrNull()
                        if (INAPP_PRODUCTS.contains(productId)) {
                            // Consumable: aguardar consume antes de notificar sucesso
                            consumePurchaseAndNotify(purchase)
                        } else {
                            // Subscription: notificar sucesso imediatamente
                            purchaseCallback?.invoke(Result.success(purchase))
                        }
                    } else {
                        purchaseCallback?.invoke(Result.failure(Exception("Erro ao validar compra: ${ackResult.debugMessage}")))
                    }
                }
            } else {
                purchaseCallback?.invoke(Result.success(purchase))
            }
        } else {
            // Purchase state não é PURCHASED - notificar erro
            purchaseCallback?.invoke(Result.failure(Exception("Estado de compra inválido: ${purchase.purchaseState}")))
        }
    }

    /**
     * Consome a compra (para produtos consumíveis) e notifica sucesso após o consume completar.
     * Isso evita race condition onde o callback era chamado antes do consume terminar.
     */
    private fun consumePurchaseAndNotify(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            // Notificar sucesso após consume completar (independente do resultado do consume)
            // Pois acknowledge já foi feito - a compra é válida
            purchaseCallback?.invoke(Result.success(purchase))
        }
    }

    /**
     * Restaura compras anteriores do usuário.
     * Deve ser chamado ao iniciar o app para verificar assinaturas ativas e compras permanentes.
     */
    suspend fun restorePurchases(): List<Purchase> = withContext(Dispatchers.IO) {
        if (!ensureConnection()) return@withContext emptyList()

        val inAppPurchases = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ).purchasesList

        val subsPurchases = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ).purchasesList

        return@withContext (inAppPurchases + subsPurchases).filter {
            it.purchaseState == Purchase.PurchaseState.PURCHASED
        }
    }

    fun disconnect() {
        billingClient.endConnection()
    }
}