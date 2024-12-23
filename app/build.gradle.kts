import org.gradle.api.JavaVersion
import java.io.FileInputStream
import java.util.Properties

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val apiKey = properties.getProperty("API_KEY");
val defaultWebClientId = properties.getProperty("default_web_client_id");
plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.roulette"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.roulette"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"$defaultWebClientId\"")
    }

    buildFeatures {
        buildConfig = true
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
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    implementation ("com.google.firebase:firebase-auth:22.1.1")
    implementation (libs.cardview)
    implementation (libs.recyclerview)

    implementation(libs.picasso)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.volley)
    implementation (libs.play.services.maps)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}