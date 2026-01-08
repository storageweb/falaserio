# 💰 MONETIZAÇÃO - Prompts para Gemini

## Lógica de Negócio do FalaSério

---

## 📊 ESTRUTURA DE PLANOS

| ID Produto            | Nome              | Tipo                   | Preço        | Créditos | Ads | Suporte   |
|-----------------------|-------------------|------------------------|--------------|----------|-----|-----------|
| `falaserio_10`        | FalaSério 10      | INAPP (consumível)     | R$ 10,99     | 10       | SIM | -         |
| `falaserio_20`        | FalaSério 20      | INAPP (consumível)     | R$ 16,99     | 20       | SIM | -         |
| `falaserio_30`        | FalaSério 30      | SUBS (mensal)          | R$ 19,99/mês | 30/mês   | NÃO | -         |
| `falaserio_50`        | FalaSério 50      | SUBS (anual)           | R$ 59,99/ano | 50/mês   | NÃO | WhatsApp  |
| `falaserio_lifetime`  | Licença Vitalícia | INAPP (não-consumível) | R$ 99,90     | ∞        | NÃO | 1 ano     |
| `falaserio_perpetual` | Licença Perpétua  | INAPP (não-consumível) | R$ 199,90    | ∞        | NÃO | Vitalício |

---

## 💬 PROMPTS PARA GEMINI

### PROMPT 1: Criar SubscriptionManager

```
Crie um SubscriptionManager.kt que gerencie o estado de assinatura do usuário.

Requisitos:
1. Verificar se usuário tem assinatura ativa (falaserio_30 ou falaserio_50)
2. Verificar se tem licença vitalícia ou perpétua
3. Renovar créditos mensais automaticamente para assinantes
4. Salvar estado no Room (UserCreditsEntity já existe)
5. Integrar com BillingManager existente

Estados possíveis:
- FREE: usuário gratuito, vê anúncios, 3 créditos iniciais
- PACK_10/PACK_20: comprou pacote avulso, vê anúncios
- SUBSCRIBER_30: assinante mensal, sem anúncios, 30 créditos/mês
- SUBSCRIBER_50: assinante anual, sem anúncios, 50 créditos/mês
- LIFETIME: licença vitalícia, sem anúncios, créditos infinitos
- PERPETUAL: licença perpétua, sem anúncios, créditos infinitos

Use Flow para emitir mudanças de estado.
```

### PROMPT 2: Lógica de Controle de Anúncios

```
Implemente a lógica de exibição de anúncios no app.

Regras:
1. Usuários FREE e PACK veem:
   - Banner no topo da HomeScreen
   - Banner no rodapé da HistoryScreen
   - Botão "Assistir anúncio +1 crédito" na CreditsScreen
   
2. Assinantes (SUBSCRIBER_30, SUBSCRIBER_50, LIFETIME, PERPETUAL):
   - NÃO veem banners
   - NÃO veem botão de anúncio recompensado
   - Interface limpa sem ads

Crie um AdController que:
- Expõe StateFlow<Boolean> shouldShowAds
- Observa SubscriptionManager
- Fornece composables condicionais para banners

Exemplo de uso:
@Composable
fun HomeScreen() {
    val showAds by adController.shouldShowAds.collectAsState()
    
    Column {
        if (showAds) {
            BannerAd(adUnitId = AdManager.BANNER_TOP_ID)
        }
        // resto da tela...
    }
}
```

### PROMPT 3: Implementar BannerAd Composable

```
Crie um BannerAd composable que exibe AdMob banner.

Requisitos:
1. Usar AndroidView para integrar AdView do AdMob
2. Aceitar adUnitId como parâmetro
3. Carregar ad automaticamente no onResume
4. Pausar no onPause
5. Destruir no onDispose
6. Mostrar placeholder enquanto carrega
7. Esconder se falhar ao carregar

Exemplo:
@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
)
```

### PROMPT 4: Renovação Automática de Créditos

