# Tasks

## 1. Gitignore and Local Key Generation Script

- [x] 1.1 Update `.gitignore` with rules to exclude `*.keystore` (preserving `debug.keystore`), `*.jks`, and release secret files (`*-signing.env`, `*.credentials`), and verify with `git status` that test files matching these patterns are untracked.
- [x] 1.2 Implement `scripts/generate-release-key.sh` to generate a random password, write it to a local private credentials file (`android-release-signing.env`) without printing to console, generate the Android release keystore using `keytool`, and verify execution produces the keystore and secret file.
- [x] 1.3 Implement `scripts/setup-github-secrets.sh` to read `android-release-signing.env` and the generated keystore, base64-encode the keystore, and configure repository secrets via `gh secret set` (`ANDROID_KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) without echoing passwords to stdout.

## 2. Android Build Configuration

- [x] 2.1 Update `app/androidApp/build.gradle.kts` to define a `release` signing config that binds to environment variables or properties (`RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`), defaulting to unsigned if omitted, and verify with `./gradlew :app:androidApp:assembleRelease --console=plain`.

## 3. GitHub Actions Release Workflow

- [x] 3.1 Create `.github/workflows/release-android.yml` triggered exclusively via `workflow_dispatch`, setting up Java, decoding the base64 keystore secret to a runner file, running `:app:androidApp:assembleRelease`, and uploading the release APK via `actions/upload-artifact@v4`.
- [x] 3.2 Verify workflow YAML syntax and run `./gradlew compileKotlinJvm compileKotlin detektMain ktfmtCheck --console=plain` to ensure codebase integrity and formatting standards.

## Workflow follow-up

- Archive the change after implementation and CI verification pass.
