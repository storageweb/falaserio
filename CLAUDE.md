# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Clean build (resolve duplicate class issues)
./gradlew clean assembleDebug

# Run unit tests
./gradlew test

# Run specific test class
./gradlew testDebugUnitTest --tests "br.com.webstorage.falaserio.ClassName"

# Run instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest
```

## Architecture Overview

**FalaSério** is an Android Voice Stress Analysis (VSA) entertainment app.

### Clean Architecture Layers

```
presentation/          # UI (Compose + ViewModels)
├── ui/screens/       # HomeScreen, HistoryScreen, CreditsScreen
├── viewmodel/        # MainViewModel, HistoryViewModel, CreditsViewModel
└── navigation/       # NavGraph with 3 routes

domain/               # Business logic
├── audio/           # VsaAnalyzer (DSP algorithms), AudioRecorder
├── model/           # VsaMetrics (5 stress metrics)
├── usecase/         # AnalyzeAudioUseCase
└── billing/         # BillingManager, ProductInfo

data/                 # Data layer
├── local/           # Room (AppDatabase, DAOs, Entities)
└── repository/      # CreditsRepository, HistoryRepository

di/                   # Hilt modules
├── AudioModule      # Provides AudioRecorder
├── DatabaseModule   # Provides Room + DAOs
└── VsaModule        # Provides VsaAnalyzer
```

### DSP Pipeline (VsaAnalyzer.kt)

The core analysis flow in `domain/audio/VsaAnalyzer.kt`:
1. Read WAV file (44.1kHz, 16-bit PCM)
2. Extract frames (4096 samples, 50% overlap)
3. Apply Hamming window
4. Calculate 5 metrics: Micro-Tremor, Pitch Variation, Jitter, Shimmer, HNR
5. Compute weighted stress score (0-100%)

### Key Technical Details

- **Audio Format**: WAV 44.1kHz 16-bit PCM mono
- **Frame Size**: 4096 samples (~93ms)
- **Hop Size**: 2048 samples (50% overlap)
- **Pitch Range**: 80-400Hz (human voice)
- **Stress Score**: Weighted average (Tremor 30%, Pitch 20%, Jitter 20%, Shimmer 15%, HNR 15%)

## Stack Versions

Defined in `gradle/libs.versions.toml`:
- Kotlin 2.1.0
- Compose BOM 2025.01.00
- Hilt 2.57.2
- Room 2.6.1
- KSP 2.1.0-1.0.29
- AGP 8.7.0
- minSdk 24 / targetSdk 35

## Monetization Products (ProductInfo.kt)

| ID | Type | Description |
|----|------|-------------|
| `pack_10_credits` | INAPP | +10 credits |
| `pack_20_credits` | INAPP | +20 credits |
| `subscriber_30` | SUBS | 30/month + no ads |
| `subscriber_50` | SUBS | 50/month + no ads |
| `lifetime_unlimited` | INAPP | Unlimited forever |
| `perpetual_100` | INAPP | 100 + no ads |

## AdMob Setup

Replace placeholder in `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-YOUR_REAL_APP_ID"/>
```

## LSP Kotlin (Navegação Semântica)

O projeto usa Kotlin LSP para navegação inteligente de código.

**Operações disponíveis:**
```
LSP(operation: "documentSymbol", file: "*.kt")  # Lista símbolos
LSP(operation: "hover", file: "*.kt", line, char)  # Tipos/docs
LSP(operation: "findReferences", ...)  # Encontra referências
LSP(operation: "goToDefinition", ...)  # Vai para definição
```

**Instalação (se necessário):**
```bash
# Binário em /dados/Claudio/tools/server/
# Symlink em ~/.local/bin/kotlin-lsp
curl -L -o server.zip https://github.com/fwcd/kotlin-language-server/releases/latest/download/server.zip
unzip -o server.zip -d /dados/Claudio/tools/
ln -sf /dados/Claudio/tools/server/bin/kotlin-language-server ~/.local/bin/kotlin-lsp
```

## Pending Features

- AdMob rewarded ads integration
- Unit tests
- Widget for quick analysis
