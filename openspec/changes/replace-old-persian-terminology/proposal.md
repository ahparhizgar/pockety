# Proposal

## Why
Persian settings and privacy policy still contain the old brand and pocket term. Consistent terminology must show «جیبک» and «جیب» throughout current Persian user-facing copy.

## What Changes
- Correct the three remaining resource values: privacy policy hero brand, personal-data pocket plural, and settings version brand.
- Audit current Persian UI sources for old brand and pocket forms, replacing the full brand before the pocket term.
- Preserve resource keys, placeholders, Persian grammar and half-spaces, English text, and stored user content.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `project-name`: Specify consistent Persian product terminology, including settings version formatting and privacy policy wording.

## Impact
Latest origin/main (e09240a) has three occurrences, all in `app/shared/src/commonMain/composeResources/values-fa/strings.xml`: `privacy_policy_hero_subtitle`, `privacy_policy_pillar_personal_data_desc`, and `settings_version_format`. Existing screens consume these resources directly; settings supplies `state.appVersion`. Android Persian launcher already reads «جیبک». No API, dependency, migration, identifier, navigation, or layout change is needed. Historical OpenSpec documents, POC-37 debug branding, and POC-38 ripple work are outside scope.
