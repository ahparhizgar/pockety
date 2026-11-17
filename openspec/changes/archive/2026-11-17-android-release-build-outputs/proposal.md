# Proposal

## Why

The manually triggered Android release workflow currently publishes only a release APK. Providing both debug and release APKs from the same run makes the requested variants available for testing and distribution from GitHub Actions.

## What Changes

- Build both the Android debug and release APK variants in the manually triggered release workflow.
- Publish the debug and release APKs as separate, clearly named workflow artifacts.
- Retain the existing release keystore handling for the release build and use Gradle's standard debug signing for the debug build.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None. This is a CI workflow/tooling change and does not change product behavior.

## Impact

- `.github/workflows/release-android.yml`
- GitHub Actions artifact names and uploaded APK files
- Android Gradle tasks `:app:androidApp:assembleDebug` and `:app:androidApp:assembleRelease`
