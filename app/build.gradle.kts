plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.picoxr.destinationvideo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.picoxr.destinationvideo"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        register("platform") {
            storeFile = file("keystore/platform.keystore")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("platform")
        }

        release {
            signingConfig = signingConfigs.getByName("platform")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        noCompress += listOf("glb", "gltf", "usdz", "usda", "usdc")
    }
    packaging {
        jniLibs {
            pickFirsts += "lib/*/libc++_shared.so"
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    configurations.all {
        resolutionStrategy {
            exclude("androidx.compose.ui", "ui")
            exclude("androidx.compose.ui", "ui-text")
            exclude("androidx.compose.ui", "ui-graphics")
            exclude("androidx.compose.foundation", "foundation")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    debugImplementation(libs.androidx.ui.tooling)

    // spatial UI
    implementation(platform(libs.spatialBOM))
    implementation(libs.spatialcore)
    implementation(libs.spatialui.design)
    implementation(libs.spatialui.foundation)

    implementation(libs.mediapack.common)
    implementation(libs.mediapack.player)
    implementation(libs.mediapack.ui)
    implementation(libs.mediapack.playbackcore)

    compileOnly(libs.spaceos.framework.extension)


}
