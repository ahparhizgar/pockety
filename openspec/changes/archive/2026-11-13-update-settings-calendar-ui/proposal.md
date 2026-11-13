# Proposal

## Why

The current Settings screen in `:app:shared` uses simple `RadioButton` items inside a generic `Card` for calendar system selection. In commit `f098b35`, the design system introduced a richer, higher-quality card component (`DisplayCalendarSystemCard` and `CalendarOptionCard`) featuring badges ("G" / "P"), subtitles, elevation states, and visual selection indicators. The production application in `:app:shared` needs to be updated to match the design specification while keeping existing domain integration, persistent settings, and accessibility semantics intact.

## What Changes

- Create reusable Settings UI components in `:app:shared` under `com.base.feature.settings`:
  - `CalendarOptionCard`: Card rendering symbol badge ("G" or "P"), title, subtitle, and selection icon.
  - `DisplayCalendarSystemCard`: Container card rendering section header icon/title and side-by-side `CalendarOptionCard`s for Gregorian and Persian systems.
- Update `SettingsScreen.kt` in `:app:shared` to replace the basic RadioButton layout with `DisplayCalendarSystemCard`.
- Map domain `CalendarType` (`GREGORIAN`, `PERSIAN`) to the UI options with localized title and subtitle resources.
- Preserve test tags (`calendar-option-gregorian` and `calendar-option-persian`) and keyboard / click accessibility semantics.
- Add component and screen previews (`@ComponentPreviews`, `@ScreenPreviews`) conforming to project conventions.
- Update UI tests (`SettingsE2eUiTest`) and ensure flow tests (`SettingsFlowE2eTest`) pass cleanly.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `user-settings`: Update the Settings UI requirement scenarios to specify the rich calendar option card presentation with badges and selection indicators while retaining selection and persistence behavior.

## Impact

- **UI / Features**: `SettingsScreen.kt` and newly introduced component composables in `com.base.feature.settings` in `:app:shared`.
- **String Resources**: Add subtitles and symbol string resources for Gregorian and Persian calendar options if needed.
- **Tests**: `SettingsE2eUiTest.kt` verifies updated card structure and test tags.
- **Dependencies & APIs**: No external dependency or domain model changes; continues using existing `UserSettingsRepository` and `CalendarType`.
