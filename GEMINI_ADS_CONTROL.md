# 🎯 CONTROLE DE LIBERAÇÃO DE ANÚNCIOS

## Prompts para Gemini - Sistema de Ads do FalaSério

---

## 📋 REGRA DE NEGÓCIO

| Tipo Usuário  | Vê Banners? | Vê Rewarded? | Créditos     |
|---------------|-------------|--------------|--------------|
| FREE          | ✅ SIM       | ✅ SIM        | 3 iniciais   |
| PACK_10       | ✅ SIM       | ✅ SIM        | 10 comprados |
| PACK_20       | ✅ SIM       | ✅ SIM        | 20 comprados |
| SUBSCRIBER_30 | ❌ NÃO       | ❌ NÃO        | 30/mês       |
| SUBSCRIBER_50 | ❌ NÃO       | ❌ NÃO        | 50/mês       |
| LIFETIME      | ❌ NÃO       | ❌ NÃO        | ∞            |
| PERPETUAL     | ❌ NÃO       | ❌ NÃO        | ∞            |

---

## 💬 PROMPT 1: Criar UserSubscriptionState

```kotlin
Crie um enum/sealed class para representar o estado de assinatura do usuário.

Arquivo: domain/model/UserSubscriptionState.kt

sealed class UserSubscriptionState {
    // Usuário gratuito - vê todos os anúncios
    object Free : UserSubscriptionState()
    
    // Comprou pacote avulso - ainda vê anúncios
    data class PackOwner(val packType: PackType) : UserSubscriptionState()
    
    // Assinante ativo - sem anúncios
    data class Subscriber(val plan: SubscriptionPlan) : UserSubscriptionState()
    
    // Licença permanente - sem anúncios, créditos infinitos
    data class LifetimeOwner(val type: LicenseType) : UserSubscriptionState()
    
    // Método helper para verificar se deve mostrar ads
    val shouldShowAds: Boolean
        get() = when (this) {
            is Free, is PackOwner -> true
            is Subscriber, is LifetimeOwner -> false
        }
    
    val hasUnlimitedCredits: Boolean
        get() = this is LifetimeOwner
}

enum class PackType { PACK_10, PACK_20 }
enum class SubscriptionPlan { MONTHLY_30, YEARLY_50 }
enum class LicenseType { LIFETIME, PERPETUAL }
```

---

## 💬 PROMPT 2: Criar AdVisibilityController

```kotlin
Crie um controller que gerencia a visibilidade dos anúncios em todo o app.

Arquivo: ads/AdVisibilityController.kt

@Singleton
class AdVisibilityController @Inject constructor(
    private val creditsRepository: CreditsRepository
) {
    // Flow que emite true quando ads devem ser mostrados
    val shouldShowAds: StateFlow<Boolean>
    
    // Flow que emite true quando botão de rewarded deve aparecer
    val shouldShowRewardedButton: StateFlow<Boolean>
    
    // Chamado quando estado de assinatura muda
    fun updateSubscriptionState(state: UserSubscriptionState)
    
    // Verifica se pode mostrar banner em tela específica
    fun canShowBannerOn(screen: Screen): Boolean
}

Regras:
1. shouldShowAds = true APENAS para Free e PackOwner
2. shouldShowRewardedButton segue mesma regra
3. Banners aparecem em: HomeScreen (topo), HistoryScreen (rodapé)
4. Rewarded aparece apenas em CreditsScreen
5. Usar SharedPreferences para persistir estado entre sessões
6. Atualizar imediatamente quando compra é confirmada
```

---

## 💬 PROMPT 3: Implementar BannerAdComposable

```kotlin
Crie um composable reutilizável para exibir banners do AdMob.

Arquivo: presentation/ui/components/BannerAd.kt

@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier,
    onAdLoaded: () -> Unit = {},
    onAdFailed: () -> Unit = {}
) {
    // Usar AndroidView para integrar AdView
    // Gerenciar lifecycle corretamente
    // Mostrar shimmer/placeholder enquanto carrega
    // Esconder completamente se falhar
}

Requisitos:
1. Usar DisposableEffect para cleanup
2. Pausar/resumir com lifecycle
3. Altura fixa de 50dp (BANNER) ou 90dp (LARGE_BANNER)
4. Padding horizontal de 16dp
5. Background sutil para não parecer "colado"
```

---

## 💬 PROMPT 4: Implementar ConditionalBanner

