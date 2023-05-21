buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.5")
        classpath("com.google.firebase:perf-plugin:1.4.2")
    }
}

plugins {
    id("com.android.application") version "8.2.0-alpha04" apply false
    id("com.android.library") version "8.2.0-alpha04" apply false

    // When changing Kotlin version, also check 'kotlinCompilerExtensionVersion' in app/build.gradle.kts
    // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    kotlin("plugin.serialization") version "1.8.21" apply false
    id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
}