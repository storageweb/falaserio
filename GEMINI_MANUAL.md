# 🤖 MANUAL GEMINI - FalaSério

## Instruções para Gemini no Android Studio finalizar o projeto

**Data:** 2025-01-08
**Projeto:** FalaSério - Revelador da Verdade
**Status:** Estrutura completa, precisa ajustes de compilação

---

## 📋 STATUS ATUAL

### ✅ COMPLETO

- [x] Gradle configuration (libs.versions.toml, build.gradle.kts)
- [x] AndroidManifest.xml com permissões
- [x] Room Database (entities, DAOs, database)
- [x] Domain layer (models, repositories, use cases)
- [x] Data layer (repository implementations)
- [x] Audio Recorder (44.1kHz WAV)
- [x] VSA Analyzer (5 métricas reais)
- [x] Hilt DI modules
- [x] Compose Theme (Material 3 dark)
- [x] Navigation
- [x] UI Screens (Home, History, Credits)
- [x] UI Components (RecordButton, AnalysisResultCard, etc)
- [x] MainViewModel
- [x] AdManager (AdMob)
- [x] BillingManager (Google Play)
- [x] Resources (strings, colors, themes)

### ⚠️ PRECISA AJUSTAR

- [ ] Resolver imports/compilação
- [ ] Gerar ícones PNG (Image Asset Studio)
- [ ] Testar gravação de áudio
- [ ] Testar análise VSA
- [ ] Configurar IDs AdMob reais
- [ ] Criar produtos no Google Play Console

---

## 🚀 PASSO A PASSO

### 1. Abrir projeto no Android Studio

```
File > Open > Z:\Claudio\dev\FalaSerio
```

### 2. Sync Gradle

```
File > Sync Project with Gradle Files
```

### 3. Usar Gemini para corrigir erros

---

## 💬 PROMPTS PARA GEMINI

### PROMPT 1: Análise inicial

```
Analise este projeto Android e liste todos os erros de compilação. 
O projeto usa:
- Kotlin 2.1.0
- Jetpack Compose BOM 2025.01.00
- Hilt 2.51
- Room 2.6.1
- Material 3

Liste os erros e sugira correções.
```

### PROMPT 2: Corrigir imports

```
Corrija todos os imports faltando neste arquivo. 
Mantenha a estrutura existente, apenas adicione os imports necessários.
Use as versões do libs.versions.toml do projeto.
```

### PROMPT 3: Gerar ícones

```
Preciso gerar os ícones do app para todas as densidades.
O ícone deve ser um microfone laranja (#FF6B35) em fundo escuro (#121212).
Me guie pelo processo do Image Asset Studio.
```

### PROMPT 4: Verificar AudioRecorder

```
Revise o AudioRecorder.kt e verifique:
1. Permissões de RECORD_AUDIO estão sendo checadas
2. O formato WAV está correto (44100Hz, 16-bit, mono)
3. O header WAV tem 44 bytes corretos
4. O amplitude está sendo calculado para visualização
Corrija qualquer problema encontrado.
```

### PROMPT 5: Verificar VsaAnalyzer

```
Revise o VsaAnalyzer.kt e verifique:
1. Leitura do arquivo WAV está pulando header corretamente
2. Cálculo de microtremores (8-12Hz) está correto
3. Detecção de pitch por autocorrelação funciona
4. Jitter e Shimmer estão normalizados 0-100
5. HNR está invertido (baixo HNR = alto stress)
Otimize se necessário para performance em dispositivos móveis.
```

### PROMPT 6: Testar Room Database

```
Verifique a configuração do Room:
1. FalaSérioDatabase está com @Database correto
2. Entities têm @Entity e @PrimaryKey
3. DAOs têm @Dao e queries corretas
4. Foreign key cascade está configurado
5. TypeConverters para LocalDateTime funcionam
Crie um teste unitário básico para validar.
```

### PROMPT 7: Verificar Hilt DI

```
Verifique a injeção de dependência Hilt:
1. FalaSérioApp tem @HiltAndroidApp
2. MainActivity tem @AndroidEntryPoint
3. AppModule provê Database e DAOs
4. RepositoryModule faz @Binds correto
5. ViewModels têm @HiltViewModel e @Inject constructor
Liste qualquer problema de grafo de dependência.
```

### PROMPT 8: Testar UI Compose

```
Revise as telas Compose:
1. HomeScreen - botão de gravação, animações de amplitude
2. HistoryScreen - LazyColumn com recordings
3. CreditsScreen - lista de planos
Verifique se os estados do ViewModel estão sendo coletados corretamente.
```

### PROMPT 9: Configurar ProGuard

```
Revise proguard-rules.pro para garantir que:
1. Room entities não sejam ofuscadas
2. Hilt funcione em release
3. Google Play Billing não quebre
4. AdMob funcione corretamente
Adicione regras faltando.
```

