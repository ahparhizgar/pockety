# Tasks

## 1. Data Layer Enhancements

- [ ] 1.1 Add `getExpenseById(id: Long)`, `updateExpense(expense: ExpenseEntity)`, and `deleteExpense(id: Long)` to `ExpenseDao` and `ExpenseRepository` (along with `DefaultExpenseRepository`); verify with `ExpenseRepositoryTest`.
- [ ] 1.2 Update `FakeExpenseRepository` and `FakeExpenseFactory` to support `getExpenseById`, `updateExpense`, and `deleteExpense`.

## 2. Expense View, Edit, and Delete Flow

- [ ] 2.1 Update `RootFlowDestination.AddExpenseFlow` and `AddExpenseFlowComponent` / `AddExpenseFlowChildConfig` to accept `expenseId: Long? = null`.
- [ ] 2.2 Update `AddExpenseComponent` to support edit mode (`expenseId != null`): pre-populate amount, description, category, and pocket; handle update on save; handle delete action; verify with `AddExpenseTest`.
- [ ] 2.3 Update `AddExpenseScreen` to show "Edit Expense" in top app bar and display Delete icon action button in the TopAppBar when in edit mode.
- [ ] 2.4 Update `:design` module `features/editexpense/Screen.kt` and `Component.kt` to include the TopAppBar Delete action button.

## 3. Analytics Component & Screen

- [ ] 3.1 Implement `AnalyticsComponent`, `DefaultAnalyticsComponent`, `AnalyticsComponentFactory`, and `FakeAnalyticsComponent` in `com.base.feature.analytics`: handle selected month state, previous/next navigation, monthly outflow calculation, date grouping (reverse-chronological), and item click handler; verify with `AnalyticsTest`.
- [ ] 3.2 Implement `AnalyticsScreen` with `MonthSwitcherCard` (month controls and monthly outflow, omitting trend), date group headers with sums, and clickable expense items.
- [ ] 3.3 Wire `AnalyticsComponentFactory` into `MainComponent`, host `AnalyticsComponent` in `MainComponent.Child.Analytics`, render `AnalyticsScreen` in `MainScreen`, and register component factory in Koin modules.

## 4. Home Screen Cleanup

- [ ] 4.1 Remove expense observation and `expenses` list from `HomeComponent`, `DefaultHomeComponent`, and `HomeState`; verify with `HomeTest`.
- [ ] 4.2 Remove `ExpensesList` and empty-expenses placeholder from `HomeScreen`, leaving active banners or an empty container when no banners are active.

## 5. End-to-End Integration & UI Verification

- [ ] 5.1 Update `AddViewExpenseFlowE2eTest` to verify that added expenses appear on the Analyse tab, can be clicked to edit, and can be deleted via TopAppBar.
- [ ] 5.2 Update `AddViewExpenseE2eUiTest` verifying keyboard interactions for adding, inspecting on Analyse tab, editing, and deleting via TopAppBar.
- [ ] 5.3 Execute project verification gate (`compileKotlin`, `jvmTest`, `detektMain`, `ktfmtFormat`).
