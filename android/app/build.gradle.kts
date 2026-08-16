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
        versionCode = 2
        versionName = "0.2.0"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++17", "-fexceptions", "-frtti")
                arguments += listOf("-DANDROID_STL=c++_shared")
            }
        }
    }

    buildFeatures {
        prefab = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.31.6"
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    implementation("com.google.oboe:oboe:1.10.0")
}
