# Proposal

## Why

Currently, pocket top-ups mutate a single `budgetAmount` column on `pockets` directly, and `PocketDetailsScreen` displays hardcoded dummy transactions. As a result, users cannot audit where pocket balance changes came from, see actual spending history for a pocket, or track top-up deposits over time.

By replacing the mutable `budgetAmount` column with a dedicated `pocket_top_ups` transaction table, initial pocket allocations and subsequent top-ups become auditable transactions with amounts, timestamps, and optional descriptions. In turn, `PocketDetailsScreen` can display real linked expenses alongside top-up transactions, with dynamic balance calculations derived from top-up deposits minus expenses.

## What Changes

- **Top-up Entity & Schema (Room v7 -> v8)**:
  - Add `PocketTopUpEntity` and table `pocket_top_ups` with columns `id`, `pocketId` (FK to `pockets.id` with `ON DELETE CASCADE`), `amount`, `description` (nullable), and `createdAt` (instant).
  - Remove `budgetAmount` from `pockets` table and `PocketEntity`.
  - Migrate existing data: for each existing pocket row, insert an initial top-up into `pocket_top_ups` with `amount = pockets.budgetAmount`, `description = 'Initial Balance'`, and `createdAt = pockets.createdAt`.
- **Pocket Creation & Top-up Logic**:
  - `PocketRepository.addPocket`: accepts an initial amount, creates the pocket, and atomically inserts the initial top-up transaction.
  - `PocketRepository.topUpPocket`: records a new `PocketTopUpEntity` record (with `amount` and optional `description`) instead of updating a column.
- **Combined Pocket Expenses & Top-ups Feed**:
  - Query linked expenses (`transactions WHERE pocketId = :pocketId`) and top-ups (`pocket_top_ups WHERE pocketId = :pocketId`).
  - `PocketDetailsComponent` combines both feeds into a unified list of transactions sorted descending by `createdAt`, showing real expense items (negative amount) and top-up deposits (positive amount with indicator).
  - Pocket balance is computed dynamically as `SUM(top_ups) - SUM(expenses)`.
- **Top-up UI Integrity**:
  - The quick top-up bottom sheet UI remains unchanged (amount input and preset chips), invoking the updated top-up repository logic.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `pocket-management`: Update pocket balance calculation to be derived from `SUM(top-ups) - SUM(expenses)` rather than a fixed budget column, add auditable top-up records with optional descriptions, and display real linked expenses and top-up transactions in the pocket details feed.

## Impact

- Database: Room schema upgraded from version 7 to 8 (`AppDatabase`, `MIGRATION_7_8`).
- DAOs and Repositories: `PocketDao`, `PocketEntity`, `DefaultPocketRepository`, `FakePocketRepository`, and queries observing pocket balance and transactions.
- Components & UI: `PocketDetailsComponent`, `PocketDetailsState`, `PocketSettingsComponent` (when creating/editing pocket initial amount), and corresponding preview factories.
- Tests: Unit, flow, and UI tests for pocket creation, top-ups, pocket details, and migrations.
