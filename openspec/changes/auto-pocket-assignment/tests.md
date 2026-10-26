# Tests Specification

## Unit Tests (single component / class / function)

### `expense/add/AddExpenseTest`

**Target**: `DefaultAddExpenseComponent`

**Scenarios**:

- Given `AddExpenseComponent` with pockets in `PocketRepository` (including Pocket A linked to Category 10)
    - When selecting Category 10 (`onCategorySelected`),
        - [ ] Then `state.selectedPocket` is automatically assigned to Pocket A
    - When selecting a category with no linked pocket (`onCategorySelected`),
        - [ ] Then `state.selectedPocket` remains null
    - When a pocket is assigned and user taps unlink (`onPocketCleared`),
        - [ ] Then `state.selectedPocket` is updated to null and `selectedCategory` remains unchanged
    - When a pocket is auto-assigned and user manually selects another pocket (`onPocketSelected`),
        - [ ] Then `state.selectedPocket` is updated to the manually selected pocket
    - When saving an expense with an assigned pocket (`onSaveClicked`),
        - [ ] Then `expenseRepository.addExpense` is called with the expense's `pocketId`
    - When saving an expense with unlinked pocket (`onSaveClicked`),
        - [ ] Then `expenseRepository.addExpense` is called with `pocketId = null`

### `pocket/selection/SelectPocketTest`

**Target**: `DefaultSelectPocketComponent`

**Scenarios**:

- Given `SelectPocketComponent` initialized with available budget pockets
    - When searching with query "Coffee",
        - [ ] Then filtered list displays only pockets matching "Coffee" by name or category tag
    - When selecting a pocket item,
        - [ ] Then `onPocketSelected` callback is invoked with the chosen pocket
    - When selecting the "No Pocket" option,
        - [ ] Then `onPocketSelected` callback is invoked with `null`
    - When tapping back,
        - [ ] Then `onBack` callback is invoked

## E2E Flow Tests

### `expense/AddExpenseFlowE2eTest`

**Flow**: Navigation and pocket integration within `AddExpenseFlowComponent`

**Scenarios**:

- Given a pocket "Coffee Pocket" linked to category "Cafe", and user is on `AddExpense`
    - When user navigates to `CategorySelection` and selects "Cafe",
        - [ ] Then flow returns to `AddExpense` and `selectedPocket` is auto-assigned to "Coffee Pocket"
        - And user enters valid amount, description, and saves,
            - [ ] Then expense is recorded with "Coffee Pocket" ID and pocket's remaining balance is deducted
- Given user on `AddExpense` with auto-assigned "Coffee Pocket"
    - When user taps the pocket card,
        - [ ] Then flow navigates to `PocketSelection` child
        - And user selects a different pocket "General Pocket",
            - [ ] Then flow returns to `AddExpense` displaying "General Pocket"
            - And user saves the expense,
                - [ ] Then expense is recorded with "General Pocket" ID
- Given user on `AddExpense` with auto-assigned "Coffee Pocket"
    - When user clicks unlink on the pocket card,
        - [ ] Then pocket card transitions to unlinked state while category "Cafe" remains selected
        - And user saves the expense,
            - [ ] Then expense is saved with `pocketId = null` and no pocket balance is altered

## UI & Accessibility Tests

### `expense/AddViewExpenseE2eUiTest`

**Scenarios**:

1. **Pocket Card Rendering & Unlink Interaction**:
   - Render `AddExpenseScreen` with an assigned pocket.
   - Verify pocket title, balance, and close/unlink button are displayed with accessible semantics.
   - Click unlink button via test tag / semantics and verify card switches to "Tap to link a pocket".
2. **Keyboard Accessibility**:
   - Traverse input focus using keyboard ImeActions and Tab navigation through amount field, category card, pocket card, description field, and save action.
   - Verify pocket selector card and unlink button are keyboard-focusable and activate with keyboard click/enter.
