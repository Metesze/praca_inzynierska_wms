plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Wtyczka Google Services
}

android {
    namespace = "com.example.magazynapp"
    compileSdk = 34 // Ustawienie wersji SDK

    defaultConfig {
        applicationId = "com.example.magazynapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0" // Dopasowane do wersji Compose
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")

    // JUnit 5 do testów jednostkowych
    
    // Mockito do tworzenia obiektów testowych (Mock)
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation ("org.robolectric:robolectric:4.8.1") // Check for the latest version

    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0") // JUnit 5
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.0") // JUnit 5

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-database:20.1.0")
    implementation("com.google.firebase:firebase-auth:21.0.0")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Dodanie MPAndroidChart

    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
}
