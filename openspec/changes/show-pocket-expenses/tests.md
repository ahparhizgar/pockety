# Tests Specification

## Unit Tests (single component / class / function)

### `pocket_management/PocketDetailsTest`

**Target**: `PocketDetailsComponent`

**Scenarios**:

- Given a pocket with top-up transactions and linked expenses in the repository
    - When `PocketDetailsComponent` is initialized for that pocket
        - [ ] Then the component state emits the dynamically computed balance: `sum(top-ups) - sum(expenses)`
        - [ ] Then `state.transactions` contains both top-ups and expenses merged in descending chronological order
        - [ ] Then top-up transactions have `isTopUp = true` and positive amounts
        - [ ] Then expense transactions have `isTopUp = false` and negative amounts

- Given `PocketDetailsComponent` initialized with quick top-up bottom sheet opened
    - When user enters a custom amount and clicks confirm top-up
        - [ ] Then the bottom sheet dismisses and state updates with a positive confirmation message
        - [ ] Then the updated balance and new top-up transaction appear in `state.transactions`

- Given `PocketDetailsComponent` displaying linked transactions
    - When user deletes a linked expense transaction
        - [ ] Then the expense is removed from the pocket
        - [ ] Then the pocket balance reflects the restored amount

### `pocket_management/PocketRepositoryTest`

**Target**: `PocketRepository` (`DefaultPocketRepository` & Room DAO)

**Scenarios**:

- Given a newly created pocket with an initial allocation amount
    - When `addPocket` is executed
        - [ ] Then the pocket entity is inserted into `pockets`
        - [ ] Then an initial `PocketTopUpEntity` is recorded with `amount` equal to the allocation
        - [ ] Then `observePockets` emits the pocket with balance equal to the initial allocation

- Given a pocket with an initial balance and existing linked expenses
    - When `topUpPocket` is called with an amount and optional description
        - [ ] Then a new `PocketTopUpEntity` row is inserted in `pocket_top_ups`
        - [ ] Then `observePockets` reflects the increased remaining balance: `initial + topUp - expenses`

- Given a pocket with multiple top-ups and linked expenses
    - When observing top-ups and expenses for that pocket
        - [ ] Then all top-ups for that pocket ID are returned
        - [ ] Then all expenses for that pocket ID are returned

## E2E Flow Tests

### `pocket_management/PocketDetailsFlowE2eTest`

**Flow**: Navigation from RootComponent to PocketDetails, observing live top-up and expense transactions.

**Scenarios**:

- Given RootComponent initialized with a pocket containing linked expenses and an initial top-up
    - When user navigates from Pockets screen to PocketDetailsScreen
        - [ ] Then the pocket details screen displays the real balance and real transaction list
    - And user confirms a quick top-up of 100,000 Tomans
        - [ ] Then the balance immediately increases by 100,000 Tomans
        - [ ] Then a new top-up item appears at the top of the transaction feed

## UI & Accessibility Tests

### `pocket_management.PocketDetailsE2eUiTest`

**Scenarios**:

1. Display real transactions feed: Verify that real expenses (with deduction format `-... T`) and top-ups (with deposit format `+... T`) are rendered on `PocketDetailsScreen`, and an empty placeholder is displayed when no transactions exist.
2. Quick top-up flow: Using keyboard input, enter a top-up amount in `QuickTopUpBottomSheet`, confirm using keyboard action / button click, and verify feedback banner and updated balance.
3. Accessible navigation: Verify all action buttons and transaction rows are keyboard navigable with proper content descriptions.

## Repository & Migration Tests

### `db/MigrationTest`

**Target**: `MIGRATION_7_8` in `AppDatabase`

**Scenarios**:

- Given a v7 database with existing pockets having positive `budgetAmount` values
    - When `MIGRATION_7_8` runs
        - [ ] Then table `pocket_top_ups` is created with foreign key to `pockets(id)` and index on `pocketId`
        - [ ] Then `pockets` table no longer contains `budgetAmount` column
        - [ ] Then initial rows in `pocket_top_ups` exist for each migrated pocket with the corresponding `budgetAmount` as `amount` and `Initial Balance` as description
