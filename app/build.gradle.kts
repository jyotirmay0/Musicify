import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.musicify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.musicify"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // Load CLIENT_ID from local.properties safely
        val properties = Properties()
        val localFile = project.rootProject.file("local.properties")
        if (localFile.exists()) {
            properties.load(localFile.inputStream())
        }
        val ClientId = properties.getProperty("CLIENT_ID") ?: ""
        buildConfigField("String", "CLIENT_ID", "\"$ClientId\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Kotlin ---
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.20"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.6.2"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

    // --- AndroidX Core ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // --- Compose BOM ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // --- Navigation Compose ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")

    // --- Lifecycle ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- Retrofit ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // --- OkHttp ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Coil for Image Loading ---
    implementation("io.coil-kt:coil-compose:2.5.0")

    // --- Media3 ExoPlayer ---
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-session:1.2.1")

    // --- Hilt (Dependency Injection) ---
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // --- Google Play Services ---
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-base:18.3.0")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
}