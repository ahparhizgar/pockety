# Tasks

## 1. Data Model & Persistent Storage

- [x] 1.1 Define `AppLanguage` enum (`ENGLISH("en")`, `PERSIAN("fa")`) in `com.base.feature.settings` in `:app:shared`.
- [x] 1.2 Update `UserSettingsRepository` and `DataStoreUserSettingsRepository` with `appLanguage: Flow<AppLanguage>` and `suspend fun setAppLanguage(language: AppLanguage)`.
- [x] 1.3 Update `FakeUserSettingsRepository` to support `appLanguage`.
- [x] 1.4 Add unit tests in `UserSettingsRepositoryTest.kt` verifying default language is `ENGLISH`, persisting `PERSIAN`, and reactive emission. Verify with `./gradlew compileKotlinJvm --offline`.

## 2. Settings Component & Dialog Integration

- [x] 2.1 Update `SettingsState` in `SettingsComponent.kt` to expose `selectedLanguage: AppLanguage` and `isLanguageDialogVisible: Boolean`.
- [x] 2.2 Define `LanguageSettingsHandler` interface in `:app:shared` (with fake for testing and desktop/default fallback).
- [x] 2.3 Implement language selection handling and dialog visibility controls in `DefaultSettingsComponent` and `FakeSettingsComponent`.
- [x] 2.4 Update `SettingsTest.kt` covering `selectedLanguage` initial state, `onLanguageSelected`, and dialog visibility behavior. Verify with `./gradlew compileKotlinJvm --offline`.

## 3. UI, Resources & RTL Layout Support

- [x] 3.1 Update string resources in `values/strings.xml` and `values-fa/strings.xml` with labels for Persian language display ("Persian" / "فارسی") and language selection dialog title/actions.
- [x] 3.2 Implement `LanguageSelectionDialog` in `:app:shared` and update `SettingsLanguageRow` / `SettingsScreen` to display the active language name and trigger the dialog when visible.
- [x] 3.3 Connect `LocalLayoutDirection` in root Compose composition (`RootScreen` / `BaseTheme`) driven by `selectedLanguage` (`LayoutDirection.Rtl` for `AppLanguage.PERSIAN`, `LayoutDirection.Ltr` for `AppLanguage.ENGLISH`).
- [x] 3.4 Add previews and UI tests (`SettingsLanguageE2eUiTest`) verifying RTL layout direction and language row behavior.

## 4. Android Platform Per-App Language Settings

- [x] 4.1 Create `locales_config.xml` in `:app:androidApp/src/main/res/xml/` containing `en` and `fa`, and declare `android:localeConfig="@xml/locales_config"` in `AndroidManifest.xml`.
- [x] 4.2 Implement Android `LanguageSettingsHandler` in `:app:androidApp` checking `Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU` (API 33+) to dispatch `Settings.ACTION_APP_LOCALE_SETTINGS` intent, and fallback to in-app dialog on API < 33.
- [x] 4.3 Wire Android `LanguageSettingsHandler` in Koin Android module and sync system locale in `MainActivity.kt`.
- [x] 4.4 Run `./gradlew compileKotlinJvm --offline` to ensure end-to-end multiplatform build succeeds.