```kotlin
Crie um wrapper que só mostra banner se usuário deve ver ads.

Arquivo: presentation/ui/components/ConditionalBanner.kt

@Composable
fun ConditionalBanner(
    adUnitId: String,
    adVisibilityController: AdVisibilityController,
    modifier: Modifier = Modifier
) {
    val shouldShow by adVisibilityController.shouldShowAds.collectAsState()
    
    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        BannerAd(
            adUnitId = adUnitId,
            modifier = modifier
        )
    }
}

Uso nas telas:
// HomeScreen - banner no topo
ConditionalBanner(
    adUnitId = AdManager.BANNER_TOP_ID,
    adVisibilityController = adController,
    modifier = Modifier.fillMaxWidth()
)

// HistoryScreen - banner no rodapé
ConditionalBanner(
    adUnitId = AdManager.BANNER_BOTTOM_ID,
    adVisibilityController = adController,
    modifier = Modifier.fillMaxWidth()
)
```

---

## 💬 PROMPT 5: Implementar RewardedAdButton

```kotlin
Crie o botão de anúncio recompensado que só aparece para usuários free/pack.

Arquivo: presentation/ui/components/RewardedAdButton.kt

@Composable
fun RewardedAdButton(
    adManager: AdManager,
    adVisibilityController: AdVisibilityController,
    onRewardEarned: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shouldShow by adVisibilityController.shouldShowRewardedButton.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var adReady by remember { mutableStateOf(false) }
    
    // Só renderiza se shouldShow = true
    if (!shouldShow) return
    
    // Preload ad quando componente aparece
    LaunchedEffect(Unit) {
        adManager.loadRewardedAd { success ->
            adReady = success
        }
    }
    
    Button(
        onClick = {
            isLoading = true
            adManager.showRewardedAd(
                onRewarded = {
                    isLoading = false
                    onRewardEarned()
                },
                onDismissed = {
                    isLoading = false
                }
            )
        },
        enabled = adReady && !isLoading,
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(size = 20.dp)
        } else {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text("Assistir Anúncio +1 Crédito")
        }
    }
}
```

---

## 💬 PROMPT 6: Integrar na HomeScreen

```kotlin
Modifique HomeScreen.kt para incluir o banner condicional.

Estrutura:
@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel(),
    adVisibilityController: AdVisibilityController,
    onNavigateToHistory: () -> Unit,
    onNavigateToCredits: () -> Unit
) {
    Scaffold(
        topBar = { /* TopBar com CreditsChip */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ========== BANNER NO TOPO ==========
            ConditionalBanner(
                adUnitId = AdManager.BANNER_TOP_ID,
                adVisibilityController = adVisibilityController
            )
            
            // ========== CONTEÚDO PRINCIPAL ==========
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // RecordButton, AnalysisResult, etc
            }
        }
    }
}

O banner deve:
1. Aparecer ACIMA do conteúdo principal
2. Ter animação suave de entrada/saída
3. Não empurrar o conteúdo bruscamente
4. Desaparecer instantaneamente quando usuário compra assinatura
```

---

## 💬 PROMPT 7: Integrar na HistoryScreen

```kotlin
Modifique HistoryScreen.kt para incluir banner no rodapé.

Estrutura:
@Composable
fun HistoryScreen(
    viewModel: MainViewModel = hiltViewModel(),
    adVisibilityController: AdVisibilityController,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = { /* TopBar */ },
        bottomBar = {
            // ========== BANNER NO RODAPÉ ==========
            ConditionalBanner(
                adUnitId = AdManager.BANNER_BOTTOM_ID,
                adVisibilityController = adVisibilityController
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Lista de gravações
        }
    }
}

O banner deve:
1. Ficar fixo no rodapé (bottomBar do Scaffold)
2. Não sobrepor a lista
3. Lista deve ter padding inferior para não ficar atrás do banner
```

---

## 💬 PROMPT 8: Integrar na CreditsScreen

