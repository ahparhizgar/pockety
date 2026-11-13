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
  val releaseKeystorePath =
      providers.environmentVariable("RELEASE_KEYSTORE_PATH").orNull
          ?: providers.environmentVariable("KEYSTORE_PATH").orNull
          ?: providers.gradleProperty("RELEASE_KEYSTORE_PATH").orNull
          ?: providers.gradleProperty("KEYSTORE_PATH").orNull
  val releaseKeystorePassword =
      providers.environmentVariable("RELEASE_KEYSTORE_PASSWORD").orNull
          ?: providers.environmentVariable("KEYSTORE_PASSWORD").orNull
          ?: providers.gradleProperty("RELEASE_KEYSTORE_PASSWORD").orNull
          ?: providers.gradleProperty("KEYSTORE_PASSWORD").orNull
  val releaseKeyAlias =
      providers.environmentVariable("RELEASE_KEY_ALIAS").orNull
          ?: providers.environmentVariable("KEY_ALIAS").orNull
          ?: providers.gradleProperty("RELEASE_KEY_ALIAS").orNull
          ?: providers.gradleProperty("KEY_ALIAS").orNull
  val releaseKeyPassword =
      providers.environmentVariable("RELEASE_KEY_PASSWORD").orNull
          ?: providers.environmentVariable("KEY_PASSWORD").orNull
          ?: providers.gradleProperty("RELEASE_KEY_PASSWORD").orNull
          ?: providers.gradleProperty("KEY_PASSWORD").orNull

  val releaseKeystoreFile = releaseKeystorePath?.let {
    rootProject.file(it).takeIf { f -> f.exists() } ?: file(it).takeIf { f -> f.exists() }
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
    if (
        releaseKeystoreFile != null &&
            releaseKeystorePassword != null &&
            releaseKeyAlias != null &&
            releaseKeyPassword != null
    ) {
      create("release") {
        storeFile = releaseKeystoreFile
        storePassword = releaseKeystorePassword
        keyAlias = releaseKeyAlias
        keyPassword = releaseKeyPassword
      }
    }
  }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  buildTypes {
    getByName("debug") { signingConfig = signingConfigs.getByName("debug") }
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      signingConfigs.findByName("release")?.let { releaseSigning -> signingConfig = releaseSigning }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  testOptions { unitTests.all { it.useJUnitPlatform() } }
}
