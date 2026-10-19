# Design

## Context

See proposal.md - Why.
The app currently has Room database at version 5 with tables for `transactions`, `candidate_expenses`, and `categories`. `MainComponent` and `MainScreen` manage bottom bar navigation across tabs, where `PocketyTab.Pockets` currently renders a placeholder `Box`.
`:design` module contains `PocketsScreen`, `PocketBudgetCard`, and `DynamicBalanceSyncBanner`, which can be adapted into `app/shared` adhering to the project UI guidelines (Surface/Scaffold, string resources, Decompose components).

## Goals / Non-Goals

**Goals:**
- Add Room entity `PocketEntity`, Dao `PocketDao`, and repository `PocketRepository`.
- Implement database migration from version 5 to 6 (`MIGRATION_5_6`), adding the `pockets` table and adding a nullable foreign key `pocketId` to `transactions`.
- Calculate remaining balance dynamically: `budgetAmount - SUM(linked expenses)`.
- Implement `PocketsComponent` with `Value<PocketsState>`, observing pocket models with calculated balance and over-budget status.
- Implement `PocketsScreen` supporting empty state, pocket budget cards (without history/settings buttons), Top-up button placeholder, and an "Add Pocket" FAB placeholder.
- Provide comprehensive tests: Room migration tests, repository integration tests, component unit tests, and screen previews.

**Non-Goals:**
- Creating, editing, or deleting pockets (out of scope for this slice).
- Linking an expense to a pocket during expense creation (out of scope).
- Pocket detail / ledger navigation (card click is a no-op placeholder).
- Pre-seeding default pockets in database.

## Decisions

### 1. Database Schema & Migration (v5 -> v6)
- **Table `pockets`**:
  - `id`: INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
  - `name`: TEXT NOT NULL
  - `categoryTag`: TEXT NOT NULL (or category label)
  - `budgetAmount`: INTEGER NOT NULL
  - `icon`: TEXT NOT NULL
  - `createdAt`: INTEGER NOT NULL
- **Table `transactions` update**:
  - Add column `pocketId` (INTEGER NULLABLE) referencing `pockets(id)` with `ON DELETE SET NULL`.
  - Add index `index_transactions_pocketId`.
- **Query / Dao**:
  - Define a joined query or data model `PocketWithSpent` / compute balance in DAO using `LEFT JOIN transactions ON transactions.pocketId = pockets.id GROUP BY pockets.id`, returning `spentAmount = COALESCE(SUM(transactions.amount), 0)`.
  - Remaining balance is `budgetAmount - spentAmount`. If `remainingBalance < 0`, `isOverBudget = true`.

*Alternative considered*: Storing running balance column on `pockets`. Rejected because balance sync on expense additions/deletions/edits is dynamic and simpler/less error-prone when calculated from linked transactions.

### 2. Decompose Component Architecture
- `PocketsComponent` interface:
  - `val state: Value<PocketsState>`
  - `fun onPocketClick(pocketId: Long)`
  - `fun onAddPocketClick()`
- `PocketsState(val pockets: List<PocketItemModel> = emptyList(), val isLoading: Boolean = false)`
- `PocketsComponentFactory` registered via Koin.
- Embedded as child in `MainComponent` for `MainChildConfig.Pockets`.

### 3. Screen UI Adaptation
- Port `PocketsScreen` and `PocketBudgetCard` from `:design` to `app/shared`.
- Remove Settings and History buttons from `PocketBudgetCard` as specified.
- Show an empty state when `pockets.isEmpty() && !isLoading` with appropriate message and icon.
- Extended FAB "Add Pocket" is displayed at the bottom right, triggering `onAddPocketClick` (no-op).

## Risks / Trade-offs

- **[Empty Initial Database]** → Without pre-seeding or pocket creation, a fresh install shows the empty state. This is intended for this vertical slice; tests will use test data / `FakePocketRepository` / DAO inserts.
- **[SQLite Migration Compatibility]** → SQLite versions require table reconstruction or standard `ALTER TABLE ... ADD COLUMN`. Migration 5->6 will use standard SQLite commands and be verified using `BundledSQLiteDriver` in `MigrationTest`.
