# Proposal

## Why

With core calendar arithmetic for Persian and Gregorian calendars already implemented in the core module, users now need the ability to select their preferred calendar system in the app settings. Furthermore, date representations across the user interface (month navigation, month and day formatting, and weekly structures) need to respect this setting so Persian calendar users experience native Solar Hijri dates and day-of-week sequences.

## What Changes

- Add a user settings capability and UI screen (under the main Settings tab) allowing users to select their preferred calendar system (`GREGORIAN` or `PERSIAN`).
- Persist the selected calendar preference in the app's persistent storage (DataStore).
- Extend `Calendar` in `core` to support day of week calculation (`DayOfWeek`) and first-day-of-week properties appropriate for Gregorian (Monday) and Persian (Saturday) calendars.
- Integrate the user's preferred calendar system into UI date display and navigation, specifically in Analytics (month pagination, month names, and grouped expense dates) and transaction/pocket date formatting.

## Capabilities

### New Capabilities
- `user-settings`: Storage and management of user preferences including calendar system selection, with reactive observation for UI components.

### Modified Capabilities
- `calendar-system`: Extend calendar interface with day-of-week determination, calendar-specific first day of week, and localized/calendar-aware month naming.

## Impact

- `core`: `com.base.calendar.Calendar`, `GregorianCalendar`, `PersianCalendar`, `CalendarDate` extended with weekday calculations.
- `app/shared`: New `settings` feature module (`SettingsComponent`, `SettingsScreen`), DI bindings, and integration into `MainComponent` Settings tab.
- `app/shared`: `AnalyticsComponent` updated to use preferred calendar for month navigation and date grouping.
- `app/shared`: Pocket/transaction date formatting updated to respect preferred calendar.
