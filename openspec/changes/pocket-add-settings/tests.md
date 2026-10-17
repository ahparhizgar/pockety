# Tests Specification

## Unit Tests (single component / class / function)

### `pocket_management/PocketSettingsTest`

**Target**: `PocketSettingsComponent`

**Scenarios**:

- Given a `PocketSettingsComponent` initialized in creation mode (`pocketId = null`)
    - When initialized
        - [ ] Then `nameText` is empty, `nameError` is null, and default icon and color are pre-selected
- Given a `PocketSettingsComponent` in creation mode
    - When user changes name text to "Coffee & Dining"
        - [ ] Then `nameText` is updated to "Coffee & Dining" and `nameError` is cleared
- Given a `PocketSettingsComponent` with empty name text
    - When user taps save action
        - [ ] Then `nameError` is set to empty name error message and pocket is not saved
- Given a `PocketSettingsComponent`
    - When user selects an accent color
        - [ ] Then `selectedColor` is updated to the chosen color Long
- Given a `PocketSettingsComponent`
    - When user selects an icon
        - [ ] Then `selectedIcon` is updated to the chosen icon identifier
- Given a `PocketSettingsComponent`
    - When user selects an associated category
        - [ ] Then `selectedCategory` is updated and displayed
- Given a `PocketSettingsComponent` with an associated category
    - When user clears the category association
        - [ ] Then `selectedCategory` becomes null
- Given a `PocketSettingsComponent` with balance of 100,000
    - When user inputs 50,000 in top-up amount and confirms
        - [ ] Then balance updates to 150,000 and top-up input field clears
- Given a `PocketSettingsComponent` with valid name, icon, color, and budget
    - When user taps save action
        - [ ] Then pocket is saved in repository and `onCompleted` callback is invoked

### `pocket_management/PocketRepositoryTest`

**Target**: `PocketRepository` and Room DAO with schema v7

**Scenarios**:

- Given an in-memory database with schema v7
    - When adding a pocket with name, icon, color, category, and budget
        - [ ] Then the pocket is persisted and observable via `observePockets()` with matching properties
- Given an existing pocket in the repository
    - When fetching pocket by id
        - [ ] Then the retrieved entity contains the saved color, categoryId, and metadata

### `db/MigrationTest`

**Target**: Database migration `MIGRATION_6_7`

**Scenarios**:

- Given a SQLite database at schema version 6 containing pockets
    - When migrating from version 6 to version 7
        - [ ] Then `color` and `categoryId` columns are successfully added to `pockets` table with default color values preserved

## E2E Flow Tests

### `pocket_management/PocketSettingsFlowE2eTest`

**Flow**: Navigation and integration flow from root component into pocket settings and category selection subscreens

**Scenarios**:

- Given app on Main screen at Pockets tab
    - When user taps Add Pocket floating action button
        - [ ] Then active child in RootComponent is `PocketSettingsFlowComponent` with `PocketSettingsComponent` active
    - And user taps Change Category on pocket settings
        - [ ] Then active child in `PocketSettingsFlowComponent` is `CategorySelectionComponent`
    - And user selects a category from the category list
        - [ ] Then `CategorySelectionComponent` is popped, returning to `PocketSettingsComponent` with the selected category populated
    - And user enters valid pocket name and taps Save Changes
        - [ ] Then `PocketSettingsFlowComponent` is dismissed, returning to `PocketsScreen` with the new pocket visible in the list

- Given app in `PocketSettingsFlowComponent`
    - When user taps back navigation button
        - [ ] Then `PocketSettingsFlowComponent` is dismissed, returning to `PocketsScreen` without creating a pocket

## UI & Accessibility Tests

### `pocket_management.PocketSettingsE2eUiTest`

**Scenarios**:

1. User opens pocket settings, navigates fields via keyboard actions, enters a valid pocket name, and saves successfully using keyboard Done action.
2. User submits empty pocket name and observes validation error message on the name field.
3. User selects different accent color swatches and verifies visual selection checkmark and preview tint update.
4. User filters icons using category filter chips and selects an icon from the grid.
5. User enters custom amount in manual top-up field and triggers confirm top-up button, verifying updated displayed balance.
