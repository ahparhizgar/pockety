# Proposal

## Why

Users need visibility into their budget pockets and their current financial balances from the bottom navigation bar. Implementing the pocket list screen provides users with an overview of their budget pockets, balances, and empty states, laying the groundwork for budget management without waiting for the full pocket creation flow.

## What Changes

- Introduce a new Room `pockets` table and database migration (v5 -> v6) with a nullable foreign key linking transactions (`pocketId` on `transactions`).
- Persist pocket budget amount in database and compute the remaining balance as `budgetAmount - SUM(linked expenses)`.
- Introduce `PocketRepository` and Room DAO with queries observing pocket entities and calculating balance.
- Create `PocketsComponent` (Decompose) wired into `MainComponent` under the existing `PocketyTab.Pockets` bottom-bar tab.
- Implement `PocketsScreen` in `app/shared`:
  - Empty state when no pockets are present.
  - List of budget pockets using `PocketBudgetCard` displaying category, name, formatted remaining balance, and over-budget status.
  - Card interaction is a no-op placeholder for navigating to pocket details (pocket details out of scope).
  - "Top-up", "Settings", and "History" actions are either omitted or non-functional.
  - "Add Pocket" Extended FAB rendered as a no-op placeholder (pocket creation out of scope).
- Complete test suite for migrations, component state/navigation, and UI.

## Capabilities

### New Capabilities
- `pocket-management`: Covers viewing budget pockets, tracking pocket balances against linked expenses, and empty states.

### Modified Capabilities
<!-- None: existing expense management requirements remain unchanged, only a nullable foreign key is added to the database table -->

## Impact

- Database schema updated to version 6 with `MIGRATION_5_6`.
- `transactions` table gains nullable `pocketId` foreign key referencing `pockets(id)`.
- `MainComponent` and `MainScreen` wire real `PocketsComponent` and `PocketsScreen` instead of the empty placeholder box.
