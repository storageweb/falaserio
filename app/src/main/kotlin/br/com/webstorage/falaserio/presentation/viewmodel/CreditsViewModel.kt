package br.com.webstorage.falaserio.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.webstorage.falaserio.data.repository.CreditsRepository
import br.com.webstorage.falaserio.domain.billing.BillingManager
import br.com.webstorage.falaserio.domain.billing.MonetizationManager
import br.com.webstorage.falaserio.domain.billing.ProcessingResult
import br.com.webstorage.falaserio.domain.billing.ProductInfo
import com.android.billingclient.api.ProductDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para tela de créditos/loja.
 */
@HiltViewModel
class CreditsViewModel @Inject constructor(
    private val creditsRepository: CreditsRepository,
    private val billingManager: BillingManager,
    private val monetizationManager: MonetizationManager
) : ViewModel() {

    private val _credits = MutableStateFlow(0)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    private val _isUnlimited = MutableStateFlow(false)
    val isUnlimited: StateFlow<Boolean> = _isUnlimited.asStateFlow()

    // Alterado para usar a classe ProductDetails diretamente da biblioteca de billing
    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    private val _purchaseError = MutableStateFlow<String?>(null)
    val purchaseError: StateFlow<String?> = _purchaseError.asStateFlow()

    init {
        loadCredits()
        loadProducts()
    }

    private fun loadCredits() {
        viewModelScope.launch {
            creditsRepository.getCredits().collect { entity ->
                entity?.let {
                    _credits.value = it.available
                    _isUnlimited.value = it.isUnlimited
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _products.value = billingManager.getAvailableProducts()
        }
    }

    // A assinatura mudou. Agora a UI (Composable) precisa passar a Activity e o ProductDetails.
    fun purchaseProduct(activity: Activity, productDetails: ProductDetails) {
        viewModelScope.launch {
            _isPurchasing.value = true
            _purchaseError.value = null

            val result = billingManager.purchase(activity, productDetails)

            result.fold(
                onSuccess = { purchase ->
                    // Usa o MonetizationManager para processar a compra
                    when (val processingResult = monetizationManager.processPurchase(productDetails.productId)) {
                        is ProcessingResult.Success -> {
                            // Compra processada com sucesso
                        }
                        is ProcessingResult.Error -> {
                            _purchaseError.value = "Erro no processamento da compra. Tente novamente."
                        }
                    }
                },
                onFailure = { exception ->
                    _purchaseError.value = exception.message ?: "Erro na compra"
                }
            )

            _isPurchasing.value = false
        }
    }

    fun onAdWatched() {
        viewModelScope.launch {
            creditsRepository.addCredits(1)
        }
    }

    /**
     * Restaura compras anteriores do usuário.
     * Deve ser chamado explicitamente pelo usuário (botão "Restaurar Compras").
     */
    fun restorePurchases() {
        viewModelScope.launch {
            _isPurchasing.value = true
            _purchaseError.value = null

            try {
                // 1. Busca compras ativas no Google Play
                val purchases = billingManager.restorePurchases()
                
                if (purchases.isEmpty()) {
                    _purchaseError.value = "Nenhuma compra encontrada para restaurar."
                } else {
                    // 2. Processa cada compra (com flag isRestore=true)
                    val restoredIds = monetizationManager.processMultiplePurchases(purchases)
                    
                    if (restoredIds.isNotEmpty()) {
                        // Sucesso silencioso (UI atualiza via StateFlow de créditos)
                    } else {
                        // Compras existem mas podem não ser válidas no nosso config
                         _purchaseError.value = "Compras encontradas mas não puderam ser restauradas."
                    }
                }
            } catch (e: Exception) {
                _purchaseError.value = "Erro ao restaurar: ${e.message}"
            } finally {
                _isPurchasing.value = false
            }
        }
    }

    fun clearError() {
        _purchaseError.value = null
    }
}
