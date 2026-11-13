# Tests Specification

## Verification Plan & Build Tests

This change covers build infrastructure, tooling scripts, and CI workflows. Because there are no client-side UI or domain capability changes, verification is performed at the build, script, and workflow levels.

### Build Configuration Verification

**Target**: `app/androidApp/build.gradle.kts` release signing config

**Scenarios**:

- Given a build environment without release signing environment variables:
    - When running `./gradlew :app:androidApp:assembleRelease`
        - [ ] Then the build succeeds and generates an unsigned release APK or falls back cleanly without breaking.
- Given a build environment with release signing environment variables (`RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`):
    - When running `./gradlew :app:androidApp:assembleRelease`
        - [ ] Then the build succeeds and generates a signed release APK (`app-release.apk`) verified via `apksigner verify`.

### Key Generation Script Verification

**Target**: `scripts/generate-release-key.sh`

**Scenarios**:

- Given keytool and openssl installed in the local environment:
    - When running the generation script
        - [ ] Then a release keystore file is created.
        - [ ] Then the generated passwords and credentials are saved to a file with restricted permissions (`600`) and NOT printed to stdout/stderr.
        - [ ] Then `git status` verifies the keystore and secret file are ignored according to `.gitignore`.

### GitHub Actions Workflow Verification

**Target**: `.github/workflows/release-android.yml`

**Scenarios**:

- Given the workflow configuration:
    - When inspected
        - [ ] Then the workflow trigger is strictly `workflow_dispatch`.
        - [ ] Then secrets `ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` are passed as environment variables.
        - [ ] Then the release APK artifact is uploaded via `actions/upload-artifact@v4`.

### Repository Checks Gate

**Target**: Project verification gates

**Scenarios**:

- Given changes to build scripts and git configuration:
    - When running compilation, detekt, and ktfmt checks:
        - [ ] `./gradlew compileKotlinJvm compileKotlin --continue --console=plain` passes.
        - [ ] `./gradlew :detektMain --console=plain` passes.
        - [ ] `./gradlew ktfmtCheck --console=plain` passes.
