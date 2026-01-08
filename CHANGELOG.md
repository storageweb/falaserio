# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [0.1.0-alpha] - 2025-01-08

### 🎉 Release Inicial - Arquitetura Completa

Primeira versão funcional do FalaSério com toda a arquitetura Clean Architecture + MVVM
implementada.

### Adicionado

#### 🏗️ Infraestrutura

- **Gradle Configuration**
    - Kotlin 2.1.0 com Compose Compiler Plugin (novo método!)
    - Compose BOM 2025.01.00 (versão mais recente)
    - KSP 2.1.0-1.0.29 para Room
    - Hilt 2.51 para DI
    - Room 2.6.1 para persistência
    - Billing Library 7.0.0 para monetização
    - minSdk 24, targetSdk 35

- **Hilt Modules**
    - `AudioModule.kt` - Provides AudioRecorder com @ApplicationContext
    - `DatabaseModule.kt` - Provides Room Database + DAOs
    - `VsaModule.kt` - Provides VsaAnalyzer + UseCase

#### 🎤 Camada de Áudio

- **AudioRecorder Interface** - Contrato para gravação
- **AudioRecorderImpl** - Implementação com AudioRecord
    - 44.1kHz sample rate
    - 16-bit PCM mono
    - Buffer 4096 samples
    - StateFlows para isRecording, duration, amplitude
    - Salva arquivos WAV com header correto

#### 🔬 Análise VSA (Voice Stress Analysis)

- **VsaAnalyzer.kt** - 363 linhas de DSP puro em Kotlin!
    - `readWavFile()` - Parser de WAV 16-bit PCM
    - `extractFrames()` - Windowing com Hamming
    - `fft()` - Transformada de Fourier (DFT)
    - `calculateMicroTremor()` - Detecção 8-12Hz via FFT
    - `calculatePitchVariation()` - Autocorrelation pitch detection
    - `calculateJitter()` - Variação período ciclo-a-ciclo
    - `calculateShimmer()` - Variação amplitude ciclo-a-ciclo
    - `calculateHNR()` - Harmonic-to-Noise Ratio

- **VsaMetrics.kt** - Data class com 5 métricas
    - Thresholds científicos para cada métrica
    - Propriedades booleanas `indicatesStress`
    - `getStressLevel()` retorna texto localizado
    - Score ponderado com ±5% randomness

- **AnalyzeAudioUseCase.kt** - Use case wrapper

#### 💾 Camada de Dados

- **Room Database v1**
    - `HistoryEntity` - Gravações com todas métricas
    - `CreditsEntity` - Estado de assinatura/créditos
    - `HistoryDao` - CRUD com Flow
    - `CreditsDao` - Operações atômicas

- **Repositories**
    - `HistoryRepository` - Salva análises + deleta arquivos
    - `CreditsRepository` - Lógica de créditos/assinaturas

#### 💳 Monetização

- **BillingManager.kt** - Google Play Billing 7.0.0
    - 4 produtos INAPP configurados
    - 2 assinaturas configuradas
    - Query de produtos assíncrono
    - Consumo de compras

- **ProductInfo.kt** - Data class para produtos

#### 🎨 Apresentação

- **Theme**
    - `Color.kt` - Paleta VSA (verde/vermelho/amarelo)
    - `Theme.kt` - Material 3 + Dynamic Colors
    - `Typography.kt` - Escala tipográfica completa

- **Navigation**
    - `NavGraph.kt` - 3 rotas: Home, History, Credits

- **Screens**
    - `HomeScreen.kt` - Gravação com animações
    - `HistoryScreen.kt` - Lista de análises
    - `CreditsScreen.kt` - Loja de créditos

- **ViewModels**
    - `MainViewModel.kt` - Gravação + Análise
    - `HistoryViewModel.kt` - CRUD histórico
    - `CreditsViewModel.kt` - Compras + Ads

#### 📱 App

- `FalaSerioApp.kt` - @HiltAndroidApp
- `MainActivity.kt` - @AndroidEntryPoint + Compose
- `AndroidManifest.xml` - Permissões + AdMob meta

### Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ HomeScreen  │  │HistoryScreen│  │CreditsScreen│         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                 │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐         │
│  │MainViewModel│  │HistoryVM   │  │CreditsVM   │          │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
└─────────┼────────────────┼────────────────┼─────────────────┘
          │                │                │
┌─────────┼────────────────┼────────────────┼─────────────────┐
│         │           DOMAIN                │                 │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐         │
│  │AnalyzeUCase │  │ VsaAnalyzer │  │BillingMgr  │          │
│  └──────┬──────┘  └─────────────┘  └─────────────┘         │
│         │                                                   │
│  ┌──────▼──────┐                                           │
│  │AudioRecorder│                                           │
│  └─────────────┘                                           │
└─────────────────────────────────────────────────────────────┘
          │
┌─────────┼───────────────────────────────────────────────────┐
│         │              DATA                                 │
│  ┌──────▼──────┐  ┌─────────────┐  ┌─────────────┐         │
│  │HistoryRepo  │  │CreditsRepo  │  │ AppDatabase │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                 │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐         │
│  │ HistoryDao  │  │ CreditsDao  │  │   Room DB   │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Colaboradores

| Contribuidor        | Papel                       |
|---------------------|-----------------------------|
| Andarilho dos Véus  | Arquiteto / Product Owner   |
| Claudio (Claude AI) | Desenvolvedor Principal     |
| Roginho             | Executor / QA               |
| GeGe (Gemini AI)    | Consultora de Imports/Stack |

### Estatísticas

- **Arquivos Kotlin criados:** 24
- **Linhas de código:** ~2.500
- **Maior arquivo:** VsaAnalyzer.kt (363 linhas)
- **Módulos Hilt:** 3
- **Entidades Room:** 2
- **Telas Compose:** 3
- **ViewModels:** 3
- **Produtos Billing:** 6

---

## [Unreleased]

### Planejado

- [ ] Integração AdMob (Rewarded Ads)
- [ ] Testes unitários (JUnit5 + MockK)
- [ ] Testes instrumentados (Compose Testing)
- [ ] CI/CD com GitHub Actions
- [ ] Publicação Play Store (Closed Testing)
- [ ] Widget de análise rápida
- [ ] Compartilhamento de resultados
- [ ] Análise offline completa

---

## Notas de Desenvolvimento

### Por que Kotlin 2.1.0?

- Novo Compose Compiler Plugin automático
- Melhor performance de compilação
- Suporte completo a K2 compiler

### Por que KSP ao invés de KAPT?

- 2x mais rápido que KAPT
- Suporte nativo para Room 2.6+
- Melhor integração com Kotlin 2.x

### Por que Clean Architecture?

- Separação clara de responsabilidades
- Testabilidade independente por camada
- Facilidade de manutenção e evolução
- Padrão da indústria Android

### Por que DSP em Kotlin puro?

- Sem dependência de bibliotecas nativas
- Controle total sobre algoritmos
- Portabilidade garantida
- Facilidade de debug

---

*TOQUE DA LUZ - Lei 1536 Aplicada*

*A Sinergia Entre Humanos e IAs Produz Maravilhas*
