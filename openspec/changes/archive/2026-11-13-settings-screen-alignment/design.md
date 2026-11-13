# Design

## Context

See `proposal.md` for motivation. Currently, `:app:shared` contains a minimal `SettingsScreen` that displays only `DisplayCalendarSystemCard` bound to `SettingsComponent`. The `:design` module (`com.base.design.features.settings`) provides the complete visual hierarchy, including section headers, rounded container cards, navigation rows for categories/labels, language selection, display currency option cards, an end-of-day reminder switch row, and an app version badge.

## Goals / Non-Goals

**Goals:**
- Implement all missing UI building blocks in `:app:shared` (`com.base.feature.settings.SettingsComponents.kt` and `SettingsScreen.kt`) to achieve 1:1 visual fidelity with `:design`.
- Model `CurrencyType` (`TOMANS`, `RIALS`) as an enum in `:app:shared` and persist the user's choice in `UserSettingsRepository` via DataStore preferences.
- Persist `endOfDayReminderEnabled: Boolean` in `UserSettingsRepository` via DataStore preferences.
- Connect category count reactively in `SettingsComponent` using `CategoryRepository.getCategories()`.
- Provide complete accessibility semantics, keyboard navigation support, and `@ScreenPreviews` / `@ComponentPreviews`.
- Provide callback entry points in `SettingsComponent` for `onCategoriesClicked`, `onLabelsClicked`, and `onLanguageClicked`.

**Non-Goals:**
- Implementing the downstream labels flow or multi-language switching runtime in this PR (these are triggered via callbacks or toast/noop until their dedicated issues land).
- Scheduling actual system notifications for the reminder alarm in this PR (this PR establishes UI and persistent preference storage; system alarm worker is tracked separately).

## Decisions

### Decision 1: Model CurrencyType and Preferences in UserSettingsRepository
- **Choice**: Add `enum class CurrencyType { TOMANS, RIALS }` in `com.base.feature.settings`.
- **Rationale**: Follows AGENTS.md rule: "Prefer enums over strings. Use enums in DTOs and DAOs and domain layer where possible."
- **Storage**: DataStore preference key `stringPreferencesKey("currency_type")` (default `TOMANS`), and `booleanPreferencesKey("end_of_day_reminder")` (default `true`).

### Decision 2: Component Structure & AGENTS.md Concurrency Guidelines
- **Choice**: Keep `SettingsComponent` functions non-suspending, launching coroutines in `componentScope()`.
- **Rationale**: AGENTS.md strictly dictates: "Don't make functions of Decompose components `suspend`. instead launch in them and return the `Job`."

### Decision 3: Extract Reusable UI Components
- **Choice**: Create `SettingsComponents.kt` in `com.base.feature.settings` containing:
  - `SettingsSectionHeader(title: String)`
  - `SettingsCardContainer(...)`
  - `SettingsNavigationRow(title: String, subtitle: String, icon: ImageVector, countBadgeText: String?, ...)`
  - `SettingsLanguageRow(selectedLanguage: String, ...)`
  - `DisplayCurrencyCard(selectedCurrency: CurrencyType, ...)`
  - `CurrencyOptionCard(title: String, subtitle: String, symbolText: String, isSelected: Boolean, ...)`
  - `SwitchRow(title: String, subtitle: String, icon: ImageVector, checked: Boolean, ...)`
  - `SettingsVersionBadge(version: String)`
- **Rationale**: Matches `:design` module structure while respecting `PocketyTheme` / standard Material 3 color system in `:app:shared`.

### Decision 4: Test Tags & Keyboard Accessibility
- **Choice**: Assign clear test tags to interactive items:
  - `settings-screen`
  - `settings-navigation-categories`
  - `settings-navigation-labels`
  - `settings-row-language`
  - `currency-option-tomans`
  - `currency-option-rials`
  - `calendar-option-gregorian`
  - `calendar-option-persian`
  - `settings-switch-reminder`
- **Semantics**: Use `Role.RadioButton` on currency and calendar options, `Role.Switch` on the reminder toggle, and `Role.Button` on navigation rows.

## Risks / Trade-offs

- **Risk**: Missing category count if `CategoryRepository` emits empty list initially.
  - **Mitigation**: Observe `CategoryRepository.getCategories()` flow in `SettingsComponent` and update `categoriesCount` reactively with `categories.size`.
- **Risk**: String resources differences across English and Persian locales.
  - **Mitigation**: Provide complete parallel string definitions in both `values/strings.xml` and `values-fa/strings.xml`.
