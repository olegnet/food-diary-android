@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "net.oleg.fd"
    setCompileSdkVersion(34)

    defaultConfig {
        applicationId = "net.oleg.fd"

        minSdk = 27
        targetSdk = 34

        versionCode = 28
        versionName = "1.22"

        resourceConfigurations.addAll(listOf("en", "ru"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val debug = getByName("debug") {
            val props = Properties().apply { load(rootProject.file("signing.properties").inputStream()) }
            storeFile = file(props.getProperty("storeFile"))
            storePassword = props.getProperty("storePassword")
            keyAlias = props.getProperty("keyAlias")
            keyPassword = props.getProperty("keyPassword")
        }
        register("release") {
            initWith(debug)
        }
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
//            configure<CrashlyticsExtension> {
//                mappingFileUploadEnabled = false
//            }
        }
        val debug by getting {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("debug")
            applicationIdSuffix = ".debug"
        }
        val benchmark by creating {
            initWith(release)
            signingConfig = signingConfigs.findByName("debug")
            matchingFallbacks.add("release")
            proguardFiles("benchmark-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()

        // FIXME
//        allWarningsAsErrors = true

        freeCompilerArgs = freeCompilerArgs + ("-opt-in=kotlin.RequiresOptIn," +
                "com.google.accompanist.permissions.ExperimentalPermissionsApi," +
                "androidx.compose.material3.ExperimentalMaterial3Api")
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compiler.extension.get()
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime.livedata)

    implementation(libs.material)
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)

    implementation(libs.material3)
    implementation(libs.material3.winsize)

    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.compose)
    androidTestImplementation(libs.navigation.testing)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.perf.ktx)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    testImplementation(libs.room.testing)

    implementation(libs.paging.common.ktx)
    implementation(libs.paging.compose)

    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.accompanist.permissions)
    implementation(libs.mlkit.barcode.scanning)

    implementation(libs.kotlinx.serialization.json)

    implementation (libs.datastore.preferences)

    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.core.ktx.test)
    testImplementation(libs.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib) {
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation(libs.ui.test.junit4)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

// must be the last line
apply(plugin = "com.google.gms.google-services")
