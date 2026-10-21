import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.ktfmt)
}

kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_11 } }

dependencies {
  implementation(projects.app.shared)

  implementation(libs.koin.core)
  implementation(libs.koin.android)
  implementation(libs.androidx.activity.compose)
  implementation(libs.decompose)

  implementation(libs.compose.uiToolingPreview)
  debugImplementation(libs.compose.uiTooling)

  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotest.framework)
  testImplementation(libs.kotest.assertion)
  testImplementation(libs.kotest.junit)
  testImplementation(libs.koin.test)
}

android {
  namespace = "ir.ahparhizgar.pockety"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "ir.ahparhizgar.pockety"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  signingConfigs {
    getByName("debug") {
      storeFile = file("debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  buildTypes {
    getByName("debug") { signingConfig = signingConfigs.getByName("debug") }
    getByName("release") { isMinifyEnabled = false }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  testOptions { unitTests.all { it.useJUnitPlatform() } }
}
