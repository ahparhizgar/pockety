# Proposal

## Why
Display Currency option cards show a rectangular ripple beyond their rounded corners (POC-38, reported in POC-36). Feedback should follow the visible card boundary without changing preference behavior.

## What Changes
- Confine ripple and pressed feedback to the existing 16.dp rounded option shape for currency and calendar cards.
- Preserve colors, selected styling, size, shadows, radio semantics, focus, keyboard activation, test tags, and preference persistence.
- Verify corner presses visually and selection through keyboard regression tests.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `user-settings`: Require rounded interaction feedback and accessible selection for settings option cards.

## Impact
Shared `OptionSelectionCard` in `SettingsComponents.kt`, its existing previews, and settings UI tests. No backend, dependency, storage, navigation, or design module changes. Debug branding (POC-37) remains separate.
