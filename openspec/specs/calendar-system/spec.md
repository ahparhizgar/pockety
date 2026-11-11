# calendar-system Specification

## Purpose
Provides calendar representations and arithmetic for Gregorian and Persian (Solar Hijri) systems, supporting date conversions, epoch instant conversions, and calendar calculations.

## Requirements

### Requirement: Calendar date model and calendar type
The system SHALL provide a calendar date representation containing year, month, and day components, along with an indicator of whether the date is Gregorian or Persian.

#### Scenario: Instantiate valid calendar date
- **WHEN** a date is created with year 1403, month 1, day 15 and calendar type Persian
- **THEN** the date object holds year 1403, month 1, day 15 and identifies as Persian

#### Scenario: Reject invalid month or day
- **WHEN** a date is constructed with an invalid month or day exceeding month limits for the specified calendar year
- **THEN** the system rejects the construction with an `IllegalArgumentException`

### Requirement: Gregorian calendar conversion and arithmetic
The system SHALL provide a Gregorian calendar implementation capable of converting between calendar dates and epoch timestamps, determining leap years, and calculating days in a month.

#### Scenario: Convert epoch milliseconds to Gregorian calendar date
- **WHEN** an epoch millisecond timestamp corresponding to 2026-03-21T00:00:00Z is converted using the Gregorian calendar at UTC
- **THEN** the returned calendar date has year 2026, month 3, and day 21 with Gregorian calendar type

#### Scenario: Convert Gregorian calendar date to epoch milliseconds
- **WHEN** a Gregorian date of 2026-03-21 is converted to epoch milliseconds at start of day in UTC
- **THEN** the resulting epoch millisecond corresponds exactly to 2026-03-21T00:00:00Z

#### Scenario: Gregorian leap year calculation
- **WHEN** checking leap year status for year 2024 and year 2025 in Gregorian calendar
- **THEN** year 2024 is identified as a leap year and year 2025 is not

### Requirement: Persian (Solar Hijri) calendar conversion and arithmetic
The system SHALL provide a Persian calendar implementation capable of converting between Solar Hijri dates and epoch timestamps, converting between Gregorian and Persian dates, determining Persian leap years, and calculating days in a Persian month.

#### Scenario: Convert known Gregorian date to Persian date
- **WHEN** a Gregorian date of 2024-03-20 is converted to Persian calendar
- **THEN** the converted date is year 1403, month 1 (Farvardin), and day 1 (Nowruz)

#### Scenario: Convert known Persian date to Gregorian date
- **WHEN** a Persian date of 1403-01-01 is converted to Gregorian calendar
- **THEN** the converted date is year 2024, month 3, and day 20

#### Scenario: Convert epoch milliseconds to Persian date
- **WHEN** an epoch millisecond timestamp representing 2024-03-20T00:00:00Z is converted using Persian calendar in UTC
- **THEN** the returned Persian date has year 1403, month 1, day 1

#### Scenario: Persian month length and leap year
- **WHEN** querying days in month for month 12 (Esfand) in Persian leap year 1403 vs non-leap year 1402
- **THEN** month 12 has 30 days in year 1403 and 29 days in year 1402

### Requirement: Calendar arithmetic and navigation
The system SHALL provide calendar arithmetic operations to add or subtract days, months, and years for both Gregorian and Persian dates while maintaining valid month boundaries.

#### Scenario: Add days across Persian month boundary
- **WHEN** 5 days are added to Persian date 1403-06-31 (last day of 31-day month Shahrivar)
- **THEN** the resulting Persian date is 1403-07-05 (Mehr)

#### Scenario: Add month with day clamping
- **WHEN** 1 month is added to Persian date 1403-06-31 to month 7 (which has 30 days)
- **THEN** the resulting Persian date clamps to 1403-07-30

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
