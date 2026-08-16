plugins {
    id("com.android.application")
}

android {
    namespace = "com.stylo.dsp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.stylo.dsp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        prefab = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation("com.google.oboe:oboe:1.10.0")
}
