# Proposal

## Why

Debug and release currently share an Android application ID, so installing one replaces or conflicts with the other. Developers need both installed together.

## What Changes

- Give Android debug builds the `.debug` application ID suffix.
- Keep release identity and existing namespace, packages, signing, optimization, and app labels unchanged.
- Verify packaged identities and coexistence on an available device or emulator.

## Capabilities

### New Capabilities

- `android-installation`: Android variant identity and simultaneous debug/release installation.

### Modified Capabilities

None. Existing `project-name` branding requirements remain satisfied.

## Impact

Configuration change in `app/androidApp/build.gradle.kts`; no new dependencies, APIs, UI, or data migrations. Existing debug installs with the original ID are not migrated into the new debug package.