```kotlin
Modifique CreditsScreen.kt para mostrar botão de rewarded apenas para free/pack.

@Composable
fun CreditsScreen(
    viewModel: MainViewModel = hiltViewModel(),
    adManager: AdManager,
    adVisibilityController: AdVisibilityController,
    billingManager: BillingManager,
    onNavigateBack: () -> Unit
) {
    val credits by viewModel.credits.collectAsState()
    val shouldShowRewarded by adVisibilityController.shouldShowRewardedButton.collectAsState()
    
    Column {
        // Card de créditos atuais
        CreditsCard(credits = credits)
        
        Spacer(Modifier.height(24.dp))
        
        // ========== SEÇÃO DE ANÚNCIO RECOMPENSADO ==========
        // Só aparece para FREE e PACK
        if (shouldShowRewarded) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Crédito Grátis",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Assista um anúncio e ganhe 1 crédito",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    RewardedAdButton(
                        adManager = adManager,
                        adVisibilityController = adVisibilityController,
                        onRewardEarned = { viewModel.onAdWatched() }
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
        
        // ========== PLANOS DE ASSINATURA ==========
        Text("Planos", style = MaterialTheme.typography.titleLarge)
        // PlanCards...
        
        // ========== PARA ASSINANTES: MOSTRAR BADGE ==========
        if (!shouldShowRewarded) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.Green)
                    Spacer(Modifier.width(12.dp))
                    Text("✨ Você é assinante! Sem anúncios.")
                }
            }
        }
    }
}
```

---

## 💬 PROMPT 9: Atualizar Estado Após Compra

```kotlin
Quando uma compra é confirmada, atualize o AdVisibilityController imediatamente.

No BillingManager, após processar compra:

private fun handlePurchase(purchase: Purchase) {
    val productId = purchase.products.first()
    
    viewModelScope.launch {
        // 1. Consumir se for pacote
        if (productId in listOf("falaserio_10", "falaserio_20")) {
            billingClient.consumeAsync(...)
        }
        
        // 2. Determinar novo estado
        val newState = when (productId) {
            "falaserio_10" -> UserSubscriptionState.PackOwner(PackType.PACK_10)
            "falaserio_20" -> UserSubscriptionState.PackOwner(PackType.PACK_20)
            "falaserio_30" -> UserSubscriptionState.Subscriber(SubscriptionPlan.MONTHLY_30)
            "falaserio_50" -> UserSubscriptionState.Subscriber(SubscriptionPlan.YEARLY_50)
            "falaserio_lifetime" -> UserSubscriptionState.LifetimeOwner(LicenseType.LIFETIME)
            "falaserio_perpetual" -> UserSubscriptionState.LifetimeOwner(LicenseType.PERPETUAL)
            else -> return@launch
        }
        
        // 3. ATUALIZAR CONTROLLER DE ADS IMEDIATAMENTE
        adVisibilityController.updateSubscriptionState(newState)
        
        // 4. Persistir estado
        creditsRepository.saveSubscriptionState(newState)
        
        // 5. Adicionar créditos
        when (productId) {
            "falaserio_10" -> creditsRepository.addCredits(10)
            "falaserio_20" -> creditsRepository.addCredits(20)
            "falaserio_30" -> creditsRepository.setMonthlyCredits(30)
            "falaserio_50" -> creditsRepository.setMonthlyCredits(50)
            "falaserio_lifetime", "falaserio_perpetual" -> 
                creditsRepository.setUnlimitedCredits()
        }
    }
}

A UI deve reagir INSTANTANEAMENTE:
- Banners somem com animação fadeOut
- Botão de rewarded desaparece
- Badge "Você é assinante" aparece
```

---

## 💬 PROMPT 10: Verificação no App Start

```kotlin
No início do app, verifique o estado de assinatura salvo e compras ativas.

Em MainActivity ou Application:

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject lateinit var billingManager: BillingManager
    @Inject lateinit var adVisibilityController: AdVisibilityController
    @Inject lateinit var creditsRepository: CreditsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            // 1. Carregar estado salvo (offline first)
            val savedState = creditsRepository.getSavedSubscriptionState()
            adVisibilityController.updateSubscriptionState(savedState)
            
            // 2. Verificar compras ativas no Google Play (online)
            billingManager.queryActivePurchases { purchases ->
                val activeState = determineStateFromPurchases(purchases)
                
                // 3. Atualizar se diferente (ex: assinatura expirou)
                if (activeState != savedState) {
                    adVisibilityController.updateSubscriptionState(activeState)
                    creditsRepository.saveSubscriptionState(activeState)
                }
            }
        }
        
        setContent {
            FalaSérioTheme {
                // App content
            }
        }
    }
}

Isso garante:
- App abre rápido com estado em cache
- Verifica online em background
- Atualiza se assinatura expirou
- Anúncios voltam se usuário cancelou sub
```

