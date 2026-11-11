# Design

## Context

See `proposal.md` for background and motivation. The `core` module provides `Calendar`, `CalendarDate`, `GregorianCalendar`, and `PersianCalendar` for epoch/date conversions and date manipulation. However:
1. `Calendar` currently lacks day of week determination and calendar-specific first-day-of-week metadata.
2. The user's active calendar system preference is not stored or managed anywhere.
3. The main `Settings` tab in `MainComponent` currently displays a blank placeholder Box (`settings-screen`).
4. UI screens such as `AnalyticsComponent` and `PocketDetailsComponent` hardcode Gregorian assumptions (e.g. `now.month.ordinal + 1`, Gregorian `Month` enum names, and Gregorian date arithmetic).

## Goals / Non-Goals

**Goals:**
- Provide `UserSettingsRepository` (backed by Jetpack DataStore Preferences) to store and reactively stream `CalendarType` preference (defaulting to `CalendarType.GREGORIAN`).
- Implement `SettingsComponent` and `SettingsScreen` in `app/shared` providing a clean UI to switch between Gregorian and Persian calendars.
- Wire `SettingsComponent` into `MainComponent` under `MainChildConfig.Settings`.
- Extend `Calendar` in `core` with:
  - `DayOfWeek` (using standard `kotlinx.datetime.DayOfWeek` or domain enum)
  - `fun getDayOfWeek(date: CalendarDate): DayOfWeek`
  - `val firstDayOfWeek: DayOfWeek` (Monday for Gregorian, Saturday for Persian)
  - Helper to get localized/display month names for Gregorian and Persian calendars.
- Update `AnalyticsComponent` to observe user calendar preference and display month headers, navigate months, and format date groups according to the selected calendar.
- Update date formatting in pocket transactions to respect the selected calendar.

**Non-Goals:**
- Database migration of stored transaction timestamps (all timestamps remain UTC epoch milliseconds / `Instant`).
- Full internationalization of all app languages/locales in this change (focus is on the calendar system selection and its date/month/weekday presentation).
- Altering automatic monthly top-up backend job logic (persisted records and schedules remain tied to exact month period boundaries or timestamps).

## Decisions

### Decision 1: Settings Storage via DataStore Preferences
- **Choice**: Add `UserSettingsRepository` backed by `DataStore<Preferences>`, similar to `DataStoreTokenStorage`.
- **Key**: `stringPreferencesKey("calendar_system")` storing enum name `GREGORIAN` or `PERSIAN`. Default is `CalendarType.GREGORIAN`.
- **API**: Exposes `val calendarType: Flow<CalendarType>` and `suspend fun setCalendarType(type: CalendarType)`.
- **Rationale**: `DataStore` is already configured in `othersModule()` in `AppModule.kt` and works across Kotlin Multiplatform targets. Provides clean reactive flow for UI components.
- **Alternatives considered**: Storing in SQLite / Room database. Rejected because user settings are key-value preferences rather than relational entities.

### Decision 2: Day of Week & First Day of Week in Calendar Interface
- **Choice**: Add to `com.base.calendar.Calendar`:
  ```kotlin
  val firstDayOfWeek: DayOfWeek
  fun getDayOfWeek(date: CalendarDate): DayOfWeek
  ```
  `CalendarDate.dayOfWeek` can delegate to the calendar.
- **Computation**:
  - For Gregorian: compute via `LocalDate(year, month, day).dayOfWeek`.
  - For Persian: convert date to Gregorian date via `toGregorian(date)`, then get day of week.
  - `firstDayOfWeek`: `DayOfWeek.MONDAY` for `GregorianCalendar`, `DayOfWeek.SATURDAY` for `PersianCalendar`.
- **Rationale**: The day of week is an absolute civil property aligned with epoch days. Since Persian conversion to Gregorian is exact, delegating Persian day of week through Gregorian preserves perfect accuracy without duplicate astronomical arithmetic.

### Decision 3: Month Names & Formatting in Calendar System
- **Choice**: Introduce helper in `com.base.calendar`:
  ```kotlin
  fun getMonthName(month: Int, type: CalendarType): String
  ```
  - Persian months (1..12): Farvardin, Ordibehesht, Khordad, Tir, Mordad, Shahrivar, Mehr, Aban, Azar, Dey, Bahman, Esfand.
  - Gregorian months (1..12): January, February, March, April, May, June, July, August, September, October, November, December.
- **Rationale**: Centralizing calendar month names in the core calendar module ensures consistency across analytics, transaction lists, and any future date pickers.

### Decision 4: Decompose Settings Component & Screen Architecture
- **Choice**: Create feature package `com.base.feature.settings`:
  - `SettingsComponent`: interface exposing `state: Value<SettingsState>`, `fun onCalendarTypeSelected(type: CalendarType)`.
  - `DefaultSettingsComponent`: implements `SettingsComponent`, injects `UserSettingsRepository`, updates state and handles user action.
  - `FakeSettingsComponent` and `FakeSettingsStateFactory` in the same file according to `AGENTS.md`.
  - `SettingsScreen`: Composable screen with `PocketyTopAppBar`, calendar selection cards/options with checkmarks or radio style using MaterialTheme tokens.
  - Register `SettingsComponentFactory` in `DecomposeModule.kt` and integrate into `DefaultMainComponent`.

### Decision 5: Reactive Calendar Preference in AnalyticsComponent
- **Choice**: Inject `UserSettingsRepository` (or calendar flow) into `DefaultAnalyticsComponent`.
  - In `init`, combine `UserSettingsRepository.calendarType` with expense and category flows.
  - Maintain `currentCalendarDate: CalendarDate` instead of separate Gregorian `currentYear` / `currentMonth` fields.
  - Month navigation increments or decrements month using `calendar.addMonths(currentDate, +/-1)`.
  - Date groups formatted using `toCalendarDate(expense.createdAt.toEpochMilliseconds())`.

## Risks / Trade-offs

- **[Risk] State recomputation on calendar system switch**: When user switches calendar in settings and returns to Analytics, existing expense items need to be regrouped into different month intervals.
  - *Mitigation*: Combining `calendarType` in `AnalyticsComponent`'s reactive flow triggers `buildState()` automatically whenever the preference changes.
- **[Risk] Month boundary differences between Persian and Gregorian**: A Gregorian month spans across two Persian months.
  - *Mitigation*: When filtering `rawExpenses` for the selected month, compare `CalendarDate(calendar.toCalendarDate(expense.createdAt)).month == selectedMonth && year == selectedYear` using the active calendar. This cleanly isolates expenses belonging to that specific calendar month.
