# Tasks

## 1. Remove Authentication & Bypass Login

- [x] 1.1 Update `DefaultRootComponent` to route directly to `MainComponent.Config` on startup without checking `TokenStorage`, and verify root component initializes to `MainComponent`.
- [x] 1.2 Remove `AuthComponent`, auth screens, and associated auth dependencies/routes from `app/shared`.

## 2. Local Expense Data Layer

- [x] 2.1 Create `ExpenseEntity`, `ExpenseDao`, and `ExpenseRepository` (with `FakeExpenseFactory` in the same file) supporting insert and observe operations.
- [x] 2.2 Register `ExpenseEntity` and `ExpenseDao` in `AppDatabase`, update database version, and verify database builds and passes unit tests.

## 3. Expense Decompose Components & UI

- [x] 3.1 Implement `AddExpenseComponent` (with `AddExpenseState`, form validation for positive amount and non-empty description, returning `Job`, and Value state) along with `FakeAddExpenseComponent`.
- [x] 3.2 Implement `AddExpenseTest` to verify form validations, empty/zero amount errors, and successful submission.
- [x] 3.3 Create `AddExpenseScreen` composable with keyboard options and `@ScreenPreviews`.
- [x] 3.4 Update `HomeScreen` and its component to display the list of recorded expenses with an "Add Expense" action/FAB.

## 4. End-to-End Integration & Verification

- [x] 4.1 Implement `AddViewExpenseFlowE2eTest` combining adding an expense and verifying its appearance in the expense list through root/main navigation without relying on internal database details.
- [x] 4.2 Implement `AddViewExpenseE2eUiTest` using keyboard actions to verify the end-to-end user experience and accessibility.
- [x] 4.3 Run verification gates (compilation, jvmTest, detekt, ktfmt) to ensure code meets all quality standards.
