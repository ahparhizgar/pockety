# Tests Specification

## Unit Tests (single component / class / function)

### `expense/AddExpenseTest`

**Target**: `com.base.feature.expense.add.AddExpenseComponent`

**Scenarios**:

- Given `AddExpenseComponent`
    - When submitting with valid amount and empty description
        - [ ] Then saves expense with empty description and calls `onCompleted` without errors
    - When submitting with valid amount and whitespace description
        - [ ] Then saves expense with trimmed empty description without validation errors
    - When submitting with empty amount and empty description
        - [ ] Then shows amount validation error only and `descriptionError` remains null
    - When editing an existing expense and clearing description
        - [ ] Then updates expense successfully with empty description

### `analytics/AnalyticsComponentTest` or `analytics/AnalyticsScreenTest`

**Target**: `AnalyticsExpenseItem` composable / `AnalyticsComponent`

**Scenarios**:

- Given an expense item with amount, category, and non-empty description
    - When rendered in `AnalyticsExpenseItem`
        - [ ] Then displays the description text and category avatar
- Given an expense item with amount, category, and empty description
    - When rendered in `AnalyticsExpenseItem`
        - [ ] Then displays the category name as title
- Given an expense item with amount, no category, and empty description
    - When rendered in `AnalyticsExpenseItem`
        - [ ] Then does not display the description title node, displaying only the amount and time

## E2E Flow Tests

### `expense/AddExpenseFlowE2eTest`

**Flow**: Add expense without description through `RootComponent` to `AnalyticsComponent`

**Scenarios**:

- Given Home screen with active user session
    - When user opens Add Expense flow, enters amount "25000", leaves description empty, and saves
        - [ ] Then expense is saved to repository with amount 25000 and empty description
        - And navigation returns to home / analytics view showing the recorded expense

## UI & Accessibility Tests

### `expense/AddViewExpenseE2eUiTest`

**Scenarios**:

1. User opens `AddExpenseScreen`, enters valid amount, leaves description blank, and clicks Save button; verify form submits successfully without showing `description-error`.
2. Expense item with neither category nor description renders in the list; verify `expense-description` tag is not present and `expense-amount` tag is displayed with the formatted amount.
