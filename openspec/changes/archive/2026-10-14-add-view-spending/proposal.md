# Proposal: Add and View Expense

## Why

Pockety needs to start functioning as a minimal, working expense tracker without requiring remote backend authentication. Removing login/auth and introducing a minimal local-first vertical slice for adding and viewing expenses enables an agile, usable product at any given time.

## What Changes

- **Remove Auth Flow**: Bypass login, sign up, and token checks so the application launches straight into the main application.
- **Add Minimal Expense Entry**: Allow users to record an expense with only an amount and a description. Form validation will guard against invalid entries (e.g. zero or empty amounts).
- **View Expenses**: Display recorded expenses directly in the `HomeScreen` in reverse-chronological order.
- **Local Persistence**: Persist expenses locally in Room database so expenses remain available across app launches without any backend.
- **End-to-End & Component Testing**: Add combined integration tests for adding and viewing expenses end-to-end (independent of db implementation details), plus an `AddExpenseTest` to verify form validation and error states.

## Capabilities

### New Capabilities
- `expense-management`: Adding and viewing basic financial expenses (amount and description) locally.

### Modified Capabilities
<!-- No existing capabilities modified -->

## Impact

- `RootComponent` & `RootScreen`: Launches directly into `MainComponent`, bypassing auth checks.
- `app/shared`: Removes `AuthComponent`.
- Database: Adds `ExpenseEntity` and `ExpenseDao` to `AppDatabase`.
- UI: Introduces minimal `HomeScreen` expense feed and `AddExpenseScreen` (adapted from `:design` components).
- Implementing `AddExpenseComponent` Decompose component.
- Tests: Adds unified E2E integration test for adding and viewing expenses, E2E UI test (keyboard friendly), and component tests for add-expense form validation.
