plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "at.fhj.peakflowmate"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "at.fhj.peakflowmate"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:${room_version}")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    val cameraVersion = "1.3.4"
    implementation("androidx.camera:camera-core:${cameraVersion}")
    implementation("androidx.camera:camera-camera2:${cameraVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraVersion}")
    implementation("androidx.camera:camera-view:${cameraVersion}")

    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.rmtheis:tess-two:9.1.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.work:work-runtime:2.9.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}