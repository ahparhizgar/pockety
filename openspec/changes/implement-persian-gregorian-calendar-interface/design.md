# Design

## Context

See `proposal.md` for motivation and background.
The `core` module targets Kotlin Multiplatform (JVM and Android). Currently, the codebase uses `kotlinx-datetime` for instant and date-time operations in UI and domain features, but lacks a multi-calendar abstraction for Persian (Solar Hijri / Jalali) calendars.
The issue description specifies: "infrastructure task only. we want a calander interface that has 2 implementations: gregorian and persian calandar. you can use https://github.com/samanzamani/PersianDate this repo for the Persian part."

## Goals / Non-Goals

**Goals:**
- Provide a clean, idiomatic Kotlin interface `Calendar` (or `CalendarService` / `CalendarProvider`) in `core` that abstracts date operations across calendar systems.
- Define model classes: `CalendarType` (`GREGORIAN`, `PERSIAN`), `CalendarDate` (`year`, `month`, `day`, `calendarType`), and optional time/instant helpers.
- Implement `GregorianCalendar` and `PersianCalendar` conforming to the interface.
- Provide conversion between `CalendarDate` and `kotlinx.datetime.Instant` (or epoch milliseconds) and bidirectional conversion between Gregorian and Persian dates.
- Use pure Kotlin algorithms (adapted from the standard Jalali algorithms / PersianDate references) so the calendar code runs seamlessly across all KMP targets (JVM, Android, and future targets) without native or platform-specific Java dependencies.
- Provide reusable fake factories (`FakeCalendarDateFactory`) in test fixtures as required by `AGENTS.md`.

**Non-Goals:**
- UI components, date picker dialogs, or Compose formatting widgets (those belong to `:design` and `app/shared` in separate vertical slices).
- Database entity migrations or updates to expense records (the calendar interface is purely an infrastructure utility).
- Islamic (Hijri Lunar) calendar or other calendar systems.

## Decisions

### Decision 1: Placement in `core` Module
- **Choice**: Place interface and implementations in `core/src/commonMain/kotlin/com/base/calendar/`.
- **Rationale**: `core` is already configured for shared libraries and has minimal dependencies. All client modules (`app/shared`) and backend (`server`) can depend on `core`.
- **Alternatives Considered**: Placing inside `app/shared`; rejected because calendar calculations are core domain logic that should be available to both client and server targets.

### Decision 2: Pure Kotlin Implementation vs External JVM Library
- **Choice**: Implement the Persian conversion algorithm directly in Kotlin within `core` rather than pulling in a JVM-only Android/Java library dependency. The algorithm will follow the standard astronomical/arithmetic Jalali conversion algorithms used by `PersianDate` (Kazimierz M. Borkowski / Saman Zamani).
- **Rationale**: The library `com.github.samanzamani:PersianDate` is an Android/JVM Java library. Implementing the mathematical conversion directly in Kotlin Multiplatform ensures 100% KMP compatibility across JVM, Android, iOS, and Wasm targets without external binary dependencies or JitPack configuration overhead.
- **Alternatives Considered**: Adding JitPack dependency on `com.github.samanzamani:PersianDate`; rejected because it restricts KMP common code and introduces external repository resolution risks.

### Decision 3: Calendar Interface API Design
- **Choice**:
  ```kotlin
  enum class CalendarType {
    GREGORIAN,
    PERSIAN,
  }

  data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val type: CalendarType,
  ) : Comparable<CalendarDate>

  interface Calendar {
    val type: CalendarType
    fun isLeapYear(year: Int): Boolean
    fun getDaysInMonth(year: Int, month: Int): Int
    fun toCalendarDate(epochMillis: Long, timeZone: TimeZone = TimeZone.UTC): CalendarDate
    fun toEpochMillis(date: CalendarDate, timeZone: TimeZone = TimeZone.UTC): Long
    fun addDays(date: CalendarDate, days: Int): CalendarDate
    fun addMonths(date: CalendarDate, months: Int): CalendarDate
    fun addYears(date: CalendarDate, years: Int): CalendarDate
    fun toGregorian(date: CalendarDate): CalendarDate
    fun fromGregorian(date: CalendarDate): CalendarDate
  }
  ```
- **Rationale**: Clear, predictable contract. Using `kotlinx.datetime.TimeZone` ensures consistent handling of time zones without tying to `java.util.Calendar`.

### Decision 4: Dependency on `kotlinx-datetime` in `core`
- **Choice**: Add `libs.kotlinx.datetime` to `core/build.gradle.kts` `commonMain.dependencies`.
- **Rationale**: `kotlinx-datetime` is already in `gradle/libs.versions.toml` (version 0.7.1) and used across `app/shared`. Adding it to `core` standardizes `Instant` and `TimeZone` representations across all layers.

## Risks / Trade-offs

- **[Risk] Algorithm discrepancy at leap year boundaries (e.g. 33-year Jalali cycle vs astronomical calculation)**
  - *Mitigation*: Cover historical and modern test vectors with comprehensive unit tests for Persian calendar leap years and month transitions (e.g., years 1399, 1403, 1404, 1408).
- **[Risk] Invalid date construction (e.g. month > 12 or day > 31)**
  - *Mitigation*: Validate ranges during date instantiation or conversion, throwing `IllegalArgumentException` with descriptive error messages.
