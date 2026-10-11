# Proposal

## Why

We want to build a new KMP app called Pockety (جیبک) that helps users track their daily personal expenses by automatically reading bank SMSs. The current project is a generic base project, and it needs to be tailored for this specific use case, including trimming down unused targets (web, iOS) to speed up initial Android-first development while keeping the backend intact.

## What Changes

- Rename the project from the generic base project to Pockety (persian name: جیبک).
- Remove the Web target completely from the KMP setup.
- Comment out iOS sections in build scripts to temporarily disable it for faster Android development, while leaving iOS source code intact.
- Retain the backend setup as is.

## Capabilities

### New Capabilities
- `project-name`: project name changes.

### Modified Capabilities
None.

## Impact

- **Build configuration:** KMP build scripts will be modified to remove `webApp` and comment out iOS targets.
- **Project naming:** Project name configurations in `settings.gradle.kts` and manifest/string resources will be updated to "Pockety".
- **Source code:** Introduction of new data models and components for `expense-tracking` and `bank-sms-parser`.
