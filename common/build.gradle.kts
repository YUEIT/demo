plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
    namespace = "cn.yue.base"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    api(libs.androidx.core)
    api(libs.androidx.core.ktx)
    api(libs.androidx.activity)
    api(libs.androidx.fragment)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.compiler)
    api(libs.androidx.lifecycle.livedata)
    api(libs.androidx.lifecycle.viewmode)
    api(libs.androidx.lifecycle.viewmodel.savestate)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.cardview)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.coordinatorlayout)
    api(libs.androidx.gridlayout)
    api(libs.androidx.collection)
    api(libs.androidx.collection.ktx)
    api(libs.androidx.recyclerview)
    api(libs.androidx.swiperefreshlayout)
    api(libs.androidx.viewpager2)
    api(libs.androidx.work)
    api(libs.androidx.work.rxjava2)
    api(libs.rxjava)
    api(libs.rxandroid)
    api(libs.glide)
    api(libs.gson)
    api(libs.exoplayer)
    api(libs.okhttp)
    api(libs.okhttp.log)
    api(libs.retrofit)
    api(libs.retrofit.adapter.rxjava)
    api(libs.retrofit.converter.gson)
    api(libs.material)
}