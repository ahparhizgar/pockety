# Design

## Context

The current `Release Android` workflow is triggered by `workflow_dispatch` and has one `release-android` job. It decodes the release keystore, runs `:app:androidApp:assembleRelease`, removes the temporary keystore, then uploads `app/androidApp/build/outputs/apk/release/*.apk` as `pockety-release-apk`.

## Goals / Non-Goals

**Goals:**

- Produce both debug and release APKs from one manual workflow run.
- Keep the two downloadable artifacts distinct and identifiable.
- Preserve current release signing and keystore cleanup behavior.

**Non-Goals:**

- Change workflow triggers, build tools, app versioning, or product code.
- Publish an Android App Bundle or deploy to an app store.

## Decisions

- Run the `assembleDebug` and `assembleRelease` Gradle tasks in the existing job. This keeps both APK variants tied to the same checked-out commit and Java/Gradle setup. Separate jobs were considered, but they duplicate setup without a need for independent scheduling.
- Upload each output directory separately using stable names: `pockety-debug-apk` and `pockety-release-apk`. A single combined artifact was considered, but separate artifacts make variant selection explicit when downloading.
- Let Gradle use its standard debug signing configuration. The debug APK is for testing and does not need the release keystore. Keep release credentials scoped to the release task and continue to remove the temporary keystore in an `always()` cleanup step.

## Risks / Trade-offs

- [Debug APK could be mistaken for a distributable release] → Give it a distinct `pockety-debug-apk` artifact name and retain the `debug` variant in the file name.
- [Missing APK could silently produce an incomplete run] → Configure both upload steps with `if-no-files-found: error`.
