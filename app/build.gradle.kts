plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") // NOVO: Substitui composeOptions
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "br.com.webstorage.falaserio"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.webstorage.falaserio"
        minSdk = 24 // Android 7.0 - suporte total às APIs de áudio modernas
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // REMOVIDO: composeOptions { kotlinCompilerExtensionVersion } 
    // O novo plugin kotlin.plugin.compose gerencia isso automaticamente

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ========== COMPOSE BOM 2025.01.00 ==========
    val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Compose Runtime
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ========== HILT 2.57.2 ==========
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")

    // ========== ROOM COMPILER ==========
    ksp("androidx.room:room-compiler:2.6.1")
    
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ========== ROOM 2.6.1 ==========
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    
    // Removido ksp duplicado
    // ksp("androidx.room:room-compiler:${versions.room}") 

    // ========== COROUTINES ==========
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // ========== LIFECYCLE ==========
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // ========== ADMOB ==========
    implementation("com.google.android.gms:play-services-ads:23.6.0")

    // ========== BILLING LIBRARY 7.0 (6.0+ obrigatório) ==========
    implementation("com.android.billingclient:billing-ktx:7.0.0")

    // ========== ACCOMPANIST (Permissões) ==========
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // ========== CORE ==========
    implementation("androidx.core:core-ktx:1.15.0")

    // ========== DATASTORE ==========
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ========== WORK MANAGER ==========
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // ========== DEBUG ==========
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ========== TEST ==========
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.13")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
