# Tasks

## 1. Database Schema & Migration (v6 -> v7)

- [ ] 1.1 Implement `MIGRATION_6_7` in `AppDatabase.kt` adding `color` and `categoryId` columns to `pockets` table, increment database version to 7, and verify with Room migration test.
- [ ] 1.2 Update `PocketEntity`, `PocketWithSpent`, `PocketItemModel`, `PocketDao`, and `PocketRepository` (including fake implementations) to support `color` and `categoryId`, verifying with `PocketRepositoryTest`.

## 2. Decompose Component & Flow Architecture

- [ ] 2.1 Implement `PocketSettingsComponent` (interface, state, default and fake implementations, and factory) with name validation, color/icon selection, category linking, and balance top-up, verifying with `PocketSettingsTest`.
- [ ] 2.2 Implement `PocketSettingsFlowComponent` with child navigation for `PocketSettings`, `CategorySelection`, and `AddCategory`, registering factories in Koin `DecomposeModule`.
- [ ] 2.3 Wire `PocketSettingsFlowComponent` into `RootComponent` and connect `PocketsScreen` FAB in `MainComponent` to launch the flow in creation mode, verifying with `PocketSettingsFlowE2eTest`.

## 3. UI Implementation (`PocketSettingsScreen`)

- [ ] 3.1 Implement `PocketSettingsScreen` in `app/shared` adapting `:design` (identity section, accent color picker, categorized icon picker, associated category card, manual top-up card, sticky save button), string resources, and component previews.
- [ ] 3.2 Update `PocketBudgetCard` to use the pocket's persisted accent color for its icon badge container.
- [ ] 3.3 Implement `PocketSettingsE2eUiTest` verifying keyboard navigation, name validation errors, visual options selection, and balance updates.

## 4. Verification & Quality Gates

- [ ] 4.1 Run full verification gates (`compileKotlin`, `jvmTest`, `detektMain`, `ktfmtFormat`) and verify clean status.
