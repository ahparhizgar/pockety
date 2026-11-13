# Tasks

## 1. Preferences & Domain Storage

- [x] 1.1 Add `CurrencyType` enum (`TOMANS`, `RIALS`) and update `UserSettingsRepository` with `currencyType` and `endOfDayReminderEnabled` flows and setters in `:app:shared`.
- [x] 1.2 Implement DataStore persistence for currency and reminder preferences in `DataStoreUserSettingsRepository` and update `FakeUserSettingsRepository`.
- [x] 1.3 Add tests in `UserSettingsRepositoryTest.kt` verifying default values, persistence, and reactive updates for currency and reminders.

## 2. String Resources & Localization

- [x] 2.1 Add string resources for all settings section headers ("Organize & Taxonomy", "Preferences & Region", "Notifications & Automation"), row labels ("Categories", "Labels", "App language", "End-of-day reminder"), subtitles, currencies ("Tomans", "Rials"), and version badge to `values/strings.xml` and `values-fa/strings.xml`.

## 3. Settings UI Components

- [x] 3.1 Implement reusable settings composables in `com.base.feature.settings.SettingsComponents.kt` in `:app:shared` matching the `:design` module (`SettingsSectionHeader`, `SettingsCardContainer`, `SettingsNavigationRow`, `SettingsLanguageRow`, `DisplayCurrencyCard`, `CurrencyOptionCard`, `SwitchRow`, `SettingsVersionBadge`).
- [x] 3.2 Add previews using `@ComponentPreviews` for each settings component with both light and dark variations.

## 4. Settings Component & Screen Integration

- [x] 4.1 Update `SettingsComponent` interface, `DefaultSettingsComponent`, and `FakeSettingsComponent` to expose `selectedCurrency`, `endOfDayReminderEnabled`, and `categoriesCount`, along with selection/toggle callbacks.
- [x] 4.2 Update `SettingsScreen.kt` in `:app:shared` to arrange the complete 3-section layout and version badge, applying test tags (`settings-navigation-categories`, `currency-option-tomans`, `currency-option-rials`, `settings-switch-reminder`).
- [x] 4.3 Add previews in `SettingsScreen.kt` using `@ScreenPreviews` covering standard and Persian/Rials variations.

## 5. Verification & Testing

- [x] 5.1 Update `SettingsTest.kt` for component-level verification of currency selection and reminder toggling.
- [x] 5.2 Update `SettingsFlowE2eTest.kt` and `SettingsE2eUiTest.kt` to verify UI rendering, accessibility, and interactive clicks.
- [x] 5.3 Run all project verification gates (`./gradlew compileKotlinJvm compileKotlin`, `./gradlew jvmTest test`, `./gradlew :detektMain`, `./gradlew ktfmtFormat`) and confirm all checks pass.

## Workflow follow-up

- Reassign to Developer for implementation.
- Once Developer completes tasks and passes CI, archive the change with OpenSpec and rebase linearly onto `main`.
