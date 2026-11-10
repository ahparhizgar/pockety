# Tests Specification

## Unit Tests (single component / class / function)

### `calendar/CalendarTest`

**Target**: `com.base.calendar.Calendar`, `GregorianCalendar`, and `PersianCalendar`

**Scenarios**:

- Given Gregorian calendar
    - When checking first day of week,
        - [ ] Then firstDayOfWeek is MONDAY
    - When checking day of week for Gregorian date 2026-03-21,
        - [ ] Then day of week is SATURDAY
    - When getting month name for month 1 and month 12,
        - [ ] Then month 1 is "January" and month 12 is "December"

- Given Persian calendar
    - When checking first day of week,
        - [ ] Then firstDayOfWeek is SATURDAY
    - When checking day of week for Persian date 1403-01-01,
        - [ ] Then day of week is WEDNESDAY
    - When getting month name for Persian month 1 and month 12,
        - [ ] Then month 1 is "Farvardin" and month 12 is "Esfand"

### `settings/SettingsTest`

**Target**: `com.base.feature.settings.SettingsComponent`

**Scenarios**:

- Given SettingsComponent initialized with default preferences
    - When state is observed,
        - [ ] Then selectedCalendarType is GREGORIAN
    - When user selects Persian calendar,
        - [ ] Then selectedCalendarType updates to PERSIAN and is persisted in UserSettingsRepository

### `analytics/AnalyticsCalendarTest`

**Target**: `com.base.feature.analytics.AnalyticsComponent`

**Scenarios**:

- Given AnalyticsComponent with user preference set to PERSIAN
    - When component loads expenses,
        - [ ] Then month label displays Persian month name and year (e.g. Farvardin 1403)
        - [ ] And date groups format dates using Persian calendar (e.g., 1 Farvardin 1403)
    - When user clicks previous month,
        - [ ] Then selected month navigates to the previous Persian solar month (Esfand of previous Persian year)

## E2E Flow Tests

### `settings/SettingsFlowE2eTest`

**Flow**: Navigation from RootComponent to Settings tab and switching calendar preference

**Scenarios**:

- Given app initialized at MainComponent
    - When user selects Settings tab in bottom bar,
        - [ ] Then active child in MainComponent is SettingsComponent
    - And user selects Persian calendar option,
        - [ ] Then SettingsComponent state reflects PERSIAN
    - And user selects Analytics tab,
        - [ ] Then AnalyticsComponent displays active month in Persian calendar

## Repository & Data Tests

### `settings/UserSettingsRepositoryTest`

- Given UserSettingsRepository backed by DataStore Preferences
    - When reading initial calendarType,
        - [ ] Then default value is CalendarType.GREGORIAN
    - When saving calendarType as PERSIAN,
        - [ ] Then subsequent reads and flow emissions yield CalendarType.PERSIAN

## UI & Accessibility Tests

### `settings/SettingsE2eUiTest`

**Scenarios**:

1. User navigates to Settings tab via keyboard tab navigation or click on Settings tab.
2. Settings screen displays calendar system preference section with options for Gregorian and Persian calendars.
3. User navigates via keyboard arrows/tab to Persian calendar option and selects it; option indicates selected state.
