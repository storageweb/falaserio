package br.com.webstorage.falaserio.domain.billing

import br.com.webstorage.falaserio.data.repository.CreditsRepository
import com.android.billingclient.api.Purchase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de monetização que processa compras usando a configuração centralizada.
 * 
 * Este manager elimina a necessidade de hardcoding de lógica de produtos,
 * tornando fácil adicionar, editar ou remover produtos.
 */
@Singleton
class MonetizationManager @Inject constructor(
    private val creditsRepository: CreditsRepository
) {
    
    /**
     * Processa uma compra bem-sucedida aplicando os benefícios do produto.
     * 
     * @param productId ID do produto comprado
     * @return true se processado com sucesso, false se produto não encontrado
     */
    suspend fun processPurchase(productId: String): Boolean {
        val product = MonetizationConfig.getProductById(productId) ?: return false
        
        return when {
            // Produto com créditos ilimitados
            product.isUnlimited -> {
                creditsRepository.setUnlimitedCredits()
                if (product.hideAds) {
                    creditsRepository.setShouldShowAds(false)
                }
                true
            }
            
            // Produto de assinatura
            product.isSubscription -> {
                product.subscriptionType?.let { subType ->
                    creditsRepository.setSubscription(subType, showAds = !product.hideAds)
                    if (product.monthlyCredits > 0) {
                        creditsRepository.renewMonthlyCredits(product.monthlyCredits)
                    }
                    true
                } ?: false
            }
            
            // Produto de créditos simples
            product.credits > 0 -> {
                creditsRepository.addCredits(product.credits)
                if (product.hideAds) {
                    creditsRepository.setShouldShowAds(false)
                }
                true
            }
            
            else -> false
        }
    }
    
    /**
     * Processa múltiplas compras (usado em restore purchases).
     * 
     * @param purchases Lista de compras a processar
     * @return Lista de IDs de produtos processados com sucesso
     */
    suspend fun processMultiplePurchases(purchases: List<Purchase>): List<String> {
        val processedProducts = mutableListOf<String>()
        
        purchases.forEach { purchase ->
            purchase.products.forEach { productId ->
                if (processPurchase(productId)) {
                    processedProducts.add(productId)
                }
            }
        }
        
        return processedProducts
    }
    
    /**
     * Verifica se um produto existe na configuração.
     */
    fun isValidProduct(productId: String): Boolean {
        return MonetizationConfig.getProductById(productId) != null
    }
    
    /**
     * Retorna informações sobre um produto.
     */
    fun getProductInfo(productId: String): MonetizationConfig.Product? {
        return MonetizationConfig.getProductById(productId)
    }
    
    /**
     * Retorna a lista de produtos ordenada para exibição.
     */
    fun getDisplayProducts(): List<MonetizationConfig.Product> {
        return MonetizationConfig.getProductsSorted()
    }
    
    /**
     * Retorna apenas produtos populares.
     */
    fun getPopularProducts(): List<MonetizationConfig.Product> {
        return MonetizationConfig.getProductsSorted().filter { it.isPopular }
    }
    
    /**
     * Retorna produtos por tipo.
     */
    fun getProductsByType(type: ProductType): List<MonetizationConfig.Product> {
        return MonetizationConfig.ALL_PRODUCTS.filter { it.type == type }
    }
    
    /**
     * Calcula o valor de créditos que um produto oferece.
     * Para assinaturas, retorna créditos mensais.
     */
    fun getProductCreditsValue(productId: String): Int {
        val product = MonetizationConfig.getProductById(productId) ?: return 0
        
        return when {
            product.isUnlimited -> Int.MAX_VALUE
            product.isSubscription -> product.monthlyCredits
            else -> product.credits
        }
    }
    
    /**
     * Verifica se um produto remove anúncios.
     */
    fun productHidesAds(productId: String): Boolean {
        return MonetizationConfig.getProductById(productId)?.hideAds ?: false
    }
}
