// Top-level build file - FalaSério
// Corrigido para Kotlin 2.1.0 + Compose 2025.01.00

plugins {
    id("com.android.application") version "8.7.0" apply false
    id("com.android.library") version "8.7.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false // NOVO: Compose Compiler Plugin
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false // KSP compatível com Kotlin 2.1.0
}

// Limpar build directory
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
