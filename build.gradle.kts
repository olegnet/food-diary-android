buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.0")
        classpath("com.google.firebase:perf-plugin:1.4.1")
    }
}

plugins {
    id("com.android.application") version "7.4.0-alpha03" apply false
    id("com.android.library") version "7.4.0-alpha03" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}