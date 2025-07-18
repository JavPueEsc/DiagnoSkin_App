plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "es.studium.diagnoskin_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.studium.diagnoskin_app"
        minSdk = 30
        targetSdk = 35
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.litert.support.api)
    implementation(libs.litert)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //recyclerView
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    //Consultas
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //Biometria
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    implementation ("androidx.biometric:biometric:1.2.0-alpha04")
    //Camara
    val cameraxVersion = "1.1.0-beta01"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    //Tensorflow Lite
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.12.0")
    //Corrutinas para tareas asíncronas
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //Dependencias googleMaps
    implementation ("com.google.android.gms:play-services-maps:18.1.0")

    //Test unitarios con Mockito
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}

