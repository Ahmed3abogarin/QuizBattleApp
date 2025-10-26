plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

    // safe args for passing the data between the fragments
    id("androidx.navigation.safeargs.kotlin")

    // Kapt
    id("kotlin-kapt")

    // hilt
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.vtol.quizbattleapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vtol.quizbattleapp"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Circular image
    implementation(libs.hdodenhof.circleimageview)

    // Firebase firestore
    implementation(libs.firebase.firestore)

    // Firebase Real database
    implementation(libs.firebase.database)

    // Firebase authentication
    implementation(libs.firebase.auth)

    // Dagger Hilt for dependency injection (to ensure singleton- one instance during the app lifecycle)
    // Dagger
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

}