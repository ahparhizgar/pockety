# Tests Specification

## Unit Tests (single component / class / function)

### `privacypolicy/PrivacyPolicyTest`

**Target**: `PrivacyPolicyComponent`

**Scenarios**:

- Given PrivacyPolicyComponent initialized with back navigation callback
    - When user invokes `onBack()`,
        - [ ] Then the registered back callback is executed

### `settings/SettingsTest`

**Target**: `SettingsComponent`

**Scenarios**:

- Given SettingsComponent initialized with `onPrivacyPolicyClick` callback
    - When user clicks Privacy Policy via `onPrivacyPolicyClicked()`,
        - [ ] Then the `onPrivacyPolicyClick` callback is executed

## E2E Flow Tests

### `privacypolicy/PrivacyPolicyFlowE2eTest`

**Flow**: Navigation from RootComponent to Settings, navigating to Privacy Policy, and navigating back

**Scenarios**:

- Given app initialized at MainComponent with Settings tab selected
    - When user clicks Privacy Policy navigation row
        - [ ] Then active child in RootComponent becomes `RootFlowDestination.PrivacyPolicy`
    - When user triggers back navigation from Privacy Policy
        - [ ] Then active child in RootComponent returns to `RootFlowDestination.Main` with Settings active

## UI & Accessibility Tests

### `privacypolicy/PrivacyPolicyE2eUiTest`

**Scenarios**:

1. Privacy Policy Screen Rendering:
   - Given `PrivacyPolicyScreen` is rendered
   - When the screen loads
   - Then `privacy-policy-screen` is displayed
   - And the hero card, 3 commitment pillar cards, summary card, and version footer are visible

2. TopAppBar Back Navigation & Keyboard Accessibility:
   - Given `PrivacyPolicyScreen` is displayed
   - When the user clicks or presses Enter on the back button in the top app bar
   - Then the `onBack` callback is triggered

### `settings/SettingsE2eUiTest`

**Scenarios**:

1. Privacy & Security Section Rendering:
   - Given `SettingsScreen` is rendered
   - When the screen loads
   - Then the "Privacy & Security" section header and `settings-navigation-privacy-policy` row are displayed with subtitle "Offline-first commitments & anonymous telemetry"

2. Interactive Privacy Policy Navigation:
   - Given `SettingsScreen` is displayed
   - When the user clicks or presses Enter on `settings-navigation-privacy-policy`
   - Then `onPrivacyPolicyClicked` is invoked
