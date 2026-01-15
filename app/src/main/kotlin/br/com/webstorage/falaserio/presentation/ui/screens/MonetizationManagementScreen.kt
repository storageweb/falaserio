package br.com.webstorage.falaserio.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.webstorage.falaserio.domain.billing.MonetizationConfig
import br.com.webstorage.falaserio.domain.billing.ProductType
import br.com.webstorage.falaserio.presentation.ui.theme.ErrorColor
import br.com.webstorage.falaserio.presentation.ui.theme.Primary
import br.com.webstorage.falaserio.presentation.ui.theme.SuccessColor

/**
 * Nome do arquivo de configuração de monetização.
 */
private const val MONETIZATION_CONFIG_FILE = "MonetizationConfig.kt"

/**
 * Tela de gerenciamento de produtos de monetização (apenas para desenvolvimento).
 * 
 * Esta tela permite aos desenvolvedores:
 * - Visualizar todos os produtos configurados
 * - Ver validações de configuração
 * - Verificar IDs, tipos e propriedades
 * - Identificar problemas de configuração
 * 
 * NOTA: Esta tela deve ser acessível apenas em builds de desenvolvimento/debug.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationManagementScreen(
    onNavigateBack: () -> Unit
) {
    val products = remember { MonetizationConfig.getProductsSorted() }
    val validationResults = remember { MonetizationConfig.validateAllProducts() }
    val hasErrors = validationResults.isNotEmpty()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛠️ Gerenciamento de Produtos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (hasErrors) ErrorColor.copy(alpha = 0.1f) else Primary.copy(alpha = 0.1f)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status geral
            item {
                StatusCard(
                    totalProducts = products.size,
                    hasErrors = hasErrors,
                    errorCount = validationResults.size
                )
            }
            
            // Instruções
            item {
                InstructionsCard()
            }
            
            // Mensagem de validação
            if (hasErrors) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ErrorColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Problemas de configuração detectados!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ErrorColor
                            )
                        }
                    }
                }
            }
            
            // Lista de produtos
            items(products) { product ->
                ProductDetailCard(
                    product = product,
                    errors = validationResults[product.id] ?: emptyList()
                )
            }
            
            // Footer com informações
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "💡 Para adicionar/editar/remover produtos, modifique o arquivo $MONETIZATION_CONFIG_FILE",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    totalProducts: Int,
    hasErrors: Boolean,
    errorCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasErrors) 
                ErrorColor.copy(alpha = 0.1f) 
            else 
                SuccessColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (hasErrors) Icons.Default.Error else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (hasErrors) ErrorColor else SuccessColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Status da Configuração",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (hasErrors) "Com problemas" else "Tudo OK",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hasErrors) ErrorColor else SuccessColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total de Produtos", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "$totalProducts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (hasErrors) {
                    Column {
                        Text("Problemas", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "$errorCount",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ErrorColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Como Gerenciar Produtos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InstructionItem("✅ Adicionar: Adicione nova entrada em $MONETIZATION_CONFIG_FILE")
            InstructionItem("✏️ Editar: Modifique as propriedades do produto existente")
            InstructionItem("🗑️ Remover: Remova a entrada da lista (cuidado com compras existentes)")
            InstructionItem("📍 Popular: Defina isPopular = true para destacar na loja")
            InstructionItem("🔢 Ordem: Use displayOrder para controlar a ordem de exibição")
        }
    }
}

@Composable
private fun InstructionItem(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun ProductDetailCard(
    product: MonetizationConfig.Product,
    errors: List<String>
) {
    val hasErrors = errors.isNotEmpty()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hasErrors) 
                ErrorColor.copy(alpha = 0.05f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (product.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Popular",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Chip(product.type.name)
            }
            
            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Propriedades
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.shapes.small
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PropertyRow("Créditos", 
                    when {
                        product.isUnlimited -> "∞ Ilimitado"
                        product.credits > 0 -> "${product.credits}"
                        product.monthlyCredits > 0 -> "${product.monthlyCredits}/mês"
                        else -> "0"
                    }
                )
                PropertyRow("Remove Ads", if (product.hideAds) "Sim" else "Não")
                PropertyRow("Assinatura", if (product.isSubscription) "Sim (${product.subscriptionType})" else "Não")
                PropertyRow("Ordem", "${product.displayOrder}")
            }
            
            // Erros de validação
            if (hasErrors) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ErrorColor.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                        .padding(12.dp)
                ) {
                    Text(
                        "⚠️ Problemas:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ErrorColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    errors.forEach { error ->
                        Text(
                            "• $error",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorColor,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Primary.copy(alpha = 0.2f)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}
