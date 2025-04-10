plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finalandroidproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finalandroidproject"
        minSdk = 28
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.material)
    implementation(libs.activity)
    implementation ("com.google.android.material:material:1.6.1")
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}