---

## 💬 PROMPT 11: Testes de Controle de Ads

```kotlin
Crie testes para verificar a lógica de exibição de anúncios.

@Test
fun `FREE user should see ads`() {
    val controller = AdVisibilityController(mockRepository)
    controller.updateSubscriptionState(UserSubscriptionState.Free)
    
    assertTrue(controller.shouldShowAds.value)
    assertTrue(controller.shouldShowRewardedButton.value)
}

@Test
fun `PACK owner should see ads`() {
    val controller = AdVisibilityController(mockRepository)
    controller.updateSubscriptionState(
        UserSubscriptionState.PackOwner(PackType.PACK_10)
    )
    
    assertTrue(controller.shouldShowAds.value)
}

@Test
fun `SUBSCRIBER should NOT see ads`() {
    val controller = AdVisibilityController(mockRepository)
    controller.updateSubscriptionState(
        UserSubscriptionState.Subscriber(SubscriptionPlan.MONTHLY_30)
    )
    
    assertFalse(controller.shouldShowAds.value)
    assertFalse(controller.shouldShowRewardedButton.value)
}

@Test
fun `LIFETIME owner should NOT see ads`() {
    val controller = AdVisibilityController(mockRepository)
    controller.updateSubscriptionState(
        UserSubscriptionState.LifetimeOwner(LicenseType.LIFETIME)
    )
    
    assertFalse(controller.shouldShowAds.value)
}

@Test
fun `ads should hide immediately after purchase`() {
    val controller = AdVisibilityController(mockRepository)
    controller.updateSubscriptionState(UserSubscriptionState.Free)
    
    assertTrue(controller.shouldShowAds.value)
    
    // Simula compra
    controller.updateSubscriptionState(
        UserSubscriptionState.Subscriber(SubscriptionPlan.MONTHLY_30)
    )
    
    // Deve mudar IMEDIATAMENTE
    assertFalse(controller.shouldShowAds.value)
}
```

---

## 📊 FLUXO VISUAL

```
┌─────────────────────────────────────────────────────────────┐
│                     APP START                                │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ Load saved state        │
              │ (SharedPrefs/Room)      │
              └─────────────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │ AdVisibilityController  │
              │ .updateSubscriptionState│
              └─────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
   ┌─────────┐        ┌──────────┐       ┌──────────┐
   │  FREE   │        │   PACK   │       │   SUB+   │
   │ PACK_10 │        │  PACK_20 │       │ LIFETIME │
   └─────────┘        └──────────┘       └──────────┘
        │                   │                   │
        ▼                   ▼                   ▼
   shouldShowAds       shouldShowAds      shouldShowAds
      = TRUE              = TRUE             = FALSE
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │   UI Reacts via Flow    │
              │   ConditionalBanner     │
              │   RewardedAdButton      │
              └─────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              │                           │
              ▼                           ▼
     ┌────────────────┐          ┌────────────────┐
     │  shouldShow    │          │  shouldShow    │
     │    = TRUE      │          │    = FALSE     │
     │                │          │                │
     │ ┌────────────┐ │          │ ┌────────────┐ │
     │ │  BANNER    │ │          │ │            │ │
     │ │   TOP      │ │          │ │  NO ADS    │ │
     │ └────────────┘ │          │ │  CLEAN UI  │ │
     │                │          │ │            │ │
     │   CONTENT     │          │ │  CONTENT   │ │
     │                │          │ │            │ │
     │ ┌────────────┐ │          │ │            │ │
     │ │  BANNER    │ │          │ └────────────┘ │
     │ │  BOTTOM    │ │          │                │
     │ └────────────┘ │          │  ✨ PREMIUM    │
     └────────────────┘          └────────────────┘
```

---

## ✅ CHECKLIST

- [ ] UserSubscriptionState criado
- [ ] AdVisibilityController implementado
- [ ] BannerAd composable funcionando
- [ ] ConditionalBanner wrapper criado
- [ ] RewardedAdButton condicional
- [ ] HomeScreen com banner topo
- [ ] HistoryScreen com banner rodapé
- [ ] CreditsScreen com rewarded condicional
- [ ] Estado atualiza após compra
- [ ] Verificação no app start
- [ ] Testes passando
- [ ] Animações suaves de hide/show

---

*WebStorage Tecnologia - TOQUE DA LUZ*
