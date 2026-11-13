# Proposal

## Why

Users handling financial and SMS-derived expense data need clear and transparent commitments regarding their privacy. Pockety is designed offline-first and does not send personal data or SMS contents to remote servers. Providing an accessible Privacy Policy screen and an intuitive touchpoint within the Settings screen reinforces user trust and aligns `:app:shared` with the completed design specification in `:design`.

## What Changes

- **Settings Screen Touchpoint**:
  - Add a "Privacy & Security" section to `SettingsScreen` in `:app:shared`.
  - Include a navigation row for "Privacy Policy" with subtitle "Offline-first commitments & anonymous telemetry", an icon container, and click handler.
  - Expose `onPrivacyPolicyClicked()` on `SettingsComponent` and implement it in `DefaultSettingsComponent` and `FakeSettingsComponent`.
  - Pass the privacy policy navigation action from `MainComponent` / `RootComponent` to launch the Privacy Policy flow.
- **Privacy Policy Screen & Decompose Architecture**:
  - Create `PrivacyPolicyComponent` (interface, default implementation with `onBack()`, fake implementation, and factory) in `:app:shared` (`com.base.feature.privacypolicy`).
  - Introduce `RootFlowDestination.PrivacyPolicy` (or child in Root navigation stack) to present the screen and support back navigation.
  - Create `PrivacyPolicyScreen` in `:app:shared` replicating the `:design` module layout:
    - Hero header card emphasizing offline-first principles.
    - Three core commitment pillar cards:
      1. "We do not send personal data" (local storage only, no monetization).
      2. "We do not send SMS data" (on-device SMS parsing, no server transmission).
      3. "Anonymous usage data only" (anonymous diagnostics and aggregated metrics for app stability).
    - Summary card with bullet points.
    - Footer badge for policy version / last updated date.
    - Top app bar with back navigation button.
- **Localization**:
  - Add localized string resources in English (`values/strings.xml`) and Persian (`values-fa/strings.xml`) for all Privacy Policy text, section headers, row titles, and subtitles.
- **Accessibility & Previews**:
  - Provide keyboard navigation support and test tags (`settings-navigation-privacy-policy`, `privacy-policy-screen`, etc.).
  - Add `@ScreenPreviews` and `@ComponentPreviews` for Privacy Policy components and screen.

## Capabilities

### New Capabilities
- `privacy-policy`: Defines requirements and scenarios for displaying the privacy policy, detailing core commitments (no personal data sent, no SMS data sent, anonymous telemetry only), and handling back navigation.

### Modified Capabilities
- `user-settings`: Add requirement and scenario for the Privacy & Security section touchpoint on the Settings screen and navigating to the Privacy Policy.

## Impact

- **UI & Presentation**:
  - `com.base.feature.settings.SettingsScreen.kt` and `SettingsComponent.kt`
  - New package `com.base.feature.privacypolicy` (`PrivacyPolicyScreen.kt`, `PrivacyPolicyComponents.kt`, `PrivacyPolicyComponent.kt`)
  - Navigation in `com.base.feature.root.RootFlowDestination.kt`, `RootComponent.kt`, `MainComponent.kt`, and `App.kt`
- **Resources**:
  - `values/strings.xml` and `values-fa/strings.xml` in `:app:shared`
- **Testing**:
  - Unit tests for `PrivacyPolicyComponent` and updated `SettingsComponent`
  - Integration/E2E flow tests for navigation from Settings to Privacy Policy and back
  - UI tests for screen rendering, commitments display, and keyboard accessibility
