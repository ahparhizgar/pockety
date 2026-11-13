# Tasks

## 1. Localization Resources

- [ ] 1.1 Add string resources for Privacy & Security section, privacy policy title, hero cards, core commitment pillars, summary bullet points, and footer update note in `values/strings.xml` and Persian translations in `values-fa/strings.xml`, verifying `./gradlew compileKotlinJvm --offline` succeeds.

## 2. Privacy Policy Decompose Component & Navigation

- [ ] 2.1 Implement `PrivacyPolicyComponent` (interface, `DefaultPrivacyPolicyComponent`, `FakePrivacyPolicyComponent`, and `PrivacyPolicyComponentFactory`) in `com.base.feature.privacypolicy` and verify unit tests in `PrivacyPolicyTest.kt` pass.
- [ ] 2.2 Add `RootFlowDestination.PrivacyPolicy` destination to `RootFlowDestination.kt`, wire component creation and back navigation in `DefaultRootComponent.kt`, register factory in Koin/component provider, and verify `RootComponentTest` passes.
- [ ] 2.3 Extend `SettingsComponent` with `onPrivacyPolicyClicked()` callback and add `onPrivacyPolicyClick: () -> Unit` to `SettingsComponentFactory`, wiring it in `DefaultMainComponent.kt` to push `RootFlowDestination.PrivacyPolicy`. Verify with `SettingsTest.kt`.

## 3. Privacy Policy Screen & UI Components

- [ ] 3.1 Implement modular UI components in `PrivacyPolicyComponents.kt` (`PrivacyHeroCard`, `PrivacyPolicyPillarCard`, `PrivacyDataSummaryCard`, `PrivacyBulletPoint`) with previews and test tags.
- [ ] 3.2 Implement `PrivacyPolicyScreen.kt` in `com.base.feature.privacypolicy` using `Scaffold` and `PocketyTopAppBar`, and connect it in `App.kt` `RootChildContent()`. Verify UI rendering with `PrivacyPolicyE2eUiTest.kt`.

## 4. Settings Screen Touchpoint

- [ ] 4.1 Update `SettingsScreen.kt` in `com.base.feature.settings` to add the "Privacy & Security" section with the `SettingsNavigationRow` for Privacy Policy, and verify with `SettingsE2eUiTest.kt`.
- [ ] 4.2 Implement end-to-end integration test `PrivacyPolicyFlowE2eTest.kt` verifying navigation from Settings to Privacy Policy and back.

## 5. Verification Gate

- [ ] 5.1 Run full project verification: level 1 compilation, level 2 tests & kover verification, level 3 detekt, and level 4 ktfmt (`./gradlew jvmTest koverVerify :detektMain ktfmtCheck --offline`).
