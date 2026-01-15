# 🛠️ Guia de Gerenciamento de Monetização

## Visão Geral

O FalaSério agora possui um sistema centralizado de gerenciamento de produtos de monetização que facilita **adicionar**, **editar** e **remover** produtos sem precisar modificar código em vários lugares.

## Arquitetura do Sistema

```
MonetizationConfig.kt        → Configuração centralizada de produtos
MonetizationManager.kt        → Processamento de compras
BillingManager.kt             → Integração com Google Play Billing
CreditsViewModel.kt           → ViewModel usa MonetizationManager
MonetizationManagementScreen  → Interface de visualização (dev only)
```

## Como Adicionar um Novo Produto

### Passo 1: Configurar no Google Play Console

1. Acesse o Google Play Console
2. Vá em "Monetização" → "Produtos in-app" ou "Assinaturas"
3. Crie o produto com um ID único (ex: `pack_50_credits`)
4. Configure preço, descrição, etc.

### Passo 2: Adicionar ao MonetizationConfig.kt

Abra `domain/billing/MonetizationConfig.kt` e adicione o produto na lista `ALL_PRODUCTS`:

```kotlin
val ALL_PRODUCTS = listOf(
    // ... produtos existentes ...
    
    // Novo produto
    Product(
        id = "pack_50_credits",              // DEVE corresponder ao ID do Google Play
        type = ProductType.INAPP,            // INAPP ou SUBS
        credits = 50,                        // Quantidade de créditos
        description = "Pacote com 50 créditos",
        displayOrder = 7                     // Ordem de exibição na loja
    )
)
```

### Passo 3: Testar

Execute o app e:
1. Vá para a tela de Créditos
2. O produto deve aparecer automaticamente
3. Teste a compra (use uma conta de teste do Google Play)

**Pronto!** Não é necessário modificar mais nada. O sistema automaticamente:
- Carrega o produto do Google Play
- Processa a compra
- Adiciona os créditos corretos
- Atualiza a UI

## Como Editar um Produto Existente

### Editar no Google Play Console

1. Para alterar preço ou descrição: faça no Google Play Console
2. As mudanças aparecerão automaticamente no app

### Editar Comportamento no App

Abra `MonetizationConfig.kt` e modifique as propriedades do produto:

```kotlin
Product(
    id = "pack_10_credits",
    type = ProductType.INAPP,
    credits = 15,                    // ALTERADO: era 10, agora é 15
    description = "Pacote econômico", // ALTERADO: nova descrição
    isPopular = true,                // NOVO: marcar como popular
    displayOrder = 1
)
```

## Como Remover um Produto

### ⚠️ IMPORTANTE: Cuidados ao Remover

**NÃO remova produtos se usuários já compraram!** Isso pode causar problemas ao restaurar compras.

### Opção 1: Desativar no Google Play (Recomendado)

1. Vá ao Google Play Console
2. Desative o produto (não delete)
3. Mantenha a entrada em `MonetizationConfig.kt` para compatibilidade

### Opção 2: Remover Definitivamente (Somente para produtos nunca vendidos)

1. Delete do Google Play Console
2. Remova a entrada de `ALL_PRODUCTS` em `MonetizationConfig.kt`

## Tipos de Produtos Suportados

### 1. Pacotes de Créditos Simples (INAPP Consumível)

```kotlin
Product(
    id = "pack_10_credits",
    type = ProductType.INAPP,
    credits = 10,                    // Quantidade de créditos
    description = "Pacote com 10 créditos"
)
```

### 2. Assinaturas Mensais (SUBS)

```kotlin
Product(
    id = "subscriber_30",
    type = ProductType.SUBS,
    isSubscription = true,
    subscriptionType = "SUBSCRIBER_30", // ID interno único
    monthlyCredits = 30,                // Créditos renovados mensalmente
    hideAds = true,                     // Remove anúncios
    description = "30 créditos por mês + sem anúncios"
)
```

### 3. Compra Permanente com Créditos Ilimitados (INAPP Não-Consumível)

```kotlin
Product(
    id = "lifetime_unlimited",
    type = ProductType.INAPP,
    isUnlimited = true,              // Créditos infinitos
    hideAds = true,                  // Remove anúncios
    description = "Créditos ilimitados para sempre"
)
```

### 4. Compra Permanente com Pacote Grande (INAPP Não-Consumível)

```kotlin
Product(
    id = "perpetual_100",
    type = ProductType.INAPP,
    credits = 100,                   // Pacote grande de créditos
    hideAds = true,                  // Remove anúncios
    description = "100 créditos + sem anúncios"
)
```

## Propriedades Disponíveis

