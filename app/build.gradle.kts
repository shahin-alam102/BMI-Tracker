plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.voxo.bmitracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.voxo.bmitracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
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

    testOptions {
        unitTests.isReturnDefaultValues = true
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
    implementation (libs.lifecycle.viewmodel)
    implementation (libs.lifecycle.livedata)
    implementation (libs.speedviewlib)
    implementation(libs.gson)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation (libs.play.services.ads)
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("com.google.truth:truth:1.4.2")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")

}
