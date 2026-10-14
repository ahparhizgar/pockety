# Tasks

## 1. Remove Authentication & Bypass Login

- [x] 1.1 Update `DefaultRootComponent` to route directly to `MainComponent.Config` on startup without checking `TokenStorage`, and verify root component initializes to `MainComponent`.
- [x] 1.2 Remove `AuthComponent`, auth screens, and associated auth dependencies/routes from `app/shared`.

## 2. Local Transaction Data Layer

- [x] 2.1 Create `TransactionEntity`, `TransactionDao`, and `TransactionRepository` (with `FakeTransactionFactory` in the same file) supporting insert and observe operations.
- [x] 2.2 Register `TransactionEntity` and `TransactionDao` in `AppDatabase`, update database version, and verify database builds and passes unit tests.

## 3. Transaction Decompose Components & UI

- [x] 3.1 Implement `AddTransactionComponent` (with `AddTransactionState`, form validation for positive amount and non-empty description, returning `Job`, and Value state) along with `FakeAddTransactionComponent`.
- [x] 3.2 Implement `AddTransactionComponentTest` to verify form validations, empty/zero amount errors, and successful submission.
- [x] 3.3 Create `AddTransactionScreen` composable with keyboard options and `@ScreenPreviews`.
- [x] 3.4 Update `HomeScreen` and its component to display the list of recorded transactions with an "Add Transaction" action/FAB.

## 4. End-to-End Integration & Verification

- [x] 4.1 Implement `AddViewTransactionFlowE2eTest` combining adding a transaction and verifying its appearance in the transaction list through root/main navigation without relying on internal database details.
- [x] 4.2 Implement `AddViewTransactionE2eUiTest` using keyboard actions to verify the end-to-end user experience and accessibility.
- [x] 4.3 Run verification gates (compilation, jvmTest, detekt, ktfmt) to ensure code meets all quality standards.
