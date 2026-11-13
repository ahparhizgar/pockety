# Tests Specification

## Unit Tests (single component / class / function)

### `settings/SettingsTest`

**Target**: `SettingsComponent` / `UserSettingsRepository` integration

**Scenarios**:

- Given SettingsComponent initialized with default preferences
    - When state is observed,
        - [ ] Then `selectedCalendarType` is GREGORIAN, `selectedCurrency` is TOMANS, and `endOfDayReminderEnabled` is true
    - When user selects Rials currency,
        - [ ] Then `selectedCurrency` updates to RIALS and is persisted in `UserSettingsRepository`
    - When user toggles end-of-day reminder off,
        - [ ] Then `endOfDayReminderEnabled` updates to false and is persisted in `UserSettingsRepository`

### `settings/UserSettingsRepositoryTest`

**Target**: `DataStoreUserSettingsRepository`

**Scenarios**:

- Given repository initialized with empty DataStore
    - When preferences are queried,
        - [ ] Then calendar defaults to GREGORIAN, currency defaults to TOMANS, and reminder defaults to true
    - When currency is updated to RIALS,
        - [ ] Then emitted currency flow updates to RIALS and survives across repository instances
    - When reminder is toggled to false,
        - [ ] Then emitted reminder flow updates to false and persists

## E2E Flow Tests

### `settings/SettingsFlowE2eTest`

**Flow**: Navigation from RootComponent to Settings tab and interaction with preferences

**Scenarios**:

- Given app initialized at MainComponent
    - When user selects Settings tab in bottom bar
        - [ ] Then active child in MainComponent is SettingsComponent
        - And user selects Rials currency option
            - [ ] Then SettingsComponent state reflects RIALS
        - And user toggles reminder switch off
            - [ ] Then SettingsComponent state reflects reminder disabled
        - And user selects Categories navigation row
            - [ ] Then navigation event for category management is invoked

## UI & Accessibility Tests

### `settings/SettingsE2eUiTest`

**Scenarios**:

1. Display full Settings screen hierarchy:
   - Given `SettingsScreen` is rendered with `FakeSettingsComponent`
   - When screen loads
   - Then test tags exist: `settings-screen`, `settings-navigation-categories`, `settings-navigation-labels`, `settings-row-language`, `currency-option-tomans`, `currency-option-rials`, `calendar-option-gregorian`, `calendar-option-persian`, `settings-switch-reminder`
   - And the Tomans currency card and Gregorian calendar card show selected state
   - And the reminder switch displays checked state

2. Interactive currency selection and keyboard accessibility:
   - Given `SettingsScreen` is displayed
   - When user clicks or presses Enter on `currency-option-rials`
   - Then component receives selection event and `selectedCurrency` updates to RIALS

3. Interactive reminder toggling:
   - Given `SettingsScreen` is displayed
   - When user clicks `settings-switch-reminder`
   - Then component receives toggle event and `endOfDayReminderEnabled` becomes false
