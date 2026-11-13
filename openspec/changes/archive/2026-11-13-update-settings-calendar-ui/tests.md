# Tests Specification

## Unit Tests (single component / class / function)

### `settings/SettingsTest`

**Target**: `SettingsComponent` / `UserSettingsRepository` integration

**Scenarios**:

- Given SettingsComponent initialized with default preferences
    - When state is observed,
        - [x] Then `selectedCalendarType` is GREGORIAN
    - When user selects Persian calendar,
        - [x] Then `selectedCalendarType` updates to PERSIAN and is persisted in `UserSettingsRepository`

## E2E Flow Tests

### `settings/SettingsFlowE2eTest`

**Flow**: Navigation from RootComponent to Settings tab and verifying downstream calendar effects

**Scenarios**:

- Given app initialized at MainComponent
    - When user selects Settings tab in bottom bar
        - [x] Then active child in MainComponent is SettingsComponent
        - And user selects Persian calendar option
            - [x] Then SettingsComponent state reflects PERSIAN
            - And user selects Analytics tab
                - [x] Then AnalyticsComponent displays active month in Persian calendar

## UI & Accessibility Tests

### `settings/SettingsE2eUiTest`

**Scenarios**:

1. Display calendar system options:
   - Given `SettingsScreen` is rendered with `FakeSettingsComponent` (default GREGORIAN)
   - When screen loads
   - Then `settings-screen`, `calendar-option-gregorian`, and `calendar-option-persian` test tags exist
   - And the Gregorian card is visually indicated as selected

2. Interactive selection and state change:
   - Given `SettingsScreen` is displayed
   - When user clicks `calendar-option-persian`
   - Then component receives selection event and `selectedCalendarType` becomes PERSIAN
   - And the Persian card displays selected state
