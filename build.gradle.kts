import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask
import dev.detekt.gradle.extensions.DetektExtension

plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidMultiplatformLibrary) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.ktor) apply false
  alias(libs.plugins.kotest) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.androidx.room) apply false
  alias(libs.plugins.kover)
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.detekt)
}

dependencies {
  kover(project(":app:shared"))
  kover(project(":server"))
  kover(project(":core"))
}

kover {
  reports {
    total {
      xml { onCheck = true }
      html { onCheck = true }
    }
    verify { rule { bound { minValue = 80 } } }
  }
}

tasks.register<KtfmtFormatTask>("ktfmtPrecommit") {
  source = project.fileTree(rootDir)
  include("**/*.kt", "**/*.kts")
}

tasks.register("detektMain") {
  group = "verification"
  description = "Runs all detektMain, detektMainJvm, and detektMainAndroid tasks of each subproject"
  dependsOn(
      subprojects.map { subproject ->
        subproject.tasks.matching { task ->
          task.name == "detektMain" ||
              task.name == "detektMainJvm" ||
              task.name == "detektMainAndroid"
        }
      }
  )
}

subprojects {
  apply(plugin = "dev.detekt")

  dependencies { "detektPlugins"(rootProject.libs.detekt.composeRules) }

  extensions.configure<DetektExtension> {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    source.setFrom(files("src"))
  }

  tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    exclude { it.file.invariantSeparatorsPath.contains("/build/generated/") }
  }
}
