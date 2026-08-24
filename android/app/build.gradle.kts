plugins {
    id("com.android.application")
    kotlin("android")
}

android { namespace = "com.stylo.dsp"; compileSdk = 35
    defaultConfig { applicationId = "com.stylo.dsp"; minSdk = 26; targetSdk = 35; versionCode = 3; versionName = "0.3.0" }
}

kotlin { jvmToolchain(17) }
