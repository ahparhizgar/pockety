# Tests Specification

## Unit Tests (single component / class / function)

### `expense/analytics/AnalyticsTest`

**Target**: `AnalyticsComponent` (month selection, outflow calculation, date grouping, item click)

**Scenarios**:

- Given `AnalyticsComponent` with expenses spanning multiple months
    - When initialized on the current calendar month
        - [ ] Then `canNavigateNext` is false
        - [ ] Then only expenses recorded in the current month are displayed
        - [ ] Then `monthlyOutflow` equals the sum of expenses in the current month
        - [ ] Then expenses are grouped by date in reverse-chronological order with date group headers and daily sums
    - When user triggers previous month navigation
        - [ ] Then selected month decrements to the preceding month
        - [ ] Then `canNavigateNext` becomes true
        - [ ] Then `monthlyOutflow` updates to the sum of expenses for that preceding month
        - [ ] Then the transaction list displays only expenses for that preceding month
    - When user clicks an expense item
        - [ ] Then `onExpenseClick` callback is invoked with the clicked expense ID

### `expense/add/AddExpenseTest`

**Target**: `AddExpenseComponent` (edit mode pre-population, updating, and TopAppBar deletion)

**Scenarios**:

- Given existing expense in repository
    - When `AddExpenseComponent` is created with that `expenseId`
        - [ ] Then `isEditMode` is true
        - [ ] Then `amountText`, `descriptionText`, `selectedCategory`, and `selectedPocket` are pre-populated with existing expense data
    - When user modifies amount and description and clicks Save
        - [ ] Then `updateExpense` is called on repository with modified fields
        - [ ] Then `onCompleted` is invoked
    - When user clicks Delete action
        - [ ] Then `deleteExpense` is called on repository with that expense ID
        - [ ] Then `onCompleted` is invoked
- Given `AddExpenseComponent` created in add mode (`expenseId == null`)
    - When component is initialized
        - [ ] Then `isEditMode` is false
        - [ ] Then delete action is unavailable

### `home/HomeTest`

**Target**: `HomeComponent` (expense list removal)

**Scenarios**:

- Given application with recorded expenses
    - When `HomeComponent` is initialized
        - [ ] Then HomeState does not observe or expose an expense list
        - [ ] Then SMS permission or undecided banners continue to function normally

## E2E Flow Tests

### `expense/AddViewExpenseFlowE2eTest`

**Flow**: Navigation from RootComponent across Main tabs, adding expense, viewing in Analyse tab, editing, and deleting

**Scenarios**:

- Given user launches application on Main tab
    - When user adds an expense with amount 320,000 and description "Coffee" from Home FAB
        - [ ] Then expense is saved and user returns to Main screen
    - When user selects the Analytics tab
        - [ ] Then the Analyze screen displays the month switcher card with the current month and outflow 320,000 T
        - [ ] Then the transaction list displays the expense grouped under today's date
    - When user taps the expense item in the Analyze tab
        - [ ] Then Root navigation pushes `AddExpenseFlow` in edit mode with pre-filled amount "320000" and description "Coffee"
    - When user changes description to "Latte & Croissant" and saves
        - [ ] Then user returns to Analyze screen and the expense item reflects "Latte & Croissant"
    - When user taps the expense item again and clicks Delete in TopAppBar
        - [ ] Then expense is deleted and user returns to Analyze screen showing an empty month state and 0 T outflow

## Repository & Data Tests

### `expense/ExpenseRepositoryTest`

**Target**: `ExpenseRepository` and `ExpenseDao` (CRUD operations)

**Scenarios**:

- Given `ExpenseRepository` initialized with database
    - When `addExpense` is invoked
        - [ ] Then a new expense entity is inserted with auto-generated ID
    - When `getExpenseById` is called with that ID
        - [ ] Then the matching `ExpenseEntity` is returned
    - When `updateExpense` is called with modified description and amount
        - [ ] Then `getExpenseById` and `observeExpenses` return the updated values
    - When `deleteExpense` is called with that ID
        - [ ] Then `getExpenseById` returns null and `observeExpenses` no longer contains the expense

## UI & Accessibility Tests

### `expense/AddViewExpenseE2eUiTest`

**Scenarios**:

1. **Add and verify on Analyze tab with keyboard navigation**:
   - Launch app, trigger "Add Expense" FAB.
   - Enter amount and description using keyboard, submit with Enter.
   - Click "Analytics" tab in bottom navigation bar.
   - Verify `analytics-screen`, month card, date group header, and expense item exist.
2. **Edit existing expense via item click**:
   - Click expense item in `analytics-screen`.
   - Verify `add-expense-screen` opens with "Edit Expense" title and Delete icon in TopAppBar.
   - Update description and save.
   - Verify updated description appears in the Analyze tab.
3. **Delete expense via TopAppBar action**:
   - Click expense item in `analytics-screen`.
   - Click Delete action icon in TopAppBar.
   - Verify returning to Analyze tab and empty state is displayed.