### PROMPT 10: Build Release

```
Me ajude a criar um APK de release:
1. Criar keystore para assinatura
2. Configurar signingConfigs no build.gradle.kts
3. Gerar APK assinado
4. Verificar se ProGuard não quebrou nada
```

### PROMPT 11: Preparar para Play Store

```
Liste tudo que preciso para publicar na Play Store:
1. Ícone hi-res (512x512)
2. Feature graphic (1024x500)
3. Screenshots (mínimo 2)
4. Descrição curta e longa
5. Política de privacidade
6. Classificação de conteúdo
Gere templates para cada item.
```

---

## 🔧 CONFIGURAÇÕES IMPORTANTES

### local.properties (criar este arquivo)

```properties
sdk.dir=C:\\Users\\SEU_USUARIO\\AppData\\Local\\Android\\Sdk
```

### IDs AdMob (substituir em AdManager.kt)

```kotlin
// TESTE (já configurado)
const val BANNER_TOP_ID = "ca-app-pub-3940256099942544/6300978111"
const val REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"

// PRODUÇÃO (obter no AdMob Console)
// const val BANNER_TOP_ID = "ca-app-pub-XXXXX/XXXXX"
// const val REWARDED_AD_ID = "ca-app-pub-XXXXX/XXXXX"
```

### AndroidManifest.xml - App ID AdMob

```xml
<!-- Substituir pelo ID real -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"/>
```

---

## 📁 ESTRUTURA DE ARQUIVOS

```
app/src/main/kotlin/br/com/webstorage/falaserio/
├── FalaSérioApp.kt              # Application @HiltAndroidApp
├── ads/
│   └── AdManager.kt             # AdMob rewarded ads
├── audio/
│   ├── analyzer/
│   │   └── VsaAnalyzer.kt       # Análise VSA (5 métricas)
│   └── recorder/
│       └── AudioRecorder.kt     # Gravação WAV 44.1kHz
├── billing/
│   └── BillingManager.kt        # Google Play Billing
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── Daos.kt          # RecordingDao, AnalysisDao, UserCreditsDao
│   │   ├── database/
│   │   │   └── FalaSérioDatabase.kt
│   │   └── entity/
│   │       └── Entities.kt      # RecordingEntity, AnalysisEntity, UserCreditsEntity
│   └── repository/
│       └── RepositoryImpl.kt    # Implementações dos repositories
├── di/
│   └── AppModule.kt             # Hilt modules (Database + Repository)
├── domain/
│   ├── model/
│   │   └── Models.kt            # Recording, VsaAnalysis, VsaMetrics, Verdict
│   ├── repository/
│   │   └── Repositories.kt      # Interfaces RecordingRepository, CreditsRepository
│   └── usecase/
│       └── UseCases.kt          # GetRecordings, SaveRecording, Analyze, etc
├── presentation/
│   ├── MainActivity.kt          # @AndroidEntryPoint
│   ├── navigation/
│   │   └── Navigation.kt        # NavHost + Screen sealed class
│   ├── ui/
│   │   ├── components/
│   │   │   └── Components.kt    # RecordButton, AnalysisResultCard, MetricsGrid
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt    # Tela principal
│   │   │   ├── HistoryScreen.kt # Histórico de gravações
│   │   │   └── CreditsScreen.kt # Compra de créditos
│   │   └── theme/
│   │       ├── Color.kt         # Cores Material 3
│   │       ├── Theme.kt         # FalaSérioTheme
│   │       └── Type.kt          # Typography
│   └── viewmodel/
│       └── MainViewModel.kt     # Estado e lógica da UI
└── utils/                       # (vazio, para futuras utilities)
```

---

## ⚡ COMANDOS ÚTEIS

### Limpar e rebuildar

```
./gradlew clean
./gradlew assembleDebug
```

### Instalar no dispositivo

```
./gradlew installDebug
```

### Rodar testes

```
./gradlew test
./gradlew connectedAndroidTest
```

### Gerar APK release

```
./gradlew assembleRelease
```

---

## 🎯 CHECKLIST FINAL

- [ ] Projeto compila sem erros
- [ ] App abre no emulador/device
- [ ] Permissão de microfone funciona
- [ ] Gravação de áudio funciona
- [ ] Análise VSA retorna resultados
- [ ] Histórico salva e carrega
- [ ] Créditos são consumidos
- [ ] Anúncio recompensado funciona (teste)
- [ ] Tema escuro está bonito
- [ ] Animações do botão funcionam
- [ ] Navigation entre telas funciona

---

## 📞 SUPORTE

**WebStorage Tecnologia** (AS263870)

- Desenvolvedor: Andarilho dos Véus
- IA Assistant: Claudio (TOQUE DA LUZ)

*"A vida ACIMA DE TUDO"*

---

Boa sorte Gemini! 🚀