```
Implemente a lógica de renovação mensal de créditos para assinantes.

Requisitos:
1. Quando assinatura é comprada, registrar data de início
2. A cada 30 dias, resetar créditos para o valor do plano:
   - SUBSCRIBER_30: 30 créditos
   - SUBSCRIBER_50: 50 créditos
3. Créditos não utilizados NÃO acumulam
4. Verificar renovação no app start e periodicamente
5. Usar WorkManager para check em background

Campos necessários no UserCreditsEntity:
- subscriptionStartDate: Long? (timestamp)
- lastRenewalDate: Long? (timestamp)
- subscriptionType: String? (enum)

Crie RenewalWorker que:
- Roda diariamente
- Verifica se passou 30 dias desde lastRenewal
- Renova créditos se necessário
- Atualiza lastRenewalDate
```

### PROMPT 5: Verificação de Assinatura no Backend

```
Implemente verificação de assinatura válida.

O BillingClient pode ter cache desatualizado. Precisamos:
1. No app start, chamar queryPurchasesAsync()
2. Verificar se assinatura ainda está ativa
3. Se expirou, rebaixar para FREE
4. Se ainda ativa, manter benefícios

Fluxo:
1. App abre
2. BillingManager.checkActiveSubscriptions()
3. Para cada purchase:
   - Verificar purchaseState == PURCHASED
   - Verificar isAutoRenewing para subs
   - Atualizar SubscriptionManager
4. Se nenhuma sub ativa, setar shouldShowAds = true

Adicione também listener para mudanças em tempo real.
```

### PROMPT 6: Tela de Upgrade/Upsell

```
Crie uma tela de upsell que aparece quando:
1. Usuário fica sem créditos
2. Usuário tenta analisar sem créditos
3. Usuário clica em "Remover anúncios"

A tela deve mostrar:
1. Comparativo dos planos em cards
2. Highlight no plano "Popular" (FalaSério 20)
3. Benefícios de cada plano com ícones
4. Botões de compra que chamam BillingManager
5. Link para "Restaurar compras"

Design:
- Fullscreen modal ou BottomSheet
- Animação de entrada suave
- Cores do tema (Primary para CTAs)
- Disclaimer sobre termos e política
```

### PROMPT 7: Restaurar Compras

```
Implemente função de restaurar compras para:
1. Usuário reinstalou o app
2. Usuário trocou de dispositivo
3. Compra não foi processada corretamente

Fluxo:
1. Usuário clica "Restaurar Compras"
2. Mostrar loading
3. Chamar billingClient.queryPurchasesAsync(INAPP)
4. Chamar billingClient.queryPurchasesAsync(SUBS)
5. Para cada purchase válida:
   - Se LIFETIME/PERPETUAL: setar licença permanente
   - Se SUBSCRIBER: verificar se ainda ativa
6. Atualizar UI com resultado
7. Mostrar toast de sucesso/falha

Tratar casos:
- Nenhuma compra encontrada
- Compra encontrada mas expirada
- Erro de conexão
```

### PROMPT 8: Integrar Créditos Infinitos

```
Para licenças LIFETIME e PERPETUAL, os créditos são infinitos.

Implemente:
1. No UserCredits model, adicionar campo isUnlimited: Boolean
2. No CreditsRepository, nunca decrementar se isUnlimited
3. Na UI, mostrar "∞" ao invés de número
4. No AnalyzeRecordingUseCase, pular check de créditos se unlimited

Modificar CreditsChip:
@Composable
fun CreditsChip(credits: UserCredits, onClick: () -> Unit) {
    val displayText = if (credits.isUnlimited) "∞" else "${credits.available}"
    // ...
}

Modificar useCredit():
suspend fun useCredit(): Boolean {
    val credits = getCredits()
    if (credits.isUnlimited) return true
    if (credits.available <= 0) return false
    // decrementar...
}
```

### PROMPT 9: Webhook de Validação (Opcional)

```
Para validação server-side das compras (anti-fraude), crie um endpoint.

Nota: Isso requer backend. Se não tiver, pular este prompt.

Fluxo ideal:
1. App recebe purchase do Google Play
2. App envia purchaseToken para seu servidor
3. Servidor valida com Google Play Developer API
4. Servidor confirma ou rejeita
5. App libera benefícios apenas se servidor confirmar

Se não tiver backend, confiar no BillingClient local (menos seguro mas funcional).
```

### PROMPT 10: Testes de Billing

