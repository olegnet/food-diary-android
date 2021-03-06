import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    kotlin("plugin.serialization").version("1.7.0")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "net.oleg.fd"
    setCompileSdkVersion(33)

    defaultConfig {
        applicationId = "net.oleg.fd"

        minSdk = 27
        targetSdk = 33

        versionCode = 13
        versionName = "1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
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
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        allWarningsAsErrors = true
        freeCompilerArgs = freeCompilerArgs + ("-opt-in=kotlin.RequiresOptIn," +
                "com.google.accompanist.permissions.ExperimentalPermissionsApi," +
                "androidx.compose.material3.ExperimentalMaterial3Api")
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
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
    val composeVersion = "1.2.0-rc03"
    val cameraxVersion = "1.2.0-alpha03"
    val navigationVersion = "2.5.0"
    val roomVersion = "2.5.0-alpha02"
    val material3Version = "1.0.0-alpha13"
    val lifecycleVersion = "2.5.0-rc01"

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("com.google.android.material:material:1.6.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")

    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")

    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")

    implementation(platform("com.google.firebase:firebase-bom:30.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")

    implementation("androidx.paging:paging-common-ktx:3.1.1")
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("com.google.mlkit:barcode-scanning:17.0.2")
    implementation("com.google.accompanist:accompanist-permissions:0.24.9-beta")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.jakewharton.timber:timber:5.0.1")

    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:$roomVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.2")
    testImplementation("androidx.test:core-ktx:1.4.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("org.robolectric:robolectric:4.8")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0") {
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navigationVersion")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

// must be the last line
apply(plugin = "com.google.gms.google-services")
