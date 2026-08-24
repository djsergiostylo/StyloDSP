plugins {
    id("com.android.application")
    kotlin("android")
}

android { namespace = "com.stylo.dsp"; compileSdk = 35
    defaultConfig { applicationId = "com.stylo.dsp"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.2.2" }
}

kotlin { jvmToolchain(17) }
