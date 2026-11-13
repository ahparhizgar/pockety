# Design

## Context

See `proposal.md` for background and motivation. The `:design` module already contains the full visual prototype in `com.base.design.features.privacypolicy` (`Screen.kt`, `Component.kt`), along with the "Privacy & Security" section in `com.base.design.features.settings.Screen.kt`. However, in `:app:shared`, neither the `SettingsScreen` nor the navigation tree includes Privacy Policy support.

## Goals / Non-Goals

**Goals:**
- Provide `PrivacyPolicyScreen` and modular components (`PrivacyHeroCard`, `PrivacyPolicyPillarCard`, `PrivacyDataSummaryCard`, `PrivacyBulletPoint`) in `:app:shared` matching the design system and Material 3 theme.
- Create `PrivacyPolicyComponent` (with interface, `DefaultPrivacyPolicyComponent`, `FakePrivacyPolicyComponent`, and factory) using Decompose patterns.
- Integrate `RootFlowDestination.PrivacyPolicy` into `RootComponent`, `RootFlowDestination`, and `App.kt`.
- Integrate the Privacy Policy navigation row in `SettingsScreen` and hook it to `SettingsComponent.onPrivacyPolicyClicked()`.
- Route the click in `MainComponent` to push `RootFlowDestination.PrivacyPolicy` onto the root navigation stack.
- Support complete keyboard accessibility, Compose string resources, and `@ScreenPreviews` / `@ComponentPreviews`.

**Non-Goals:**
- External web links or hosting an online privacy policy URL in this slice (this is fully on-device).
- Dynamic server-fetched privacy policy terms (policy commitments are bundled and localized).

## Decisions

### Decision 1: Navigation Architecture via RootFlowDestination
- **Choice**: Add `RootFlowDestination.PrivacyPolicy` to `RootFlowDestination` and handle it in `DefaultRootComponent` and `App.kt`.
- **Alternatives Considered**:
  - *Make Privacy Policy a child of MainComponent*: Rejected because `MainComponent` manages bottom bar tabs (Home, Inbox, Pockets, Analytics, Settings). A dedicated full-screen child on `RootComponent` properly hides the bottom bar and standardizes back button behavior across mobile and desktop.
  - *In-place replacement in Settings*: Rejected because Privacy Policy is a full sub-screen with a top app bar back action.

### Decision 2: SettingsComponent and MainComponent Callback Plumbing
- **Choice**: Add `onPrivacyPolicyClick: () -> Unit` to `SettingsComponentFactory.create()`, call it via `onPrivacyPolicyClicked()`, and pass `{ navigation.pushNew(RootFlowDestination.PrivacyPolicy) }` in `MainComponent.createSettingsChild()`.
- **Alternatives Considered**:
  - *Event bus*: Unnecessary indirection for direct hierarchical Decompose navigation.

### Decision 3: Package Structure & Component Layout
- **Choice**: Place the new feature under `com.base.feature.privacypolicy` in `app/shared/src/commonMain/kotlin/com/base/feature/privacypolicy/`:
  - `PrivacyPolicyComponent.kt`: Decompose component, fake, and factory.
  - `PrivacyPolicyComponents.kt`: UI cards (`PrivacyHeroCard`, `PrivacyPolicyPillarCard`, `PrivacyDataSummaryCard`, `PrivacyBulletPoint`).
  - `PrivacyPolicyScreen.kt`: Scaffold, TopAppBar, and scrollable container.
- **Rationale**: Adheres to AGENTS.md modularity, preview guidelines, and separation of UI components.

### Decision 4: Test Tags & Accessibility Semantics
- **Choice**:
  - `settings-section-privacy`: Container for the Privacy & Security section in Settings.
  - `settings-navigation-privacy-policy`: Navigation row for privacy policy.
  - `privacy-policy-screen`: Root scaffold test tag.
  - `privacy-policy-back`: Back button in top app bar.
  - `privacy-policy-pillar-personal-data`, `privacy-policy-pillar-sms-data`, `privacy-policy-pillar-telemetry`: Commitment pillar cards.

## Risks / Trade-offs

- **Risk**: Missing localized strings for Persian locale causing runtime fallback or missing text.
  - **Mitigation**: Add parallel string keys to both `values/strings.xml` and `values-fa/strings.xml`.
- **Risk**: Root back button handling on Android or desktop predictive back.
  - **Mitigation**: `RootFlowDestination.PrivacyPolicy` uses standard `rootNavigation.pop()`, properly hooked into `RootComponent.onBack()` and Decompose back stack.
