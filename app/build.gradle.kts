plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.github.mrbean355.variantkontrol") version "1.0.0-SNAPSHOT"
}

android {
    namespace = "com.github.mrbean355.toggles"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.github.mrbean355.toggles"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    flavorDimensions += listOf("quality", "tier")
    productFlavors {
        create("free") {
            dimension = "tier"
        }
        create("paid") {
            dimension = "tier"
        }
        create("good") {
            dimension = "quality"
        }
        create("bad") {
            dimension = "quality"
        }
    }
    buildTypes {
        debug { }
        release { }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

variantKontrol {
    packageName = "com.github.mrbean355.toggles"
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
}
