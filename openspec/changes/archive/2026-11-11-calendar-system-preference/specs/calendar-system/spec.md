# Spec Delta

## ADDED Requirements

### Requirement: Day of week and first day of week determination
The system SHALL determine the day of the week for any given calendar date and provide the calendar-appropriate first day of the week, with Saturday as the first day for Persian calendar and Monday as the first day for Gregorian calendar.

#### Scenario: Day of week for known Gregorian date
- **WHEN** querying the day of week for Gregorian date 2026-03-21 (Saturday)
- **THEN** the day of week is determined to be SATURDAY

#### Scenario: Day of week for known Persian date
- **WHEN** querying the day of week for Persian date 1403-01-01 (Wednesday, 2024-03-20)
- **THEN** the day of week is determined to be WEDNESDAY

#### Scenario: First day of week by calendar type
- **WHEN** querying the first day of the week for Gregorian and Persian calendars
- **THEN** the Gregorian calendar returns MONDAY and the Persian calendar returns SATURDAY

### Requirement: Calendar-aware month display naming
The system SHALL provide localized or calendar-specific month names and formatting for both Gregorian and Persian calendars.

#### Scenario: Persian month names
- **WHEN** formatting or resolving the name for Persian month 1 through 12
- **THEN** month 1 is Farvardin, month 7 is Mehr, and month 12 is Esfand

#### Scenario: Gregorian month names
- **WHEN** formatting or resolving the name for Gregorian month 1 through 12
- **THEN** month 1 is January and month 12 is December

### Requirement: Calendar-aware UI date formatting and month navigation
The system SHALL display expense group dates, month titles, and month navigation according to the user's active calendar system preference.

#### Scenario: Analytics month navigation in Persian calendar
- **WHEN** user's preferred calendar is PERSIAN and user navigates months in Analytics
- **THEN** navigation steps forward and backward by Persian solar months and the month label displays the Persian month name and Solar Hijri year

#### Scenario: Expense date grouping in Persian calendar
- **WHEN** expenses are viewed in Analytics under Persian calendar preference
- **THEN** expense dates are grouped and formatted using Persian calendar dates (e.g., Today, Yesterday, or day and Persian month name)
