# Tasks

## 1. Database Schema & Migration (v7 to v8)

- [ ] 1.1 Define `PocketTopUpEntity` in `Pocket.kt` with fields `id`, `pocketId`, `amount`, `description`, `createdAt`, with foreign key on `PocketEntity(id)` (`CASCADE`) and index on `pocketId`; verify compilation with `./gradlew compileKotlinJvm`
- [ ] 1.2 Remove `budgetAmount` from `PocketEntity` in `Pocket.kt`, create `MIGRATION_7_8` in `AppDatabase.kt` and bump database version to 8; verify with `MigrationTest.kt`
- [ ] 1.3 Add test scenario in `MigrationTest.kt` validating that existing pocket `budgetAmount` rows are migrated into `pocket_top_ups` with `Initial Balance` description and `pockets` table structure has `budgetAmount` dropped; verify with `./gradlew jvmTest --tests "com.base.db.MigrationTest"`

## 2. Pocket DAO & Repository Top-up Support

- [ ] 2.1 Update `PocketDao` queries: add `insertTopUp`, `observePocketTopUps(pocketId: Long)`, and update `observePocketsWithSpent` / balance calculation to derive remaining balance from `SUM(top_ups) - SUM(expenses)`; verify with `PocketRepositoryTest`
- [ ] 2.2 Update `DefaultPocketRepository`: update `addPocket` to atomically insert an initial top-up transaction, update `topUpPocket` to insert a new `PocketTopUpEntity`, and expose observation of pocket top-ups and expenses; verify with `PocketRepositoryTest`
- [ ] 2.3 Update `FakePocketRepository` and test fake factories to support top-ups and dynamic balance calculation; verify with `./gradlew jvmTest --tests "com.base.capability.pocket_management.PocketRepositoryTest"`

## 3. Pocket Details Component Combined Feed

- [ ] 3.1 Update `PocketDetailsComponent` and `DefaultPocketDetailsComponent` to reactively observe linked expenses and pocket top-ups, dynamically calculate remaining balance, and merge both into `state.transactions` sorted descending by `createdAt`; verify with `PocketDetailsTest`
- [ ] 3.2 Update `onConfirmQuickTopUp()` in `PocketDetailsComponent` to record a top-up transaction through `PocketRepository.topUpPocket(pocketId, amount)` and update confirmation feedback; verify with `PocketDetailsTest`
- [ ] 3.3 Verify and update `FakePocketDetailsComponent` and preview state factories to match new data structures and previews; verify with `./gradlew compileKotlinJvm`

## 4. End-to-End Verification & Quality Gates

- [ ] 4.1 Update and run flow tests in `PocketDetailsFlowE2eTest` to verify navigating to pocket details, viewing real transactions, and performing a quick top-up; verify with `./gradlew jvmTest --tests "com.base.capability.pocket_management.PocketDetailsFlowE2eTest"`
- [ ] 4.2 Update and run UI tests in `PocketDetailsE2eUiTest` ensuring combined transaction list display, empty state, and quick top-up sheet keyboard accessibility; verify with `./gradlew jvmTest --tests "com.base.capability.pocket_management.PocketDetailsE2eUiTest"`
- [ ] 4.3 Run Level 1 (compilation), Level 2 (jvmTest & test), Level 3 (detekt), and Level 4 (ktfmt) verification gates per project guidelines
