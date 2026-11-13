# Design

## Context

See `proposal.md` for background and user goals.

Currently:
- The app is a Kotlin Multiplatform Compose application (`:app:shared`, `:app:androidApp`, `:app:desktopApp`).
- Compose resources have `values/strings.xml` (English default) and `values-fa/strings.xml` (Persian).
- `SettingsComponent` currently has `fun onLanguageClicked()`, which is a no-op stub.
- `SettingsLanguageRow` displays a hardcoded "English" label.
- Preferences are managed via Jetpack DataStore in `UserSettingsRepository` (storing `calendarType`, `currencyType`, `endOfDayReminderEnabled`).
- On Android, `MainActivity` extends `ComponentActivity` with `minSdk = 24` and `targetSdk = 36`.

## Goals / Non-Goals

**Goals:**
- Provide domain model `AppLanguage` with values `ENGLISH("en")` and `PERSIAN("fa")`.
- Store and observe `AppLanguage` in `UserSettingsRepository` and expose in `SettingsState`.
- Support Android 13+ (API 33+) system per-app language settings via `android.provider.Settings.ACTION_APP_LOCALE_SETTINGS` or `LocaleManager` / `AppCompatDelegate.setApplicationLocales`.
- Support legacy Android (< API 33) and desktop/multiplatform fallback via an in-app language picker dialog that commits directly to `UserSettingsRepository`.
- Apply `LocalLayoutDirection` dynamically (`LayoutDirection.Rtl` for Persian, `LayoutDirection.Ltr` for English) at the root Compose hierarchy (`RootScreen` / `BaseTheme`).
- Maintain testing instructions specifying target Android versions for verification.

**Non-Goals:**
- Dynamic remote string downloading (strings remain statically compiled in Compose multiplatform resources).
- Supporting languages beyond English and Persian in this change.

## Decisions

### Decision 1: Model `AppLanguage` in `:app:shared` domain
We define `AppLanguage` enum in `com.base.feature.settings`:
```kotlin
enum class AppLanguage(val code: String) {
  ENGLISH("en"),
  PERSIAN("fa");

  companion object {
    fun fromCode(code: String): AppLanguage =
        entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
  }
}
```
- **Rationale**: Simple, type-safe, mirrors `CalendarType` and `CurrencyType`.
- **Alternatives considered**: Freeform BCP-47 String tags. Rejected because the app only supports English and Persian and strict typing prevents invalid state.

### Decision 2: Store language preference in `UserSettingsRepository`
Add `appLanguageKey = stringPreferencesKey("app_language")` in `DataStoreUserSettingsRepository`:
- `val appLanguage: Flow<AppLanguage>` (defaults to `AppLanguage.ENGLISH`)
- `suspend fun setAppLanguage(language: AppLanguage)`
- Update `FakeUserSettingsRepository` similarly.
- **Rationale**: Consistent with all other user preferences; allows multiplatform testing and instant local fallback.

### Decision 3: Language switcher interaction & platform routing
In `SettingsComponent`:
- Add `isLanguageDialogVisible: Boolean` in `SettingsState`.
- Introduce a platform bridge / handler `LanguageSettingsHandler`:
  ```kotlin
  interface LanguageSettingsHandler {
    val supportsSystemLocaleSettings: Boolean
    fun openSystemLocaleSettings()
  }
  ```
- When `onLanguageClicked()` is invoked:
  - If `handler.supportsSystemLocaleSettings` is true: call `handler.openSystemLocaleSettings()`.
  - Otherwise: set `isLanguageDialogVisible = true` to display the in-app `LanguageSelectionDialog`.
- `onLanguageSelected(language: AppLanguage)`: saves language to `UserSettingsRepository` and dismisses dialog.
- On Android:
  - On API 33+ (Android 13+ / `Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU`):
    `supportsSystemLocaleSettings` is true. `openSystemLocaleSettings()` creates an Intent with `Settings.ACTION_APP_LOCALE_SETTINGS` and `Uri.fromParts("package", context.packageName, null)`.
  - On API < 33:
    `supportsSystemLocaleSettings` is false, falling back to the Compose dialog.
  - Additionally, `MainActivity` observes `userSettingsRepository.appLanguage` or applies `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))` / updates configuration to keep Android system resource lookups consistent.
  - Include `res/xml/locales_config.xml` in `:app:androidApp` registered in `AndroidManifest.xml` (`android:localeConfig="@xml/locales_config"`) for standard per-app language settings in Android 13+.

### Decision 4: Dynamic RTL layout and Compose CompositionLocalProvider
In Compose root (`RootScreen` or `BaseTheme`):
- Observe `selectedLanguage` from `RootComponent` or `userSettingsRepository`.
- Determine layout direction:
  ```kotlin
  val layoutDirection = when (appLanguage) {
    AppLanguage.PERSIAN -> LayoutDirection.Rtl
    AppLanguage.ENGLISH -> LayoutDirection.Ltr
  }
  ```
- Wrap content with:
  ```kotlin
  CompositionLocalProvider(
    LocalLayoutDirection provides layoutDirection
  ) {
    ...
  }
  ```
- **Rationale**: Compose natively flips horizontal layouts (Row, start/end paddings, directional navigation) based on `LocalLayoutDirection`.

### Decision 5: Syncing system locale on Android 13+ back to `UserSettingsRepository`
- On Android 13+, when the user changes the locale via the system settings page and returns to the app (`onResume`), `MainActivity` can read `context.getSystemService(LocaleManager::class.java).applicationLocales` or `AppCompatDelegate.getApplicationLocales()`. If the first locale is "fa", update `userSettingsRepository.setAppLanguage(AppLanguage.PERSIAN)`. If "en", update to `AppLanguage.ENGLISH`.
- This ensures two-way consistency whether changed via in-app or system settings.

## Risks / Trade-offs

- **[Risk] Activity restart on locale change**: Changing locales or `AppCompatDelegate.setApplicationLocales` may trigger an activity recreation on Android.
  - *Mitigation*: Decompose components are retained (`retainedComponent`), preserving state across recreation.
- **[Risk] Compose multiplatform string resource locale**: Compose multiplatform `stringResource` needs to match the active locale.
  - *Mitigation*: Updating the app configuration / `LocalLayoutDirection` and DataStore ensures Compose recomposes all UI with the new locale resources.
- **[Risk] Intent fails on some vendor Android 13 devices**: Some custom OEM ROMs may not support `ACTION_APP_LOCALE_SETTINGS` cleanly.
  - *Mitigation*: Wrap intent launch in `runCatching` with fallback to in-app dialog if intent resolution fails.

## Target Android Versions for Testing

- **Android 13+ (API 33+)**: Test system per-app language settings launching directly from Settings -> Language, and verify returning to the app reflects Persian (RTL) or English (LTR).
- **Android 12 and below (API 24 to 32)**: Test in-app language dialog opening, selecting Persian/English, persistence across restarts, and RTL/LTR layout mirroring.
