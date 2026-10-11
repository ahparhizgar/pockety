# Tasks

## 1. Project Renaming

- [x] 1.1 Rename the project in `settings.gradle.kts` from the base name to `Pockety` and verify Gradle sync succeeds.
- [x] 1.2 Update string resources in `app/androidApp/src/main/res/values/strings.xml` to `جیبک` if `app_name` exists, and verify string resolution.

## 2. Trimming Targets

- [x] 2.1 Remove the `app/webApp` folder and references in `settings.gradle.kts` and `build.gradle.kts` files, verify Gradle sync.
- [x] 2.2 Comment out iOS targets in `app/shared/build.gradle.kts` and other multiplatform setup blocks to disable iOS building. Verify that `./gradlew assembleDebug` for Android works without running iOS tasks.

## Workflow follow-up

- Archive the change after the setup is completed and verified.
