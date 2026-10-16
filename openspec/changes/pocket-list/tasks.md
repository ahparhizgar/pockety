# Tasks

## 1. Database Schema & Migration

- [ ] 1.1 Define `PocketEntity`, `PocketDao`, and update `ExpenseEntity` with nullable `pocketId` in `app/shared/src/commonMain/kotlin/com/base/db/Pocket.kt` and `Expense.kt`; verify compilation with `./gradlew compileKotlinJvm`
- [ ] 1.2 Implement `MIGRATION_5_6` and update `AppDatabase` version to 6; verify with `MigrationTest.kt` passing via `./gradlew jvmTest --tests "com.base.db.MigrationTest"`
- [ ] 1.3 Implement `PocketRepository`, `DefaultPocketRepository`, and `FakePocketRepository` computing dynamic balances against transactions; verify repository behavior with unit tests in `app/shared/src/commonTest/kotlin/com/base/capability/pocket_management/PocketRepositoryTest.kt`

## 2. Decompose Component & Navigation

- [ ] 2.1 Implement `PocketsComponent`, `DefaultPocketsComponent`, and `FakePocketsComponent` with `PocketsState` in `app/shared/src/commonMain/kotlin/com/base/capability/pocket_management/PocketsComponent.kt`; verify with `PocketsComponentTest`
- [ ] 2.2 Wire `PocketsComponentFactory` into Koin DI and update `MainComponent` to instantiate `PocketsComponent` when `MainChildConfig.Pockets` is active; verify with `PocketListFlowE2eTest.kt` and `MainNavigationTest.kt`

## 3. UI Implementation & Previews

- [ ] 3.1 Implement `PocketBudgetCard` (without history and settings buttons) and `PocketsScreen` in `app/shared/src/commonMain/kotlin/com/base/capability/pocket_management/PocketsScreen.kt` featuring empty state, budget cards, and "Add Pocket" extended FAB placeholder; add `@ScreenPreviews` and `@ComponentPreviews`
- [ ] 3.2 Update `MainScreen` to render `PocketsScreen` for `MainComponent.Child.Pockets`; verify UI and accessibility with `PocketListE2eUiTest.kt`

## 4. Verification & Polish

- [ ] 4.1 Run full suite test and verification gate: compilation, unit/integration tests, detekt, ktfmt, and kover coverage (`./gradlew jvmTest test koverVerify :detektMain ktfmtFormat`)
