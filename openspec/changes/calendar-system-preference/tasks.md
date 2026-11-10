# Tasks

## 1. Core Calendar Extensions

- [x] 1.1 Add `firstDayOfWeek: DayOfWeek` and `getDayOfWeek(date: CalendarDate): DayOfWeek` to `Calendar`, implementing them in `GregorianCalendar` and `PersianCalendar`. Verify with `CalendarTest`.
- [x] 1.2 Add calendar month naming helpers (e.g. `getMonthName(month: Int, type: CalendarType): String`) covering both Gregorian and Persian month names. Verify with unit tests in `core`.

## 2. User Settings Repository & DataStore

- [x] 2.1 Implement `UserSettingsRepository` (with `DataStoreUserSettingsRepository` and fake in same file) supporting reactive `calendarType: Flow<CalendarType>` and `setCalendarType(CalendarType)`. Verify with `UserSettingsRepositoryTest`.
- [x] 2.2 Wire `UserSettingsRepository` in Koin `AppModule.kt`. Verify dependency resolution passes.

## 3. Settings UI Feature & Main Navigation Integration

- [x] 3.1 Implement `SettingsComponent` with `state: Value<SettingsState>`, fake factories, and `onCalendarTypeSelected(type: CalendarType)`. Verify with `SettingsTest`.
- [x] 3.2 Implement `SettingsScreen` composable with accessible options for Gregorian and Persian calendar systems, and wire `SettingsComponentFactory` in `DecomposeModule.kt`.
- [x] 3.3 Connect `SettingsComponent` in `DefaultMainComponent` for `MainChildConfig.Settings` and display `SettingsScreen` in `MainScreen`. Verify with `SettingsFlowE2eTest` and `SettingsE2eUiTest`.

## 4. UI Date Display & Analytics Calendar Integration

- [x] 4.1 Update `AnalyticsComponent` to observe user's preferred `calendarType` and format selected month labels, month navigation, and date groups according to active calendar. Verify with `AnalyticsCalendarTest`.
- [x] 4.2 Update transaction date formatting helper (or formatInstant) in `PocketDetailsComponent` / shared formatters to respect user's preferred calendar. Verify transaction list dates display correctly.
- [x] 4.3 Run full verification gate (`jvmTest`, `detektMain`, `ktfmtFormat`) to ensure all tests pass and code style is clean.
