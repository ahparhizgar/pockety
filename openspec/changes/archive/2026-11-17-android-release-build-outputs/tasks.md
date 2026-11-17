# Tasks

## 1. Produce Both APK Variants

- [x] 1.1 Update `.github/workflows/release-android.yml` to build `assembleDebug` and `assembleRelease`, keeping release credentials scoped to the release build; verify both Gradle tasks appear in the workflow and execute successfully in Actions.
- [x] 1.2 Upload the debug APK as `pockety-debug-apk` and retain `pockety-release-apk` as a distinct artifact; verify both artifacts contain their matching APK and missing outputs fail the upload step.
- [x] 1.3 Verify temporary release keystore cleanup still runs after successful and failed release build paths.
