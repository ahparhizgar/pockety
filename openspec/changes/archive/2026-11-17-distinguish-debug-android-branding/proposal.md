# Proposal

## Why

Debug and release installations can coexist but look identical in the launcher. A debug label and bug badge let users choose the intended installation reliably.

## What Changes

- Prefix Android debug launcher labels with `Debug`, preserving English and Persian base names.
- Badge existing Pockety launcher artwork with a visible bug across legacy, round, adaptive and themed icons.
- Preserve release branding, application IDs, signing and other platforms.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `project-name`: Define the localized Android debug naming exception.
- `android-installation`: Distinguish debug launcher artwork while preserving production artwork.

## Impact

Android debug resource overlays under `app/androidApp/src/debug/res`, reproducible icon source assets and packaging verification. Existing icon-resource tooling is available. No runtime logic, dependencies, database, desktop or web changes are needed.
