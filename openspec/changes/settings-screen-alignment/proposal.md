# Proposal

## Why

The existing production Settings screen in `:app:shared` only displays calendar system configuration within a basic card layout. The design prototype in `:design` (`com.base.design.features.settings.Screen` and `Component`) specifies a structured, sectioned Settings screen containing:
1. **Organize & Taxonomy**: Navigation rows for Categories (showing count badge) and Labels (or placeholder/entry point).
2. **Preferences & Region**: App Language selector row, Display Currency selector (Tomans vs Rials), and the Calendar System selector card.
3. **Notifications & Automation**: End-of-day reminder toggle (with time and subtitle description).
4. **Application Branding / Footer**: Version badge display ("Pockety v...").

Aligning the Settings screen with the design prototype brings visual consistency, establishes preference management architecture for currency and notification reminders, and integrates category/taxonomy navigation points.

## What Changes

- **Domain & Repository Preferences (`user-settings`)**:
  - Add `CurrencyType` (`TOMANS`, `RIALS`) preference storage and reactive observation to `UserSettingsRepository`.
  - Add end-of-day reminder preference toggle (`Boolean`, defaulting to true) to `UserSettingsRepository`.
  - Provide reactive flows and update methods in `UserSettingsRepository` (DataStore-backed and fake implementations).
- **Settings Component & State**:
  - Extend `SettingsState` with `selectedCurrency: CurrencyType`, `endOfDayReminderEnabled: Boolean`, `categoriesCount: Int`, and `appVersion: String`.
  - Add callbacks/methods to `SettingsComponent`:
    - `onCurrencySelected(type: CurrencyType)`
    - `onReminderToggled(enabled: Boolean)`
    - `onCategoriesClicked()`
    - `onLabelsClicked()`
    - `onLanguageClicked()`
  - Provide category counts from `CategoryRepository` into `SettingsComponent`.
- **Settings Screen UI Alignment**:
  - Replicate and integrate UI section containers from `:design`: `SettingsSectionHeader`, `SettingsCardContainer`, `SettingsNavigationRow`, `SettingsLanguageRow`, `DisplayCurrencyCard`, `CurrencyOptionCard`, `SwitchRow`, and `SettingsVersionBadge`.
  - Structure `SettingsScreen` into the 3 defined sections: Organize & Taxonomy, Preferences & Region, Notifications & Automation, plus the version badge footer.
  - Implement full keyboard accessibility and semantics on interactive rows/cards.
  - Add screen and component previews conforming to `@ScreenPreviews` and `@ComponentPreviews`.
- **Localization**:
  - Add string resources in `values/strings.xml` and `values-fa/strings.xml` for section titles, row labels, currencies (Tomans/Rials), reminders, and versions.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `user-settings`: Add requirements and scenarios for display currency preference management, end-of-day reminder preference management, taxonomy section items (categories count and navigation events), and complete Settings screen section hierarchy.

## Impact

- **UI & Presentation**: `SettingsScreen.kt`, `SettingsComponent.kt` and associated UI components in `:app:shared` (`com.base.feature.settings`).
- **Data & Storage**: `UserSettingsRepository.kt` in `:app:shared` gains keys for currency preference and reminder settings.
- **Resources**: `strings.xml` (both default and Persian) in `:app:shared`.
- **Testing**: `SettingsTest.kt`, `SettingsFlowE2eTest.kt`, and `SettingsE2eUiTest.kt` updated to verify all sections, currency selection, reminder toggling, and categories navigation.
