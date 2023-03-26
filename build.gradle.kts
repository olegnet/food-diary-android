buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.4")
        classpath("com.google.firebase:perf-plugin:1.4.2")
    }
}

plugins {
    id("com.android.application") version "8.1.0-alpha10" apply false
    id("com.android.library") version "8.1.0-alpha10" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}