| Propriedade | Tipo | Descrição | Obrigatório |
|-------------|------|-----------|-------------|
| `id` | String | ID do produto no Google Play | ✅ Sim |
| `type` | ProductType | INAPP ou SUBS | ✅ Sim |
| `credits` | Int | Créditos únicos (para não-assinaturas) | Condicional |
| `isUnlimited` | Boolean | Se dá créditos infinitos | Não (default: false) |
| `hideAds` | Boolean | Se remove anúncios | Não (default: false) |
| `isSubscription` | Boolean | Se é assinatura | Condicional |
| `subscriptionType` | String | ID interno da assinatura | Se isSubscription |
| `monthlyCredits` | Int | Créditos mensais (assinaturas) | Se isSubscription |
| `description` | String | Descrição do produto | Não |
| `isPopular` | Boolean | Destaca na loja | Não (default: false) |
| `displayOrder` | Int | Ordem de exibição | Não (default: 0) |

## Validação Automática

O sistema valida automaticamente os produtos e detecta:

- ✅ IDs duplicados
- ✅ Assinaturas sem `subscriptionType` ou `monthlyCredits`
- ✅ Produtos sem créditos definidos (quando necessário)
- ✅ Conflitos entre `isUnlimited` e `credits`

### Verificar Validação

Use a tela de gerenciamento (apenas em debug):

```kotlin
// Em desenvolvimento, adicione rota para:
MonetizationManagementScreen(onNavigateBack = { ... })
```

Ou execute manualmente:

```kotlin
val errors = MonetizationConfig.validateAllProducts()
if (errors.isNotEmpty()) {
    errors.forEach { (productId, errorList) ->
        Log.e("Monetization", "Produto $productId tem erros: $errorList")
    }
}
```

## Fluxo de Compra Automático

Quando um usuário compra um produto, o `MonetizationManager` automaticamente:

1. Identifica o produto por ID
2. Aplica os benefícios corretos:
   - Adiciona créditos
   - Define assinatura
   - Remove anúncios
   - Configura créditos ilimitados

**Você não precisa escrever código de processamento!**

## Boas Práticas

### ✅ DO (Faça)

- Configure `displayOrder` para controlar ordem de exibição
- Use `isPopular = true` para produtos em destaque
- Mantenha `description` descritivo mas conciso
- Teste em ambiente de teste do Google Play antes de publicar
- Use IDs descritivos (ex: `pack_10_credits`, não `prod001`)

### ❌ DON'T (Não faça)

- Não remova produtos já vendidos
- Não use IDs duplicados
- Não deixe `subscriptionType` vazio em assinaturas
- Não configure `credits` E `isUnlimited` ao mesmo tempo
- Não altere radicalmente benefícios de produtos ativos

## Exemplos Práticos

### Exemplo 1: Adicionar Pacote Promocional

```kotlin
Product(
    id = "promo_pack_25",
    type = ProductType.INAPP,
    credits = 25,
    description = "🎉 PROMO: 25 créditos pelo preço de 15!",
    isPopular = true,
    displayOrder = 0  // Aparece primeiro
)
```

### Exemplo 2: Adicionar Assinatura Anual

```kotlin
Product(
    id = "subscriber_yearly_100",
    type = ProductType.SUBS,
    isSubscription = true,
    subscriptionType = "SUBSCRIBER_YEARLY",
    monthlyCredits = 100,
    hideAds = true,
    description = "Assinatura anual: 100 créditos/mês + sem ads",
    displayOrder = 10
)
```

### Exemplo 3: Pacote VIP

```kotlin
Product(
    id = "vip_package",
    type = ProductType.INAPP,
    credits = 500,
    hideAds = true,
    description = "Pacote VIP: 500 créditos + sem anúncios",
    isPopular = true,
    displayOrder = 15
)
```

## Troubleshooting

### Produto não aparece na loja

1. Verifique se o ID está correto em `MonetizationConfig.kt`
2. Confirme que o produto está ativo no Google Play Console
3. Verifique se está usando conta de teste autorizada
4. Aguarde alguns minutos (cache do Google Play)

### Compra não processa benefícios

1. Verifique logs: `MonetizationManager.processPurchase()`
2. Confirme que o produto está em `ALL_PRODUCTS`
3. Execute validação: `MonetizationConfig.validateProduct(product)`

### Validação reporta erros

1. Acesse `MonetizationManagementScreen` (apenas debug)
2. Corrija os problemas listados
3. Execute o app novamente

## Suporte

Para problemas ou dúvidas:
1. Consulte a documentação do Google Play Billing
2. Verifique logs do sistema
3. Use `MonetizationManagementScreen` para debug
4. Entre em contato com @jules para revisão

---

**Desenvolvedor:** Facilite sua vida! Todo o sistema de monetização agora está em **um único arquivo**: `MonetizationConfig.kt` 🎉
