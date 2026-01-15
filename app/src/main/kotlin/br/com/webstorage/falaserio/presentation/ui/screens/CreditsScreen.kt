package br.com.webstorage.falaserio.presentation.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.webstorage.falaserio.domain.billing.MonetizationConfig
import br.com.webstorage.falaserio.presentation.ui.theme.Accent
import br.com.webstorage.falaserio.presentation.ui.theme.ErrorColor
import br.com.webstorage.falaserio.presentation.ui.theme.Primary
import br.com.webstorage.falaserio.presentation.ui.theme.SuccessColor
import br.com.webstorage.falaserio.presentation.viewmodel.CreditsViewModel
import com.android.billingclient.api.ProductDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreditsViewModel = hiltViewModel()
) {
    val credits by viewModel.credits.collectAsStateWithLifecycle()
    val isUnlimited by viewModel.isUnlimited.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val isPurchasing by viewModel.isPurchasing.collectAsStateWithLifecycle()
    val purchaseError by viewModel.purchaseError.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💰 Créditos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de créditos atuais
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Seus Créditos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isUnlimited) "∞ ILIMITADO" else "$credits",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlimited) Primary else Accent
                        )
                        if (!isUnlimited) {
                            Text("análises disponíveis", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Assistir anúncio
            item {
                 Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SuccessColor.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoLibrary, null, tint = SuccessColor, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Assistir Anúncio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Ganhe 1 crédito grátis!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(onClick = { viewModel.onAdWatched() }, colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)) {
                            Text("ASSISTIR")
                        }
                    }
                }
            }

            // Título pacotes
            item {
                Text("🛒 Pacotes de Créditos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            // Lista de produtos do Google Play
            items(products) { product ->
                // Extrai o preço do detalhe da compra única ou da primeira fase da assinatura
                val price = product.oneTimePurchaseOfferDetails?.formattedPrice 
                    ?: product.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                    ?: ""

                // Usa MonetizationConfig para determinar se é popular
                val productConfig = MonetizationConfig.getProductById(product.productId)
                val isPopular = productConfig?.isPopular ?: false

                ProductCard(
                    title = product.name, // API mudou de title para name
                    description = product.description,
                    price = price,
                    isPopular = isPopular,
                    isPurchasing = isPurchasing,
                    onClick = { viewModel.purchaseProduct(activity, product) }
                )
            }

            // Mensagem de erro
            purchaseError?.let {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f))
                    ) {
                        Text(it, modifier = Modifier.padding(16.dp), color = ErrorColor, textAlign = TextAlign.Center)
                    }
                }
            }
            
            // Aviso
            item {
                Text(
                    "⚠️ As compras são processadas pelo Google Play. Restaurações automáticas em caso de reinstalação.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }

            // Botão de Restaurar Compras (Requisito do Review)
            item {
                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPurchasing
                ) {
                    Text("Restaurar Compras Anteriores")
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    title: String,
    description: String,
    price: String,
    isPopular: Boolean,
    isPurchasing: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isPopular) Primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Star, null, tint = Accent, modifier = Modifier.size(16.dp))
                        Text("POPULAR", style = MaterialTheme.typography.labelSmall, color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onClick,
                enabled = !isPurchasing,
                colors = ButtonDefaults.buttonColors(containerColor = if (isPopular) Primary else MaterialTheme.colorScheme.primary)
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(price)
                }
            }
        }
    }
}