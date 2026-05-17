plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.voxo.bmitracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.voxo.bmitracker"
        minSdk = 24
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.10.0")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.10.0")
    implementation ("com.github.anastr:speedviewlib:1.6.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
}