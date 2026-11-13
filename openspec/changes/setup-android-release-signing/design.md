# Design

## Context

Pockety currently configures debug signing with a committed `debug.keystore` inside `app/androidApp/`. For release builds, `isMinifyEnabled` is set to false, but no release signing configuration is configured. To prepare production-ready APK artifacts, a release keystore must be created, secured, and supplied to Gradle during CI and local release builds.
See `proposal.md` for motivation.

## Goals / Non-Goals

**Goals:**
- Provide a repeatable shell script (`scripts/generate-release-key.sh`) that generates a 2048-bit (or 4096-bit) RSA release keystore and random passwords stored in a local `.env` or credential file without echoing or leaking them into terminal logs.
- Provide a script/command to set GitHub secrets directly via `gh secret set` using the generated credentials and base64-encoded keystore.
- Ensure all keystore files (`*.keystore`, `*.jks`) and secret files (e.g. `*.credentials`, `release-signing.properties`, `.env.signing`) are ignored in `.gitignore` (while preserving `debug.keystore`).
- Update `app/androidApp/build.gradle.kts` to define a `release` signing config populated from environment variables (`RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`) or Gradle properties. If credentials are not present, release build should still assemble an unsigned APK or warn cleanly rather than failing debug or test tasks.
- Create `.github/workflows/release-android.yml` triggered exclusively on `workflow_dispatch` (manual), which decodes the keystore secret, builds `:app:androidApp:assembleRelease`, and uploads the release APK as a GitHub Actions workflow artifact.

**Non-Goals:**
- Automated publishing to Google Play Store / F-Droid / internal app track (this is APK build and artifact generation only).
- Android App Bundle (`bundleRelease` / `.aab`) publishing in this phase (focus is release APK generation per requirements).
- Automating release trigger on push or tag (must be manual `workflow_dispatch` only).

## Decisions

### 1. Key Generation Strategy and Storage
- **Decision**: Provide a shell script `scripts/generate-release-key.sh` using `keytool` and `openssl rand -base64 24` to generate the password. The script writes passwords and key metadata to a local `android-release-signing.env` file (with `chmod 600`) and the keystore to `android-release.keystore`.
- **Rationale**: Keeps passwords completely contained in a file so they do not leak into CLI output, context, or shell history.
- **Alternatives Considered**:
  - Manual keytool command: Error-prone and risks entering passwords interactively or leaking in history.

### 2. Gradle Signing Configuration
- **Decision**: In `app/androidApp/build.gradle.kts`, read credentials from `System.getenv(...)` or project properties (`findProperty(...)`):
  - `KEYSTORE_PATH` / `RELEASE_KEYSTORE_PATH`
  - `KEYSTORE_PASSWORD` / `RELEASE_KEYSTORE_PASSWORD`
  - `KEY_ALIAS` / `RELEASE_KEY_ALIAS`
  - `KEY_PASSWORD` / `RELEASE_KEY_PASSWORD`
  If all four are present and the keystore file exists, configure `signingConfigs.create("release")` and assign it to `buildTypes.getByName("release").signingConfig`. Otherwise, leave unsigned.
- **Rationale**: Provides seamless integration in CI (via env vars) as well as local flexibility without breaking local builds when no release key is configured.
- **Alternatives Considered**:
  - Requiring `release-signing.properties` file: Harder in CI where environment variables are idiomatic for GitHub Secrets.

### 3. GitHub Secrets and Secret Injection in CI
- **Decision**: Secrets stored in GitHub repository:
  - `ANDROID_KEYSTORE_BASE64`: Base64-encoded release keystore file.
  - `KEYSTORE_PASSWORD`: Keystore store password.
  - `KEY_ALIAS`: Key alias name (e.g. `pockety-release`).
  - `KEY_PASSWORD`: Key alias password.
- In `.github/workflows/release-android.yml`:
  - Decode `ANDROID_KEYSTORE_BASE64` to a runner temporary file (e.g., `/tmp/android-release.keystore`).
  - Pass the file path and passwords as environment variables to the Gradle step.
  - Run `./gradlew :app:androidApp:assembleRelease --console=plain`.
  - Use `actions/upload-artifact@v4` to upload `app/androidApp/build/outputs/apk/release/*.apk`.
- **Rationale**: Keystores cannot be directly stored as plaintext GitHub secrets; base64 encoding is the industry standard for binary files in GitHub Actions.

### 4. Git Ignore Policy
- **Decision**: In `.gitignore`:
  ```gitignore
  *.keystore
  !app/androidApp/debug.keystore
  *.jks
  android-release-signing.env
  *-signing.env
  *-signing.properties
  ```
- **Rationale**: Ensures the existing `debug.keystore` remains tracked for team and CI test convenience, while all release keystores and secret files are strictly excluded.

## Risks / Trade-offs

- **[Risk]** Base64 decoding differences across OS platforms (GNU `base64 -d` vs BSD `base64 -D`).
  → **Mitigation**: GitHub Actions runners run `ubuntu-latest` where `base64 -d` or `base64 --decode` is standard GNU. Script will use standard Linux commands.
- **[Risk]** Key loss prevents future updates if the app is distributed to users.
  → **Mitigation**: The secret is backed up securely in GitHub repository secrets and developer instructions instruct storing backup in a secure vault.
- **[Risk]** Release build fails locally if someone runs `./gradlew assembleRelease` without release keys.
  → **Mitigation**: Gradle logic gracefully skips release signing if environment variables/properties are absent.
