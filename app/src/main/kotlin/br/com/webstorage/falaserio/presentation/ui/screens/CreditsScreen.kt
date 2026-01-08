package br.com.webstorage.falaserio.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.webstorage.falaserio.presentation.ui.theme.Accent
import br.com.webstorage.falaserio.presentation.ui.theme.ErrorColor
import br.com.webstorage.falaserio.presentation.ui.theme.Primary
import br.com.webstorage.falaserio.presentation.ui.theme.SuccessColor
import br.com.webstorage.falaserio.presentation.viewmodel.CreditsViewModel

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💰 Créditos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de créditos atuais
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Seus Créditos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isUnlimited) "∞ ILIMITADO" else "$credits",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlimited) Primary else Accent
                        )
                        if (!isUnlimited) {
                            Text(
                                text = "análises disponíveis",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Assistir anúncio (grátis)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = SuccessColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Assistir Anúncio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ganhe 1 crédito grátis!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.onAdWatched() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessColor
                            )
                        ) {
                            Text("ASSISTIR")
                        }
                    }
                }
            }

            // Título pacotes
            item {
                Text(
                    text = "🛒 Pacotes de Créditos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Lista de produtos
            items(products) { product ->
                ProductCard(
                    title = product.title,
                    description = product.description,
                    price = product.formattedPrice,
                    isPopular = product.productId == "subscriber_30",
                    isPurchasing = isPurchasing,
                    onClick = { viewModel.purchaseProduct(product.productId) }
                )
            }

            // Produtos hardcoded caso billing não carregue
            if (products.isEmpty()) {
                item {
                    ProductCard(
                        title = "📦 10 Créditos",
                        description = "Pacote básico para começar",
                        price = "R$ 4,99",
                        isPopular = false,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("pack_10_credits") }
                    )
                }
                item {
                    ProductCard(
                        title = "📦 20 Créditos",
                        description = "Melhor custo-benefício!",
                        price = "R$ 7,99",
                        isPopular = false,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("pack_20_credits") }
                    )
                }
                item {
                    ProductCard(
                        title = "⭐ Assinatura 30/mês",
                        description = "30 créditos mensais + sem anúncios",
                        price = "R$ 9,90/mês",
                        isPopular = true,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("subscriber_30") }
                    )
                }
                item {
                    ProductCard(
                        title = "⭐ Assinatura 50/mês",
                        description = "50 créditos mensais + sem anúncios",
                        price = "R$ 14,90/mês",
                        isPopular = false,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("subscriber_50") }
                    )
                }
                item {
                    ProductCard(
                        title = "👑 Vitalício Ilimitado",
                        description = "Análises ILIMITADAS para sempre!",
                        price = "R$ 49,90",
                        isPopular = false,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("lifetime_unlimited") }
                    )
                }
                item {
                    ProductCard(
                        title = "💎 Perpétuo 100",
                        description = "100 créditos + sem anúncios para sempre",
                        price = "R$ 29,90",
                        isPopular = false,
                        isPurchasing = isPurchasing,
                        onClick = { viewModel.purchaseProduct("perpetual_100") }
                    )
                }
            }

            // Erro
            purchaseError?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = ErrorColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Aviso
            item {
                Text(
                    text = "⚠️ As compras são processadas pelo Google Play.\nRestaurações automáticas em caso de reinstalação.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
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
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular)
                Primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "POPULAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onClick,
                enabled = !isPurchasing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) Primary else MaterialTheme.colorScheme.primary
                )
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(price)
                }
            }
        }
    }
}
