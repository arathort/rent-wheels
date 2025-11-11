plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // якщо є інтеграція з Firebase Auth або Firestore
}

android {
    namespace = "com.example.drivetracker.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
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
    implementation("com.arathort:core:1.0.0")
    implementation(project(":common"))

    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")

    implementation("androidx.annotation:annotation:1.8.0")
}
