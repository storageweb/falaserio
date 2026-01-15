package br.com.webstorage.falaserio.domain.billing

import com.android.billingclient.api.BillingClient

/**
 * Configuração centralizada de produtos de monetização.
 * 
 * Este arquivo facilita o gerenciamento de produtos, permitindo adicionar, editar ou remover
 * produtos sem precisar modificar código em vários lugares.
 */
object MonetizationConfig {
    
    /**
     * Define um produto de monetização com todas as suas propriedades.
     */
    data class Product(
        val id: String,
        val type: ProductType,
        val credits: Int = 0,
        val isUnlimited: Boolean = false,
        val hideAds: Boolean = false,
        val isSubscription: Boolean = false,
        val subscriptionType: String? = null,
        val monthlyCredits: Int = 0,
        val description: String = "",
        val isPopular: Boolean = false,
        val displayOrder: Int = 0
    )
    
    /**
     * Lista de todos os produtos disponíveis.
     * 
     * Para ADICIONAR um novo produto:
     * 1. Adicione uma nova entrada nesta lista com todas as propriedades
     * 2. Certifique-se de que o ID corresponde ao ID no Google Play Console
     * 3. Configure o tipo correto (INAPP ou SUBS)
     * 
     * Para EDITAR um produto:
     * 1. Encontre o produto na lista pelo ID
     * 2. Modifique as propriedades desejadas
     * 
     * Para REMOVER um produto:
     * 1. Remova a entrada da lista
     * 2. Considere manter produtos antigos como deprecated se usuários já compraram
     */
    val ALL_PRODUCTS = listOf(
        // ========== PACOTES DE CRÉDITOS (CONSUMÍVEIS) ==========
        Product(
            id = "pack_10_credits",
            type = ProductType.INAPP,
            credits = 10,
            description = "Pacote com 10 créditos para análises",
            displayOrder = 1
        ),
        Product(
            id = "pack_20_credits",
            type = ProductType.INAPP,
            credits = 20,
            description = "Pacote com 20 créditos para análises",
            displayOrder = 2
        ),
        
        // ========== ASSINATURAS ==========
        Product(
            id = "subscriber_30",
            type = ProductType.SUBS,
            isSubscription = true,
            subscriptionType = "SUBSCRIBER_30",
            monthlyCredits = 30,
            hideAds = true,
            description = "30 créditos por mês + sem anúncios",
            isPopular = true,
            displayOrder = 3
        ),
        Product(
            id = "subscriber_50",
            type = ProductType.SUBS,
            isSubscription = true,
            subscriptionType = "SUBSCRIBER_50",
            monthlyCredits = 50,
            hideAds = true,
            description = "50 créditos por mês + sem anúncios",
            displayOrder = 4
        ),
        
        // ========== COMPRAS PERMANENTES ==========
        Product(
            id = "lifetime_unlimited",
            type = ProductType.INAPP,
            isUnlimited = true,
            hideAds = true,
            description = "Créditos ilimitados + sem anúncios para sempre",
            displayOrder = 5
        ),
        Product(
            id = "perpetual_100",
            type = ProductType.INAPP,
            credits = 100,
            hideAds = true,
            description = "100 créditos + sem anúncios",
            displayOrder = 6
        )
    )
    
    /**
     * Produtos INAPP (compras únicas e permanentes).
     */
    val INAPP_PRODUCT_IDS: List<String> = ALL_PRODUCTS
        .filter { it.type == ProductType.INAPP }
        .map { it.id }
    
    /**
     * Produtos SUBS (assinaturas recorrentes).
     */
    val SUBS_PRODUCT_IDS: List<String> = ALL_PRODUCTS
        .filter { it.type == ProductType.SUBS }
        .map { it.id }
    
    /**
     * Retorna a configuração de um produto pelo ID.
     */
    fun getProductById(productId: String): Product? {
        return ALL_PRODUCTS.find { it.id == productId }
    }
    
    /**
     * Retorna o tipo de billing (INAPP ou SUBS) pelo ID do produto.
     */
    fun getBillingType(productId: String): String {
        return when (getProductById(productId)?.type) {
            ProductType.INAPP -> BillingClient.ProductType.INAPP
            ProductType.SUBS -> BillingClient.ProductType.SUBS
            null -> BillingClient.ProductType.INAPP
        }
    }
    
    /**
     * Retorna produtos ordenados por displayOrder.
     */
    fun getProductsSorted(): List<Product> {
        return ALL_PRODUCTS.sortedBy { it.displayOrder }
    }
    
    /**
     * Valida se um produto está configurado corretamente.
     */
    fun validateProduct(product: Product): List<String> {
        val errors = mutableListOf<String>()
        
        if (product.id.isBlank()) {
            errors.add("ID do produto não pode estar vazio")
        }
        
        if (product.isSubscription && product.subscriptionType.isNullOrBlank()) {
            errors.add("Produtos de assinatura devem ter subscriptionType")
        }
        
        if (product.isSubscription && product.monthlyCredits <= 0) {
            errors.add("Produtos de assinatura devem ter monthlyCredits > 0")
        }
        
        if (!product.isSubscription && !product.isUnlimited && product.credits <= 0) {
            errors.add("Produtos não-assinatura devem ter credits > 0 ou ser ilimitados")
        }
        
        if (product.isUnlimited && product.credits > 0) {
            errors.add("Produtos ilimitados não devem especificar créditos")
        }
        
        return errors
    }
    
    /**
     * Valida toda a configuração de produtos.
     */
    fun validateAllProducts(): Map<String, List<String>> {
        val validationResults = mutableMapOf<String, List<String>>()
        
        ALL_PRODUCTS.forEach { product ->
            val errors = validateProduct(product)
            if (errors.isNotEmpty()) {
                validationResults[product.id] = errors
            }
        }
        
        // Verificar IDs duplicados
        val duplicateIds = ALL_PRODUCTS.groupBy { it.id }
            .filter { it.value.size > 1 }
            .keys
        
        duplicateIds.forEach { duplicateId ->
            val existingErrors = validationResults[duplicateId] ?: emptyList()
            validationResults[duplicateId] = existingErrors + "ID de produto duplicado: $duplicateId"
        }
        
        return validationResults
    }
}