```
Configure testes de compra no Google Play Console.

Passos:
1. Adicionar email de teste em Play Console > Setup > License Testing
2. Usar IDs de teste do Google para desenvolvimento:
   - android.test.purchased
   - android.test.canceled
   - android.test.item_unavailable

3. Criar teste instrumentado que:
   - Simula compra de pacote
   - Verifica créditos adicionados
   - Simula assinatura
   - Verifica ads removidos
   - Simula cancelamento
   - Verifica ads voltam

4. Testar edge cases:
   - Compra durante análise em andamento
   - Múltiplas compras simultâneas
   - Perda de conexão durante compra
```

### PROMPT 11: Criar Produtos no Play Console

```
Guia para criar os produtos no Google Play Console:

1. Acessar Play Console > Monetização > Produtos

2. Criar produtos IN-APP (Managed Products):

   falaserio_10:
   - ID: falaserio_10
   - Nome: FalaSério 10
   - Descrição: 10 análises de voz
   - Preço: BRL 10.99
   - Status: Ativo

   falaserio_20:
   - ID: falaserio_20
   - Nome: FalaSério 20
   - Descrição: 20 análises de voz - Melhor custo-benefício!
   - Preço: BRL 16.99
   - Status: Ativo

   falaserio_lifetime:
   - ID: falaserio_lifetime
   - Nome: Licença Vitalícia
   - Descrição: Análises ilimitadas para sempre + 1 ano de suporte
   - Preço: BRL 99.90
   - Status: Ativo

   falaserio_perpetual:
   - ID: falaserio_perpetual
   - Nome: Licença Perpétua
   - Descrição: Análises ilimitadas + Suporte vitalício
   - Preço: BRL 199.90
   - Status: Ativo

3. Criar ASSINATURAS:

   falaserio_30:
   - ID: falaserio_30
   - Nome: FalaSério Mensal
   - Descrição: 30 análises/mês sem anúncios
   - Período: Mensal
   - Preço base: BRL 19.99
   - Período de teste: 3 dias grátis (opcional)

   falaserio_50:
   - ID: falaserio_50
   - Nome: FalaSério Anual
   - Descrição: 50 análises/mês sem anúncios + Suporte WhatsApp
   - Período: Anual
   - Preço base: BRL 59.99
   - Economia vs mensal: ~75%

4. Ativar todos os produtos
5. Publicar app em teste interno primeiro
```

---

## 🔄 FLUXO COMPLETO DE MONETIZAÇÃO

```
┌─────────────────────────────────────────────────────────────┐
│                      APP START                               │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  BillingManager.init()  │
              │  queryPurchases()       │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ SubscriptionManager     │
              │ determineUserState()    │
              └─────────────────────────┘
                            │
            ┌───────────────┼───────────────┐
            ▼               ▼               ▼
       ┌────────┐     ┌──────────┐    ┌──────────┐
       │  FREE  │     │ PACK/SUB │    │ LIFETIME │
       └────────┘     └──────────┘    └──────────┘
            │               │               │
            ▼               ▼               ▼
     ┌────────────┐  ┌────────────┐  ┌────────────┐
     │ showAds=T  │  │ showAds=? │  │ showAds=F  │
     │ credits=3  │  │ credits=N │  │ unlimited  │
     └────────────┘  └────────────┘  └────────────┘
            │               │               │
            └───────────────┴───────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │    HomeScreen loads     │
              │    AdController.       │
              │    shouldShowAds       │
              └─────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
       ┌────────────┐              ┌────────────┐
       │ Show Ads   │              │  No Ads    │
       │ + Banners  │              │  Clean UI  │
       └────────────┘              └────────────┘
```

---

## 📝 CHECKLIST DE MONETIZAÇÃO

- [ ] SubscriptionManager criado
- [ ] AdController implementado
- [ ] BannerAd composable funcionando
- [ ] Lógica de créditos infinitos
- [ ] Renovação mensal automática
- [ ] Tela de upsell
- [ ] Restaurar compras
- [ ] Produtos criados no Play Console
- [ ] Testes de billing passando
- [ ] Ads aparecem para FREE
- [ ] Ads somem para assinantes
- [ ] Créditos renovam corretamente

---

*WebStorage Tecnologia - TOQUE DA LUZ*
