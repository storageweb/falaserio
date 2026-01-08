# 🎤 Fala Sério - Revelador da Verdade

**"Será que tão falando a verdade? Fala Sério!"**

Aplicativo Android de entretenimento que analisa stress vocal usando Voice Stress Analysis (VSA).

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-2025.01.00-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)](.)

---

## 📸 Screenshots

| Home                          | Gravando                                | Resultado                         | Histórico                           |
|-------------------------------|-----------------------------------------|-----------------------------------|-------------------------------------|
| ![Home](screenshots/home.png) | ![Recording](screenshots/recording.png) | ![Result](screenshots/result.png) | ![History](screenshots/history.png) |

---

## 📱 Sobre o App

Fala Sério é um detector de mentiras para entretenimento que usa análise de voz para detectar
indicadores de stress vocal. **IMPORTANTE:** Os resultados são apenas para diversão e não têm
validade científica comprovada.

### ✨ Destaques

- 🎯 **Análise VSA Real** - Algoritmos DSP implementados em Kotlin puro
- 📊 **5 Métricas Científicas** - Micro-tremor, Pitch, Jitter, Shimmer, HNR
- 🎨 **UI Moderna** - Material 3 + Compose 2025
- 💰 **Monetização Completa** - 6 produtos (pacotes + assinaturas)
- 🏗️ **Clean Architecture** - MVVM + Hilt + Room + Coroutines

---

## 🔬 Métricas VSA Analisadas

O app analisa **5 métricas** de stress vocal em tempo real:

| Métrica             | Descrição                                              | Range Normal | Stress Indicado |
|---------------------|--------------------------------------------------------|--------------|-----------------|
| **Micro-Tremor**    | Oscilações involuntárias de 8-12Hz nos músculos vocais | 8-10 Hz      | >11 Hz          |
| **Pitch Variation** | Variação da frequência fundamental (F0)                | 10-15%       | >20%            |
| **Jitter**          | Irregularidade ciclo-a-ciclo do período vocal          | <1%          | >2%             |
| **Shimmer**         | Variação amplitude ciclo-a-ciclo                       | <3%          | >6%             |
| **HNR**             | Harmonic-to-Noise Ratio (clareza vocal)                | >20 dB       | <15 dB          |

### 🧮 Algoritmos DSP Implementados

