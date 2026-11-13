# Proposal

## Why

Users in multilingual contexts require the ability to use Pockety in either English or Persian (`fa`), with appropriate Right-to-Left (RTL) layout direction when Persian is active. On Android 13+ (API 33+), Android provides per-app language settings in system settings, while on earlier versions or non-per-app platforms, local persistent preference storage is required to control the app language and layout direction dynamically.

## What Changes

- Model application language as an explicit preference (`AppLanguage`: `ENGLISH` ["en"], `PERSIAN` ["fa"]) in `user-settings`.
- Provide persistent storage and reactive observation for `AppLanguage` in `UserSettingsRepository` (DataStore) and `FakeUserSettingsRepository`.
- Expose app language state in `SettingsState` and update `SettingsLanguageRow` to display the active language ("English" or "فارسی" / Persian).
- Implement platform-aware language selection dispatch:
  - On Android 13+ (API 33+), provide an option / intent action (`android.provider.Settings.ACTION_APP_LOCALE_SETTINGS` or `LocaleManager` / `AppCompatDelegate.setApplicationLocales`) to open system per-app language settings.
  - On Android < 33 (and multiplatform fallback / in-app picker dialog), provide an in-app selection mechanism to toggle/select between English and Persian, persisting to `UserSettingsRepository`.
- Apply reactive layout direction (`LocalLayoutDirection` `Ltr` / `Rtl`) and locale configuration in Compose root (`RootScreen` / `BaseTheme`) and Android Activity lifecycle so Persian switches UI layout to RTL and English to LTR.
- Provide documentation / report on testing compatibility across Android API versions (specifically Android 13+ / API 33+ vs Android 12- / API 24-32).

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `user-settings`: Add requirements for app language preference storage, reactive observation, language switching interaction in Settings, and RTL layout direction coordination.

## Impact

- **Storage & State**: `UserSettingsRepository` in `:app:shared` gains `appLanguage: Flow<AppLanguage>` and `setAppLanguage(AppLanguage)`.
- **UI & Presentation**:
  - `SettingsComponent` / `SettingsState` in `:app:shared` exposes `selectedLanguage: AppLanguage`.
  - `SettingsLanguageRow` in `SettingsScreen.kt` connects to `onLanguageClicked()`, opening a language selector dialog or delegating to platform intent.
  - `BaseTheme` or `RootScreen` observes `selectedLanguage` and injects `LocalLayoutDirection` (`LayoutDirection.Rtl` for Persian, `LayoutDirection.Ltr` for English).
- **Android Platform**:
  - `MainActivity.kt` and/or platform bridge in `:app:androidApp` handles per-app language settings intent on API 33+ and syncing system locale with `AppCompatDelegate` / `LocaleManager`.
  - Android resource locale configuration (`locales_config.xml`) for per-app language support.
