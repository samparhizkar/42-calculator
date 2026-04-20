plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sepidsa.fortytwocalculator"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sepidsa.fortytwocalculator"
        minSdk = 21
        targetSdk = 36
        versionCode = 23
        versionName = "2.11"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.txt")
        }
    }


    compileOptions {
        encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(files("libs/library-2.4.1.aar"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.loader)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.androidx.viewpager)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.material)
}
