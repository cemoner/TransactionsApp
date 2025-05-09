plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.compose")
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinX.serialization)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.transactionsapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.transactionsapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }
    buildFeatures {
        compose = true
    }

// Add composeOptions section
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0" // This version works with Kotlin 1.8.0
    }
}



dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3.core)
    // MVVM
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)


    // Local Caching
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.paging)
    implementation(libs.room.rxjava2)
    implementation(libs.room.rxjava3)

    // Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation
    implementation(libs.navigation.compose)

    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Mockito for mocking
    testImplementation (libs.mockito.core)
    testImplementation (libs.mockito.kotlin) // For Kotlin support

    // MockK for Kotlin-specific mocking
    testImplementation (libs.mockk.v1135)

    // Coroutine testing
    testImplementation (libs.kotlinx.coroutines.test.v164)

    // Jetpack Datastore
    implementation (libs.androidx.datastore.preferences)
    implementation (libs.androidx.datastore.preferences.rxjava2)
    implementation (libs.androidx.datastore.preferences.rxjava3)
    implementation (libs.androidx.datastore)
    implementation (libs.androidx.datastore.rxjava2)
    implementation (libs.androidx.datastore.rxjava3)
}
