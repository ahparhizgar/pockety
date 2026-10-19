import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.ktfmt)
}

kotlin {
  jvm()

  android {
    namespace = "com.base.design"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilerOptions { jvmTarget = JvmTarget.JVM_11 }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.material.icons.extended)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
    }
    jvmMain.dependencies { implementation(compose.desktop.currentOs) }
  }
}

dependencies { androidRuntimeClasspath(libs.compose.uiTooling) }

compose.desktop { application { mainClass = "com.base.design.MainKt" } }
