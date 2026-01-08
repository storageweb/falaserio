package br.com.webstorage.falaserio.domain.billing

/**
 * Informações de um produto disponível para compra.
 */
data class ProductInfo(
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val priceAmountMicros: Long = 0,
    val priceCurrencyCode: String = "BRL",
    val type: ProductType = ProductType.INAPP
)

enum class ProductType {
    INAPP,      // Compra única
    SUBS        // Assinatura
}
