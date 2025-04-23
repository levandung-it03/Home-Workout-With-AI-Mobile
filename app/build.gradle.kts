plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.restproject.mobile"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.restproject.mobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BACKEND_ENDPOINT", "\"http://10.0.2.2:9090/api\"")
        buildConfigField("String", "FASTAPI_ENDPOINT", "\"http://10.0.2.2:8000/fastapi\"")
        buildConfigField("String", "PRIVATE_USER_DIR", "\"/private/user\"")
        buildConfigField("String", "PRIVATE_ADMIN_DIR", "\"/private/admin\"")
        buildConfigField("String", "PRIVATE_AUTH_DIR", "\"/private/auth\"")
        buildConfigField("String", "PUBLIC_AUTH_DIR", "\"/public/auth\"")
        buildConfigField("String", "PUBLIC_DIR", "\"/public\"")
        buildConfigField("Integer", "EXPIRED_TKN_ERR_CODE", "11003")
        buildConfigField("String", "OAUTH2_DEFAULT_GG_PASS", "\"DEFAULT_OAUTH2_PASSWORD_FOR_GOOGLE\"")
        buildConfigField("String", "SECRET_CRYPTO_KEY", "\"2Qm5pKkOLEsHgo3/AoPBefu2CMEZ2vMwp9cpq+gSDoo=\"")
        buildConfigField("String", "OAUTH2_REDIRECT_MOBILE", "\"com.restproject.mobile://auth\"")
        buildConfigField("String", "DATA_SEPARATOR", "\"_SePaRaToR_\"")

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
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}