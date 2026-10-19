# Tests Specification

## Unit Tests (single component / class / function)

### `pocket_management/PocketsComponentTest`

**Target**: `PocketsComponent`

**Scenarios**:

- Given a `PocketRepository` with no pockets
    - When `PocketsComponent` is created
        - [ ] Then `state.value.pockets` is empty and `state.value.isLoading` becomes false
- Given a `PocketRepository` with pockets having no linked expenses
    - When `PocketsComponent` is initialized
        - [ ] Then `state.value.pockets` contains the pockets with remaining balance equal to full budget amount and not over budget
- Given a `PocketRepository` with pockets having expenses exceeding the budget amount
    - When `PocketsComponent` observes repository updates
        - [ ] Then `state.value.pockets` contains the pocket marked with `isOverBudget = true` and over-budget warning message

## Repository / Migration Tests

### `db/MigrationTest`

**Target**: Database migration `MIGRATION_5_6`

**Scenarios**:

- Given SQLite database at version 5 with `transactions` and `categories`
    - When `MIGRATION_5_6` is executed
        - [ ] Then `pockets` table exists with columns `id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`
        - [ ] Then `transactions` table contains nullable `pocketId` column with foreign key to `pockets(id)` and index `index_transactions_pocketId`

### `pocket_management/PocketRepositoryTest`

**Target**: `DefaultPocketRepository` and Room DAO queries

**Scenarios**:

- Given a pocket with budget amount 1000 and linked transactions totaling 400
    - When `observePockets()` is called
        - [ ] Then the emitted item has remaining balance of 600 and `isOverBudget = false`
- Given a pocket with budget amount 1000 and linked transactions totaling 1200
    - When `observePockets()` is called
        - [ ] Then the emitted item has remaining balance of -200 and `isOverBudget = true`

## E2E Flow Tests

### `pocket_management/PocketListFlowE2eTest`

**Flow**: Navigation from Root/Main through bottom bar tabs to Pockets

**Scenarios**:

- Given application on Home tab
    - When user selects the Pockets tab
        - [ ] Then active child in `MainComponent` is `Pockets` with `PocketsComponent` instance
        - [ ] Then pressing back returns to Home tab with empty back stack

## UI & Accessibility Tests

### `pocket_management/PocketListE2eUiTest`

**Scenarios**:

1. When database has no pockets, Pockets screen renders empty state message and testTag `pockets-empty-state`.
2. When database has pockets, Pockets screen renders pocket budget cards with names, formatted amounts, and category tags.
3. Floating action button "Add Pocket" is displayed and accessible via keyboard navigation.
