# Tests Specification

## Unit Tests (single component / class / function)

Not applicable: this changes packaging configuration, with no component logic. Do not add a source-text assertion that merely repeats the Gradle declaration. Design was deliberately omitted under the schema's conditional rule.

## E2E Flow Tests

### Android packaging verification

**Target**: Built APK application identities (variant application identities requirement).

- Given the Android build prerequisites and dependencies are available
  - When debug and release APKs are assembled (`:app:androidApp:assembleDebug :app:androidApp:assembleRelease`)
  - Then inspect each APK with `apkanalyzer manifest application-id` or `aapt dump badging`: debug is `ir.ahparhizgar.pockety.debug` and release is `ir.ahparhizgar.pockety`.
  - Then inspect merged manifests for any library-added authority or permission conflicts and confirm component names still resolve.

### Side-by-side installation verification

**Target**: Simultaneous installation requirement, both install-order scenarios.

- Given a supported device/emulator with release installed
  - When debug is installed
  - Then both package IDs appear in `adb shell pm list packages` and both apps launch independently.
- Given a supported device/emulator with debug installed and release absent
  - When a signed release APK is installed
  - Then both package IDs appear and both apps launch independently.

Use a disposable emulator for clean install-order checks. Sign a verification-only copy if release credentials are unavailable; do not change production signing configuration. Record exact commands/results. If no device is available, explicitly report coexistence checks as unexecuted rather than passed.

## Repository & Data Tests

Not applicable: no repository or schema changes. New debug identity receives separate Android app storage; existing debug data under the base ID is not migrated.

## UI & Accessibility Tests

No new UI or inputs. During available-device verification, launch each package's existing main activity. App names and icons remain unchanged.

## Regression gates

Developer runs the AGENTS.md compilation, JVM tests, coverage, Detekt, and formatting gates and records failures or missing prerequisites. These supplement APK verification; they do not prove package identity or coexistence.

## Implementation verification record

- `./gradlew ktfmtFormat` completed successfully.
- Local APK assembly and the compilation, test, coverage, and Detekt gates were not run because this task's execution instructions prohibit local builds; they require GitHub Actions CI.
- APK application IDs and merged manifests have not been inspected. The `adb` command is unavailable in this environment, so device installation and launch checks were not run.
- The implementation was pushed to `change/allow-debug-release-coexist` at commit `89d8e6a`. CI status could not be retrieved: `gh auth status` reported no authenticated GitHub CLI session, and the available GitHub API read returned 404 for the private repository's Actions endpoint.

## CI handoff and archive review (2026-11-17)

- Developer reported successful [CI run 44042309566](https://github.com/ahparhizgar/pockety/actions/runs/44042309566) at `2fe3063f9d5e18937f9037f845ad6feaea26d64f`: compilation, tests, coverage, Detekt, ktfmt, and design web build passed. This supersedes the earlier CI access limitation. Analyst could not independently re-query the run because this runtime has no gh login; evidence is the Developer handoff on POC-28.
- APK application IDs and merged manifests remain uninspected, and both device install orders and independent launch remain unexecuted. CI does not prove these acceptance checks.
- Archive/sync proceeds under the assigned post-CI workflow with task 1.2 explicitly incomplete. CTO final sign-off must consider these verification limits.
