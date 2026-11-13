# Tests Specification

## Unit Tests (single component / class / function)

### `settings/SettingsTest`

**Target**: `SettingsComponent` / `UserSettingsRepository` integration

**Scenarios**:

- Given SettingsComponent initialized with default preferences
    - When observing state,
        - [ ] Then `selectedLanguage` is `AppLanguage.ENGLISH`
    - When `onLanguageSelected(AppLanguage.PERSIAN)` is triggered,
        - [ ] Then `selectedLanguage` updates to `AppLanguage.PERSIAN` and is persisted in `UserSettingsRepository`
    - When `onLanguageClicked()` is triggered on a device with system locale support disabled / unavailable,
        - [ ] Then `isLanguageDialogVisible` becomes true
    - When language dialog dismiss action is triggered,
        - [ ] Then `isLanguageDialogVisible` becomes false

### `settings/LanguageSettingsHandlerTest`

**Target**: `LanguageSettingsHandler` platform routing

**Scenarios**:

- Given `LanguageSettingsHandler` on API 33+
    - When `supportsSystemLocaleSettings` is queried,
        - [ ] Then it returns true
    - When `openSystemLocaleSettings()` is called,
        - [ ] Then an intent targeting `ACTION_APP_LOCALE_SETTINGS` with the app package URI is launched
- Given `LanguageSettingsHandler` on API < 33
    - When `supportsSystemLocaleSettings` is queried,
        - [ ] Then it returns false

## Repository & Data Tests

### `settings/UserSettingsRepositoryTest`

**Target**: `DataStoreUserSettingsRepository`

**Scenarios**:

- Given UserSettingsRepository backed by DataStore Preferences
    - When no language preference was saved,
        - [ ] Then `appLanguage` flow emits `AppLanguage.ENGLISH`
    - When `setAppLanguage(AppLanguage.PERSIAN)` is executed,
        - [ ] Then `appLanguage` flow emits `AppLanguage.PERSIAN` and value is stored across repository instances
    - When `setAppLanguage(AppLanguage.ENGLISH)` is executed,
        - [ ] Then `appLanguage` flow emits `AppLanguage.ENGLISH`

## E2E Flow Tests

### `settings/SettingsLanguageFlowE2eTest`

**Flow**: Navigation into Settings tab and changing language preference

**Scenarios**:

- Given default app state on root navigation
    - When user navigates to Settings tab,
        - [ ] Then Settings screen displays App Language row showing "English"
    - When user changes language to Persian,
        - [ ] Then Settings screen displays App Language row showing "فارسی"
        - [ ] And the active layout direction transitions to Right-to-Left (RTL)

## UI & Accessibility Tests

### `settings/SettingsLanguageE2eUiTest`

**Scenarios**:

1. **Language row accessibility and selection**:
   - Verify `SettingsLanguageRow` has appropriate role and accessibility click actions.
   - Verify that clicking the language row opens the language selection dialog when running in fallback mode.
   - Verify keyboard navigability of language options in the dialog (Focusable, selectable via Enter/Space).
2. **RTL Directional Layout**:
   - Verify that switching to Persian causes `LocalLayoutDirection` to evaluate to `LayoutDirection.Rtl`.
   - Verify UI elements (row text alignments, navigation icons, start/end padding) layout correctly mirrored under RTL.
