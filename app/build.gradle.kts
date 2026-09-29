import java.time.Instant

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.py2c.week"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.py2c.week"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "1.5.0"
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "BUILD_TIME_UTC", "\"${Instant.now()}\"")
    }

    signingConfigs {
        val keystorePath = System.getenv("ANDROID_KEYSTORE_FILE")
        val keystoreFile = when {
            keystorePath != null -> file(keystorePath)
            rootProject.file("release.keystore").exists() -> rootProject.file("release.keystore")
            else -> null
        }
        if (keystoreFile != null) {
            create("release") {
                storeFile = keystoreFile
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                    ?: error("ANDROID_KEYSTORE_PASSWORD is required when a release keystore is present")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                    ?: error("ANDROID_KEY_ALIAS is required when a release keystore is present")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
                    ?: System.getenv("ANDROID_KEYSTORE_PASSWORD")
                    ?: error("ANDROID_KEY_PASSWORD or ANDROID_KEYSTORE_PASSWORD is required")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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

    androidResources {
        noCompress += "mp4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
}
