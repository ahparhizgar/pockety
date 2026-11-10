# Tasks

## 1. Core Module Dependency & Model Definitions

- [ ] 1.1 Add `kotlinx-datetime` dependency to `core/build.gradle.kts` and verify Gradle build succeeds with `./gradlew :core:compileKotlinJvm`
- [ ] 1.2 Implement `CalendarType` enum, `CalendarDate` data class with validation and comparison, and `Calendar` interface in `core/src/commonMain/kotlin/com/base/calendar/`
- [ ] 1.3 Implement `FakeCalendarDateFactory` in `core/src/commonMain/kotlin/com/base/calendar/Calendar.kt` (or associated test fixtures) as mandated by AGENTS.md

## 2. Gregorian Calendar Implementation

- [ ] 2.1 Implement `GregorianCalendar` implementing `Calendar` in `core/src/commonMain/kotlin/com/base/calendar/GregorianCalendar.kt` supporting leap year, days in month, epoch milliseconds / Instant conversions, and date arithmetic
- [ ] 2.2 Add unit tests in `core/src/commonTest/kotlin/com/base/calendar/GregorianCalendarTest.kt` covering leap years, days in month, arithmetic clamping, epoch conversions, and invalid dates, and verify with `./gradlew :core:jvmTest`

## 3. Persian (Solar Hijri) Calendar Implementation

- [ ] 3.1 Implement `PersianCalendar` implementing `Calendar` in `core/src/commonMain/kotlin/com/base/calendar/PersianCalendar.kt` supporting Jalali conversion algorithms, leap year determination, days in Persian months, epoch milliseconds / Instant conversions, and date arithmetic
- [ ] 3.2 Add unit tests in `core/src/commonTest/kotlin/com/base/calendar/PersianCalendarTest.kt` verifying conversions between Gregorian and Persian dates (including Nowruz 1403-01-01 <-> 2024-03-20, historical benchmarks), leap years, month boundaries, and arithmetic, and verify with `./gradlew :core:jvmTest`

## 4. Verification Gate

- [ ] 4.1 Run full project compilation, JVM tests, detekt, and ktfmt checks: `./gradlew compileKotlinJvm jvmTest :detektMain ktfmtCheck` and verify all pass cleanly

## Workflow follow-up

- Hand off to Developer for implementation.
- Archive the change after implementation and CI verification are complete.
