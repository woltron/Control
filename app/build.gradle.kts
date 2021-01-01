plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version ru.falseteam.config.Configuration.Versions.kotlin

}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "ru.falseteam.control"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled(false)
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
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-alpha09"
        kotlinCompilerVersion = "1.4.21"
    }
}

dependencies {
    implementation(project(":api"))
    implementation("ru.falseteam.rsub:rsub-ktor-websocket-connector-client")

    with(ru.falseteam.config.Configuration.Dependencies) {
        implementation(kodein)
        implementation(ktorClient)
    }

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.compose.ui:ui:1.0.0-alpha09")
    implementation("androidx.compose.material:material:1.0.0-alpha09")
    implementation("androidx.compose.ui:ui-tooling:1.0.0-alpha09")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha06")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}