buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
        classpath("com.google.firebase:perf-plugin:1.4.2")
    }
}

plugins {
    id("com.android.application") version "8.1.0-alpha01" apply false
    id("com.android.library") version "8.1.0-alpha01" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
}