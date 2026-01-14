package br.com.webstorage.falaserio.presentation.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.webstorage.falaserio.data.repository.CreditsRepository
import br.com.webstorage.falaserio.domain.billing.BillingManager
import br.com.webstorage.falaserio.domain.billing.MonetizationManager
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

            if (result.isSuccess) {
                // Usa o MonetizationManager para processar a compra
                val processed = monetizationManager.processPurchase(productDetails.productId)
                if (!processed) {
                    _purchaseError.value = "Produto não reconhecido: ${productDetails.productId}"
                }
            } else {
                _purchaseError.value = result.exceptionOrNull()?.message ?: "Erro na compra"
            }

            _isPurchasing.value = false
        }
    }

    fun onAdWatched() {
        viewModelScope.launch {
            creditsRepository.addCredits(1)
        }
    }

    fun clearError() {
        _purchaseError.value = null
    }
}
