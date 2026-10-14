# Proposal: Add and View Transaction

## Why

Pockety needs to start functioning as a minimal, working expense tracker without requiring remote backend authentication. Removing login/auth and introducing a minimal local-first vertical slice for adding and viewing transactions enables an agile, usable product at any given time.

## What Changes

- **Remove Auth Flow**: Bypass login, sign up, and token checks so the application launches straight into the main application.
- **Add Minimal Transaction Entry**: Allow users to record an expense transaction with only an amount and a description. Form validation will guard against invalid entries (e.g. zero or empty amounts).
- **View Transactions**: Display recorded transactions directly in the `HomeScreen` in reverse-chronological order.
- **Local Persistence**: Persist transactions locally in Room database so transactions remain available across app launches without any backend.
- **End-to-End & Component Testing**: Add combined integration tests for adding and viewing transactions end-to-end (independent of db implementation details), plus an `AddTransactionComponentTest` to verify form validation and error states.

## Capabilities

### New Capabilities
- `transaction-management`: Adding and viewing basic financial transactions (amount and description) locally.

### Modified Capabilities
<!-- No existing capabilities modified -->

## Impact

- `RootComponent` & `RootScreen`: Launches directly into `MainComponent`, bypassing auth checks.
- `app/shared`: Removes `AuthComponent`.
- Database: Adds `TransactionEntity` and `TransactionDao` to `AppDatabase`.
- UI: Introduces minimal `HomeScreen` transaction feed and `AddTransactionScreen` (adapted from `:design` components).
- Implementing `AddTransactionComponent` Decompose component.
- Tests: Adds unified E2E integration test for adding and viewing transactions, E2E UI test (keyboard friendly), and component tests for add-transaction form validation.
