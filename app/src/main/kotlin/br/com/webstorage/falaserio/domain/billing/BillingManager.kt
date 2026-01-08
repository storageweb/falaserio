package br.com.webstorage.falaserio.domain.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Gerenciador de compras in-app usando Google Play Billing Library 7.0.
 *
 * Produtos configurados:
 * - pack_10_credits: 10 créditos (consumível)
 * - pack_20_credits: 20 créditos (consumível)
 * - subscriber_30: Assinatura 30 créditos/mês
 * - subscriber_50: Assinatura 50 créditos/mês
 * - lifetime_unlimited: Ilimitado vitalício
 * - perpetual_100: 100 créditos + sem ads
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var currentActivity: Activity? = null
    private var purchaseCallback: ((Result<Purchase>) -> Unit)? = null

    // IDs dos produtos
    companion object {
        val INAPP_PRODUCTS = listOf(
            "pack_10_credits",
            "pack_20_credits",
            "lifetime_unlimited",
            "perpetual_100"
        )

        val SUBS_PRODUCTS = listOf(
            "subscriber_30",
            "subscriber_50"
        )
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    /**
     * Conecta ao Google Play Billing.
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (continuation.isActive) {
                        continuation.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Tentar reconectar
                }
            })
        }
    }

    /**
     * Obtém lista de produtos disponíveis.
     */
    suspend fun getAvailableProducts(): List<ProductInfo> = withContext(Dispatchers.IO) {
        val products = mutableListOf<ProductInfo>()

        if (!connect()) return@withContext products

        // Buscar produtos INAPP
        val inappParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                INAPP_PRODUCTS.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient?.queryProductDetailsAsync(inappParams) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { details ->
                    details.oneTimePurchaseOfferDetails?.let { offer ->
                        products.add(
                            ProductInfo(
                                productId = details.productId,
                                title = details.title,
                                description = details.description,
                                formattedPrice = offer.formattedPrice,
                                priceAmountMicros = offer.priceAmountMicros,
                                priceCurrencyCode = offer.priceCurrencyCode,
                                type = ProductType.INAPP
                            )
                        )
                    }
                }
            }
        }

        // Buscar assinaturas
        val subsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                SUBS_PRODUCTS.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        billingClient?.queryProductDetailsAsync(subsParams) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { details ->
                    details.subscriptionOfferDetails?.firstOrNull()?.let { offer ->
                        val phase = offer.pricingPhases.pricingPhaseList.firstOrNull()
                        phase?.let {
                            products.add(
                                ProductInfo(
                                    productId = details.productId,
                                    title = details.title,
                                    description = details.description,
                                    formattedPrice = it.formattedPrice + "/mês",
                                    priceAmountMicros = it.priceAmountMicros,
                                    priceCurrencyCode = it.priceCurrencyCode,
                                    type = ProductType.SUBS
                                )
                            )
                        }
                    }
                }
            }
        }

        products
    }

    /**
     * Inicia fluxo de compra.
     */
    suspend fun purchase(productId: String): Result<Purchase> = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            purchaseCallback = { result ->
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }

            // Na prática, você precisa obter o ProductDetails primeiro
            // e chamar launchBillingFlow com a Activity
            // Por simplicidade, simulamos sucesso
            purchaseCallback?.invoke(Result.failure(Exception("Configure a Activity antes de comprar")))
        }
    }

    /**
     * Define a Activity atual para o fluxo de compra.
     */
    fun setActivity(activity: Activity?) {
        currentActivity = activity
    }

    /**
     * Callback de compras.
     */
    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.firstOrNull()?.let { purchase ->
                    // Consumir a compra se for consumível
                    consumePurchase(purchase)
                    purchaseCallback?.invoke(Result.success(purchase))
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                purchaseCallback?.invoke(Result.failure(Exception("Compra cancelada")))
            }

            else -> {
                purchaseCallback?.invoke(Result.failure(Exception("Erro: ${result.debugMessage}")))
            }
        }
    }

    /**
     * Consome uma compra (para produtos consumíveis).
     */
    private fun consumePurchase(purchase: Purchase) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.consumeAsync(params) { _, _ -> }
    }

    /**
     * Verifica compras pendentes/já realizadas.
     */
    suspend fun queryPurchases(): List<Purchase> = withContext(Dispatchers.IO) {
        val purchases = mutableListOf<Purchase>()

        if (!connect()) return@withContext purchases

        // Verificar INAPP
        val inappParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient?.queryPurchasesAsync(inappParams) { _, purchasesList ->
            purchases.addAll(purchasesList)
        }

        // Verificar SUBS
        val subsParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient?.queryPurchasesAsync(subsParams) { _, purchasesList ->
            purchases.addAll(purchasesList)
        }

        purchases
    }

    /**
     * Desconecta do billing.
     */
    fun disconnect() {
        billingClient?.endConnection()
    }
}
