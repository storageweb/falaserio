package br.com.webstorage.falaserio.domain.billing

import br.com.webstorage.falaserio.data.repository.CreditsRepository
import com.android.billingclient.api.Purchase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resultado do processamento de uma compra.
 */
sealed class ProcessingResult {
    object Success : ProcessingResult()
    data class Error(val message: String) : ProcessingResult()
}

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
     * @param isRestore Indica se é uma restauração de compra (afeta lógica de renovação)
     * @return ProcessingResult indicando sucesso ou erro
     */
    suspend fun processPurchase(productId: String, isRestore: Boolean = false): ProcessingResult {
        val product = MonetizationConfig.getProductById(productId) 
            ?: return ProcessingResult.Error("Produto não encontrado")
        
        return try {
            when {
                // Produto com créditos ilimitados
                product.isUnlimited -> {
                    creditsRepository.setUnlimitedCredits()
                    if (product.hideAds) {
                        creditsRepository.setShouldShowAds(false)
                    }
                    ProcessingResult.Success
                }
                
                // Produto de assinatura
                product.isSubscription -> {
                    product.subscriptionType?.let { subType ->
                        creditsRepository.setSubscription(subType, showAds = !product.hideAds)
                        
                        if (product.monthlyCredits > 0) {
                            if (isRestore) {
                                // Se for restore, verifica se precisa renovar (passou 30 dias)
                                val credits = creditsRepository.getCreditsOnce()
                                val lastRenewal = credits?.lastRenewalDate ?: 0L
                                val daysSinceRenewal = (System.currentTimeMillis() - lastRenewal) / (1000 * 60 * 60 * 24)
                                
                                if (daysSinceRenewal >= 30) {
                                    creditsRepository.renewMonthlyCredits(product.monthlyCredits)
                                }
                                // Se não passou 30 dias, não faz nada com os créditos (mantém o saldo atual)
                            } else {
                                // Compra nova ou renovação explícita: aplica créditos imediatamente
                                creditsRepository.renewMonthlyCredits(product.monthlyCredits)
                            }
                        }
                        ProcessingResult.Success
                    } ?: ProcessingResult.Error("Tipo de assinatura não definido")
                }
                
                // Produto de créditos simples
                product.credits > 0 -> {
                    // Consumíveis só são processados se NÃO for restore
                    // (O Google Play não retorna consumíveis no queryPurchases depois de consumidos,
                    // mas se por acaso retornar, não queremos dar créditos duplicados)
                    if (!isRestore) {
                        creditsRepository.addCredits(product.credits)
                    }
                    
                    if (product.hideAds) {
                        creditsRepository.setShouldShowAds(false)
                    }
                    ProcessingResult.Success
                }
                
                else -> ProcessingResult.Error("Configuração de produto inválida")
            }
        } catch (e: Exception) {
            ProcessingResult.Error("Erro ao processar: ${e.message}")
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
                if (processPurchase(productId, isRestore = true) is ProcessingResult.Success) {
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
