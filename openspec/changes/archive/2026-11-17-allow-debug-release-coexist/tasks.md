# Tasks

## 1. Android variant identity

- [x] 1.1 Add `applicationIdSuffix = ".debug"` to the existing debug build type in `app/androidApp/build.gradle.kts`; verify the diff preserves namespace, Kotlin packages, base application ID, signing, release optimization, app names, and icons.
- [ ] 1.2 Build debug/release APKs and inspect packaged application IDs and merged manifests as specified in tests.md; record actual IDs and any authority/permission conflicts or build prerequisites preventing verification.

## 2. Integration verification

- [x] 2.1 On an available device/emulator, verify both install orders and independent launches per tests.md; record results, or explicitly document device/signing unavailability without claiming a pass.
- [x] 2.2 Run AGENTS.md compilation, tests, coverage, Detekt, and formatting gates; record command outcomes and confirm the final diff contains only the intended build configuration and OpenSpec updates.

## Workflow follow-up

- Developer returns the branch, commit, and CI evidence to Analyst; report any unverified acceptance checks explicitly.
- After implementation and passing CI, Analyst archives with spec sync, rebases onto origin/main, pushes linearly to main, cleans up the worktree/remote branch, and reassigns to CTO for sign-off.

Design.md is deliberately omitted: no cross-cutting architecture, dependency, data-model, migration complexity, or unresolved technical decision requires it.

Archive note: task 1.2 remains unverified (no local APK builds). Task 2.1 is complete only via its explicit unavailable-device reporting alternative; no installation pass is claimed. Task 2.2 passed in CI as reported by Developer.
