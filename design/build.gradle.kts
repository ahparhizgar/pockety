import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.roborazzi)
}

roborazzi { outputDir.set(project.file("screenshots")) }

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
    androidMain.dependencies { implementation(libs.androidx.activity.compose) }
    jvmTest.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.junit)
      implementation(libs.junit.vintage.engine)
      implementation(libs.roborazzi.core)
      implementation(libs.roborazzi.compose.desktop)
    }
  }
}

dependencies { androidRuntimeClasspath(libs.compose.uiTooling) }

compose.desktop { application { mainClass = "com.base.design.MainKt" } }
