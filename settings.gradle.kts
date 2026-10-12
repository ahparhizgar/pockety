rootProject.name = "Pockety"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "androidx.room3") {
        useModule("androidx.room3:room3-gradle-plugin:${requested.version}")
      }
    }
  }
}

dependencyResolutionManagement {
  repositories {
    exclusiveContent {
      forRepository { mavenCentral() }

      filter { includeGroup("com.squareup.okio") }
    }
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

include(":app:androidApp")

include(":app:desktopApp")

include(":app:shared")

include(":core")

include(":design")

include(":server")
