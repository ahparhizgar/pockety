# Tasks

## 1. Database & Repository Layer

- [ ] 1.1 Add `deletePocket(id: Long)` to `PocketDao` and `PocketRepository` (including `DefaultPocketRepository` and `FakePocketRepository`), and verify with `PocketRepositoryTest` passing.

## 2. Decompose Component & Flow Layer

- [ ] 2.1 Add `onDeleteClicked(): Job` and `onDeleted: () -> Unit` callback to `PocketSettingsComponent`, `PocketSettingsComponentFactory`, and `DefaultPocketSettingsComponent`. Verify with `PocketSettingsTest`.
- [ ] 2.2 Wire pocket deletion in `DefaultPocketSettingsFlowComponent` to invoke `onCompleted()`. Verify with `PocketSettingsFlowE2eTest`.

## 3. UI Implementation & String Resources

- [ ] 3.1 Add string resources for Danger Zone title, description, and delete button text in strings XML/Res.
- [ ] 3.2 Implement `PocketDangerZoneCard` in `PocketSettingsCards.kt` matching `:design` component specs and theme tokens, with `@ComponentPreviews`.
- [ ] 3.3 Add `PocketDangerZoneCard` to `PocketSettingsContent` in `PocketSettingsScreen.kt`, conditionally rendered when `!isNewPocket`. Add preview with danger zone card and verify with `PocketSettingsE2eUiTest`.

## 4. Verification Gate

- [ ] 4.1 Run Level 1 (compilation), Level 2 (jvmTest and koverVerify), Level 3 (detektMain), and Level 4 (ktfmtFormat) to ensure all gates pass.