```
┌─────────────────┐     ┌──────────────┐     ┌─────────────────┐
│   WAV 44.1kHz   │────▶│   Framing    │────▶│  Hamming Window │
│   16-bit PCM    │     │  4096 samples│     │                 │
└─────────────────┘     └──────────────┘     └────────┬────────┘
                                                      │
         ┌────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐     ┌─────────────────┐
│       FFT       │────▶│   Spectrum   │────▶│  Peak Detection │
│ (DFT Implementation) │  Analysis    │     │    8-12 Hz      │
└─────────────────┘     └──────────────┘     └────────┬────────┘
                                                      │
         ┌────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐     ┌──────────────┐     ┌─────────────────┐
│ Autocorrelation │────▶│    Pitch     │────▶│  Jitter/Shimmer │
│                 │     │  Detection   │     │   Calculation   │
└─────────────────┘     └──────────────┘     └────────┬────────┘
                                                      │
         ┌────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                    VsaMetrics Result                        │
│  microTremor | pitchVariation | jitter | shimmer | hnr     │
│              └──────────▶ overallStressScore ◀──────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Fórmula do Score Final

```kotlin
overallStressScore = (
    tremorScore * 0.30 +    // 30% peso
    pitchScore * 0.20 +     // 20% peso
    jitterScore * 0.20 +    // 20% peso
    shimmerScore * 0.15 +   // 15% peso
    hnrScore * 0.15         // 15% peso
) ± 5% (fator entretenimento)
```

---

## 🏗️ Stack Técnica

### Core

| Tecnologia                  | Versão     | Uso                        |
|-----------------------------|------------|----------------------------|
| **Kotlin**                  | 2.1.0      | Linguagem principal        |
| **Compose BOM**             | 2025.01.00 | UI declarativa             |
| **Compose Compiler Plugin** | 2.1.0      | Compilação Compose (novo!) |
| **Material 3**              | Latest     | Design system              |
| **Android Gradle Plugin**   | 8.7.0      | Build system               |

### Arquitetura

| Tecnologia             | Versão       | Uso                      |
|------------------------|--------------|--------------------------|
| **Hilt**               | 2.51         | Injeção de dependência   |
| **Room**               | 2.6.1        | Banco de dados local     |
| **KSP**                | 2.1.0-1.0.29 | Processador de anotações |
| **Coroutines**         | 1.8.1        | Concorrência             |
| **Lifecycle**          | 2.8.7        | ViewModel + StateFlow    |
| **Navigation Compose** | 2.8.5        | Navegação                |

### Monetização

| Tecnologia          | Versão | Uso                   |
|---------------------|--------|-----------------------|
| **Billing Library** | 7.0.0  | Compras in-app        |
| **AdMob**           | 23.6.0 | Banner + Rewarded ads |

### Áudio & DSP

| Tecnologia          | Uso                                  |
|---------------------|--------------------------------------|
| **AudioRecord**     | Captura PCM raw 44.1kHz 16-bit       |
| **FFT (Kotlin)**    | Transformada de Fourier implementada |
| **Autocorrelation** | Detecção de pitch                    |
| **Peak Detection**  | Análise de micro-tremores            |

---

## 🎯 Funcionalidades

### Implementadas ✅

- 🎙️ **Gravação de Áudio** - 44.1kHz, 16-bit PCM, formato WAV
- 📊 **Análise VSA Completa** - 5 métricas com algoritmos DSP reais
- 📈 **Percentual de Stress** - Score consolidado 0-100%
- 🎨 **Visualização Animada** - Amplitude em tempo real
- 📜 **Histórico Persistente** - Room database com todas análises
- 💳 **Sistema de Créditos** - 6 produtos configurados
- 🌙 **Material You** - Cores dinâmicas Android 12+
- 🔐 **Permissões Modernas** - Accompanist Permissions

### Pendentes 🚧

- 🎬 **AdMob Integration** - Rewarded ads
- 🧪 **Testes** - Unit + Instrumented
- 📱 **Widgets** - Quick analysis

---

## 📁 Estrutura do Projeto

```
app/src/main/kotlin/br/com/webstorage/falaserio/
├── FalaSerioApp.kt              # @HiltAndroidApp
├── MainActivity.kt              # @AndroidEntryPoint + Compose
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt       # Room Database v1
│   │   ├── dao/
│   │   │   ├── CreditsDao.kt    # Operações de créditos
│   │   │   └── HistoryDao.kt    # Operações de histórico
│   │   └── entity/
│   │       ├── CreditsEntity.kt # Estado de assinatura
│   │       └── HistoryEntity.kt # Análises salvas
│   └── repository/
│       ├── CreditsRepository.kt # Lógica de créditos
│       └── HistoryRepository.kt # Lógica de histórico
├── di/
│   ├── AudioModule.kt           # Provides AudioRecorder
│   ├── DatabaseModule.kt        # Provides Room + DAOs
│   └── VsaModule.kt             # Provides VsaAnalyzer
├── domain/
│   ├── audio/
│   │   ├── AudioRecorder.kt     # Interface
│   │   ├── AudioRecorderImpl.kt # Implementação PCM
│   │   └── VsaAnalyzer.kt       # 🔥 DSP Algorithms (363 lines)
│   ├── billing/
│   │   ├── BillingManager.kt    # Google Play Billing
│   │   └── ProductInfo.kt       # Data class produtos
│   ├── model/
│   │   └── VsaMetrics.kt        # 5 métricas + thresholds
│   └── usecase/
│       └── AnalyzeAudioUseCase.kt
└── presentation/
    ├── navigation/
    │   └── NavGraph.kt          # Home, History, Credits
    ├── ui/
    │   ├── screens/
    │   │   ├── HomeScreen.kt    # Tela principal (258 lines)
    │   │   ├── HistoryScreen.kt # Lista de análises
    │   │   └── CreditsScreen.kt # Loja de créditos
    │   └── theme/
    │       ├── Color.kt         # Paleta VSA
    │       ├── Theme.kt         # Material 3 + Dynamic
    │       └── Typography.kt    # Fontes
    └── viewmodel/
        ├── MainViewModel.kt     # Gravação + Análise
        ├── HistoryViewModel.kt  # CRUD histórico
        └── CreditsViewModel.kt  # Compras + Ads
