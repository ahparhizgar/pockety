# Tests Specification

## Unit Tests (single component / class / function)

### `pocket/settings/PocketSettingsTest`

**Target**: `com.base.feature.pocket.settings.PocketSettingsComponent`

**Scenarios**:

- Given a `PocketSettingsComponent` initialized for a new pocket
    - When user enables auto-refill and sets monthly allocation amount to 300,000 Tomans
        - [ ] Then component state updates with `autoRefillEnabled = true` and `monthlyAllocationAmount = 300000`
    - When user clicks save on valid pocket
        - [ ] Then pocket repository saves pocket with monthly allocation settings preserved
- Given a `PocketSettingsComponent` initialized for an existing pocket with auto-refill enabled
    - When component loads
        - [ ] Then state displays existing monthly top-up amount and toggle enabled
    - When user toggles off auto-refill and saves
        - [ ] Then pocket repository updates pocket with `autoRefillEnabled = false`

## E2E Flow Tests

### `pocket/PocketMonthlyTopUpFlowE2eTest`

**Flow**: On-app-open monthly top-up execution via `RootComponent`

**Scenarios**:

- Given a pocket with auto-refill enabled, monthly amount 500,000 T, and `lastTopUpMonth` as previous month
    - When application launches (`RootComponent` initializes)
        - [ ] Then a top-up transaction of 500,000 T is recorded for the pocket
        - And the pocket's balance increases by 500,000 T
        - And the pocket's `lastTopUpMonth` is updated to current month
- Given a pocket with auto-refill enabled whose `lastTopUpMonth` is already the current month
    - When application launches (`RootComponent` initializes)
        - [ ] Then no additional top-up transaction is created
        - And the pocket's balance remains unchanged
- Given a pocket with auto-refill disabled
    - When application launches in a new month
        - [ ] Then no top-up transaction is generated for this pocket

## Repository & Data Tests

### `pocket/PocketRepositoryTest`

**Target**: `PocketRepository` and Room DAO

**Scenarios**:

- Given a database with active pockets
    - When `processMonthlyTopUps()` is called for the current month
        - [ ] Then due pockets receive top-up transaction entries and updated `lastTopUpMonth`
    - When `processMonthlyTopUps()` is called a second time in the same month
        - [ ] Then no duplicate top-up records are inserted

### `db/MigrationTest`

**Target**: `MIGRATION_8_9` in `AppDatabase`

**Scenarios**:

- Given database at version 8 with existing pocket rows
    - When migrating from 8 to 9
        - [ ] Then `pockets` table includes `monthlyTopUpAmount`, `autoTopUpEnabled`, and `lastTopUpMonth` columns
        - And existing pocket rows retain their data with default values

## UI & Accessibility Tests

### `pocket/settings/PocketSettingsE2eUiTest`

**Scenarios**:

1. User navigates to pocket settings screen, locates monthly allocation card, and toggles auto-refill switch; verify switch state updates properly via keyboard and click.
2. User clicks adjust allocation button in monthly allocation card, enters a new amount with keyboard, and confirms; verify displayed allocation amount updates in the UI.
