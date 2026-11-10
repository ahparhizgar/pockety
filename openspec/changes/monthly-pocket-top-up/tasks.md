# Tasks

## 1. Data Layer & Migration

- [x] 1.1 Update `PocketEntity` to include `monthlyTopUpAmount: Long = 0L`, `autoTopUpEnabled: Boolean = false`, and `lastTopUpMonth: String? = null`, bump Room database version to 9, implement `MIGRATION_8_9` in `AppDatabase.kt`, and verify migration in `MigrationTest.kt`.
- [x] 1.2 Update `PocketDao` and `PocketRepository` (including `DefaultPocketRepository` and `FakePocketRepository`) to support saving monthly top-up settings and executing `processMonthlyTopUps(currentYearMonth: String): List<PocketTopUpEntity>`, verifying with `PocketRepositoryTest.kt`.

## 2. Pocket Settings UI & Component

- [x] 2.1 Update `PocketSettingsState`, `PocketSettingsComponent`, and `DefaultPocketSettingsComponent` to handle `monthlyTopUpAmount` and `autoTopUpEnabled`, verifying with `PocketSettingsTest.kt`.
- [x] 2.2 Implement `PocketMonthlyAllocationCard` in `PocketSettingsCards.kt` (with amount display, auto-refill switch, adjust allocation trigger/dialog, and `@ComponentPreviews`) and integrate it into `PocketSettingsScreen.kt`.
- [x] 2.3 Verify UI interactions with `PocketSettingsE2eUiTest.kt`.

## 3. On-App-Open Execution & Integration

- [x] 3.1 Invoke monthly top-up processing on app startup in `DefaultRootComponent`, verifying with `PocketMonthlyTopUpFlowE2eTest.kt`.
- [x] 3.2 Run full verification suite (`./gradlew compileKotlinJvm compileKotlin`, `./gradlew jvmTest test`, `./gradlew detektMain`, `./gradlew ktfmtFormat`) to ensure no regressions.

## Workflow follow-up

- Hand off to Developer for implementation.
- Once implementation and CI pass, archive the change with `openspec-archive-change` and rebase linearly onto main.