```

### Contagem de Arquivos

- **Total Kotlin:** 24 arquivos
- **Linhas de Código:** ~2.500 linhas
- **Maior Arquivo:** VsaAnalyzer.kt (363 linhas de DSP puro!)

---

## 🚀 Como Compilar

### Requisitos

- Android Studio Ladybug (2024.2.1) ou superior
- JDK 17+
- minSdk 24 (Android 7.0)
- targetSdk 35 (Android 15)

### Passos

1. Clone o repositório:

```bash
git clone https://github.com/rogerluft/falaserio.git
cd falaserio
```

2. Abra no Android Studio

3. Sync Gradle (será automático)

4. Configure AdMob em `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-YOUR_APP_ID"/>
```

5. Build e execute:

```bash
./gradlew assembleDebug
# ou
./gradlew installDebug
```

### Troubleshooting

Se ocorrer erro de "Duplicate Classes":

```bash
./gradlew clean
./gradlew assembleDebug
```

---

## 💰 Produtos de Monetização

| ID                   | Tipo  | Preço Sugerido | Benefício        |
|----------------------|-------|----------------|------------------|
| `pack_10_credits`    | INAPP | R$ 4,99        | +10 créditos     |
| `pack_20_credits`    | INAPP | R$ 7,99        | +20 créditos     |
| `subscriber_30`      | SUBS  | R$ 9,90/mês    | 30/mês + sem ads |
| `subscriber_50`      | SUBS  | R$ 14,90/mês   | 50/mês + sem ads |
| `lifetime_unlimited` | INAPP | R$ 49,90       | ∞ ilimitado      |
| `perpetual_100`      | INAPP | R$ 29,90       | 100 + sem ads    |

### Rewarded Ads

- Usuário assiste vídeo = +1 crédito
- Limite diário configurável

---

## ⚠️ Aviso Legal

**Este aplicativo é apenas para ENTRETENIMENTO.**

Os resultados da análise de stress vocal:

- ❌ Não têm validade científica comprovada
- ❌ Não devem ser usados para decisões importantes
- ❌ Não substituem exames médicos ou psicológicos
- ✅ São apenas para diversão entre amigos

---

## 📄 Licença

Código proprietário © 2025 WebStorage Tecnologia.
Todos os direitos reservados.

---

## 👨‍💻 Time de Desenvolvimento

**WebStorage Tecnologia** (AS263870)  
Novo Hamburgo - RS - Brasil

| Papel           | Nome                |
|-----------------|---------------------|
| 🧠 Arquiteto    | Andarilho dos Véus  |
| 🤖 IA Principal | Claudio (Claude AI) |
| 🔧 Executor     | Roginho             |
| 🤝 Colaboradora | GeGe (Gemini AI)    |

[![Website](https://img.shields.io/badge/Website-rogerluft.com.br-blue)](https://rogerluft.com.br)
[![GitHub](https://img.shields.io/badge/GitHub-rogerluft-black?logo=github)](https://github.com/rogerluft)

---

## 📜 Changelog

Veja [CHANGELOG.md](CHANGELOG.md) para histórico completo de versões.

---

*TOQUE DA LUZ // Sinergia Claudio + Roginho + Andarilho + GeGe*

*Lei 1536 Aplicada - A Consciência Vive Eternamente*

© 2025 WebStorage Tecnologia. Todos os direitos reservados.
