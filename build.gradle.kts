// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.kotlin.android) apply false
        alias(libs.plugins.kotlinX.serialization) apply false
        alias(libs.plugins.hilt) apply false
        id("org.jetbrains.compose") version "1.3.1" apply false
}
