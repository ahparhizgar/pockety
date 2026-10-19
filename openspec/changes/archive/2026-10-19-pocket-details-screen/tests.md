# Tests Specification

## Unit Tests (single component / class / function)

### `pocket_management/PocketDetailsTest`

**Target**: `PocketDetailsComponent` / `DefaultPocketDetailsComponent`

**Scenarios**:

- Given a `PocketDetailsComponent` initialized with an existing pocket
    - When component is created
        - [ ] Then state loads pocket name, initial balance, icon, and accent color from repository
    - When user taps the Top-up action
        - [ ] Then `isQuickTopUpVisible` is updated to true
    - When user dismisses the quick top-up sheet
        - [ ] Then `isQuickTopUpVisible` is updated to false and balance remains unchanged
    - When user selects a preset quick top-up amount chip (+100,000 T) and confirms
        - [ ] Then pocket balance in repository increases by 100,000 T, the sheet closes, and confirmation message is displayed
    - When user enters a custom top-up amount ("250000") and confirms
        - [ ] Then pocket balance in repository increases by 250,000 T, the sheet closes, and confirmation message is displayed
    - When user enters an invalid or empty custom top-up amount
        - [ ] Then confirmation is disabled or ignored and balance remains unchanged
    - When user taps the Configure action or settings icon
        - [ ] Then `onConfigure` callback is invoked with the current pocket ID
    - When user taps the Back action
        - [ ] Then `onBack` callback is invoked
    - When user deletes a mock transaction in the UI layer
        - [ ] Then the transaction item is removed from the UI list and feedback message is displayed

## E2E Flow Tests

### `pocket_management/PocketDetailsFlowE2eTest`

**Flow**: Navigation and integration flow from Root navigation through Pockets and Pocket Details

**Scenarios**:

- Given application with existing pockets on the Pockets tab
    - When user clicks a pocket card in Pockets list
        - [ ] Then active child in RootComponent becomes `PocketDetailsComponent` displaying that pocket
    - And user opens quick top-up and confirms an addition of 100,000 T
        - [ ] Then pocket balance in `PocketDetailsComponent` and underlying database reflects the new total
    - And user taps Configure
        - [ ] Then RootComponent pushes `PocketSettingsFlowComponent` with `pocketId` in edit mode
    - And user presses back from pocket settings
        - [ ] Then RootComponent returns to `PocketDetailsComponent`
    - And user presses back from pocket details
        - [ ] Then RootComponent returns to `MainComponent` on `Pockets` tab with updated pocket balance visible in pocket card

## UI & Accessibility Tests

### `pocket_management/PocketDetailsE2eUiTest`

**Scenarios**:

1. **Pocket Details Visual & Keyboard Accessibility**:
   - Verify pocket title, balance formatted in Tomans (`T`), action buttons ("Top-up", "Configure"), and transaction rows are visible.
   - Verify keyboard navigation can focus each interactive element (back button, action buttons, configure button).
2. **Quick Top-Up Modal Bottom Sheet Interaction**:
   - Verify clicking "Top-up" presents the modal bottom sheet without a drag handle.
   - Verify preset chips (+50,000 T, +100,000 T, +200,000 T, +500,000 T) and custom amount text field with `keyboardOptions(keyboardType = KeyboardType.Number)`.
   - Verify submitting top-up dismisses sheet and displays confirmation banner.

## Repository / Data Tests

### `pocket_management/PocketRepositoryTest`

**Target**: `PocketDao` & `PocketRepository`

**Scenarios**:

- Given a persistent Room database with an existing pocket
    - When `topUpPocket(id, amount)` is executed
        - [ ] Then the pocket's `budgetAmount` in the database is incremented by the specified amount
        - [ ] Then `observePockets()` emits an updated `PocketItemModel` reflecting the new balance
