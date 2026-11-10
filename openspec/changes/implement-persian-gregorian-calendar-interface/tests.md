# Tests Specification

## Unit Tests (single component / class / function)

### `calendar/GregorianCalendarTest`

**Target**: `com.base.calendar.GregorianCalendar` and `com.base.calendar.CalendarDate`

**Scenarios**:

- Given a valid Gregorian date (e.g. 2026-03-21)
    - When converted to epoch milliseconds in UTC,
        - [ ] Then the epoch milliseconds match 2026-03-21T00:00:00Z exactly
    - When converted back from epoch milliseconds,
        - [ ] Then the calendar date equals 2026-03-21 with type GREGORIAN

- Given Gregorian leap year edge cases (years 2000, 2024, 2100, 2025)
    - When checking isLeapYear,
        - [ ] Then 2000 and 2024 return true, and 2100 and 2025 return false
    - When checking days in February (month 2),
        - [ ] Then year 2024 has 29 days and year 2025 has 28 days

- Given an invalid date (month 13 or day 32)
    - When constructing CalendarDate or validating,
        - [ ] Then an IllegalArgumentException is thrown

- Given a Gregorian date 2024-01-31
    - When adding 1 month,
        - [ ] Then the day clamps to 2024-02-29 (leap year February)

### `calendar/PersianCalendarTest`

**Target**: `com.base.calendar.PersianCalendar`

**Scenarios**:

- Given Solar Hijri date 1403-01-01 (Nowruz)
    - When converting to Gregorian date,
        - [ ] Then the result is Gregorian date 2024-03-20
    - When converting to epoch milliseconds in UTC,
        - [ ] Then the epoch milliseconds equal 2024-03-20T00:00:00Z

- Given Gregorian date 2024-03-20
    - When converting to Persian date,
        - [ ] Then the result is Persian date 1403-01-01

- Given Persian leap year checks (years 1399, 1403, 1404, 1408)
    - When checking isLeapYear,
        - [ ] Then 1399, 1403, and 1408 return true, and 1404 returns false
    - When checking days in month 12 (Esfand),
        - [ ] Then Esfand has 30 days in 1403 and 29 days in 1404

- Given Persian date 1403-06-31 (last day of Shahrivar)
    - When adding 1 day,
        - [ ] Then the result is 1403-07-01 (Mehr)
    - When adding 1 month,
        - [ ] Then the result clamps to 1403-07-30 (Mehr has 30 days)

- Given multiple benchmark historical and future dates (e.g. 1357-11-22 <-> 1979-02-11, 1400-01-01 <-> 2021-03-21)
    - When converted round-trip between Persian and Gregorian,
        - [ ] Then bidirectional conversions preserve date integrity exactly

## E2E Flow Tests

*(Not applicable: Infrastructure only slice in `core` module without UI flows or root navigation).*

## Repository & Data Tests

*(Not applicable: Infrastructure only slice without database entities or migrations).*

## UI & Accessibility Tests

*(Not applicable: Infrastructure only slice without UI components).*
