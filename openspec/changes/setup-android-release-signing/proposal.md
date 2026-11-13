# Proposal

## Why

To prepare Pockety for production Android releases, we need a secure and reproducible mechanism to generate signing keys, sign release APKs, and automate APK builds via GitHub Actions on demand without checking secrets or keystores into source control or leaking credentials into conversational context.

## What Changes

- Add a dedicated local script to generate an Android release keystore along with secure, random passwords saved to an unversioned file.
- Update `.gitignore` to prevent any keystores (`*.keystore`, `*.jks`) and generated credential files from being tracked in git.
- Update `app/androidApp/build.gradle.kts` release build type configuration to support signing release APKs using signing config values supplied via environment variables or properties, falling back gracefully when absent.
- Create a manual GitHub Actions release workflow (`.github/workflows/release-android.yml`) triggered via `workflow_dispatch` that builds the release APK and uploads it as an artifact.
- Document and provide steps/scripting to upload the release keystore (base64-encoded) and key passwords to GitHub repository secrets using the GitHub CLI (`gh`).

## Capabilities

### New Capabilities
None. This change involves developer tooling, CI build infrastructure, and build configuration.

### Modified Capabilities
None.

## Impact

- **Build Configuration**: `app/androidApp/build.gradle.kts` release build configuration is enhanced to bind release signing keys when environment variables or project properties are supplied.
- **Git Ignore**: `.gitignore` is updated with rules for Android keystore files and release credential files.
- **CI / Workflows**: Adds `.github/workflows/release-android.yml` with `workflow_dispatch` trigger.
- **Secrets Management**: Configures GitHub repository secrets (`ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) for automated CI builds.
