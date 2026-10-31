# Design

## Context

Currently, `MainComponent` manages bottom bar tabs (`Home`, `Inbox`, `Pockets`, `Analytics`, `Settings`), with `Analytics` rendering an empty `Box`. `HomeScreen` observes `ExpenseRepository.observeExpenses()` directly and renders `ExpensesList`. `AddExpenseFlow` and `AddExpenseComponent` support only creating new expenses.

To provide a structured spending ledger, transaction records will move from `HomeScreen` into the `Analytics` tab under an `AnalyticsComponent`. Expenses will be filtered by calendar month, grouped by date, and clickable to open `AddExpenseFlow` in edit mode with a TopAppBar delete action.

## Goals / Non-Goals

**Goals:**
- Provide `AnalyticsComponent` and `AnalyticsScreen` displaying a `MonthSwitcherCard` and date-grouped transaction list for the selected month.
- Support previous/next month navigation, disabling next when viewing the current month.
- Calculate and display total monthly outflow for the selected month.
- Group transactions by date in reverse-chronological order with date headers and daily totals.
- Allow clicking any transaction to navigate to `AddExpenseFlow` in edit mode (`expenseId != null`).
- Pre-populate amount, description, category, and pocket in edit mode, titled "Edit Expense".
- Provide a Delete action in the `TopAppBar` when editing an expense, deleting the record and closing the flow.
- Remove the transaction list and empty-expenses placeholder from `HomeScreen`.
- Update the `:design` module (`editexpense` screen/component) to feature the TopAppBar delete action.

**Non-Goals:**
- Category spending breakdown and donut chart (explicitly out of scope).
- Month-over-month trend percentage calculation (explicitly omitted).
- Arbitrary calendar date-picker dialogs (navigation uses `<` / `>` controls).
- Delete action in create/add mode (delete is strictly an edit mode action).

## Decisions

### 1. Component Architecture for Analyse Screen
- **Choice**: Introduce `AnalyticsComponent` (and factory) under `com.base.feature.analytics` (or `analyse`), hosted as `MainComponent.Child.Analytics(val component: AnalyticsComponent)`.
- **Rationale**: Keeps tab-level Decompose components isolated and lifecycle-aware. Injected via Koin `AnalyticsComponentFactory`.
- **Alternatives Considered**: Inlining list logic in `MainComponent` - rejected because it violates separation of concerns and Decompose best practices.

### 2. Month Navigation & Outflow Calculation
- **Choice**: `AnalyticsState` stores `selectedYearMonth: YearMonth` (or `selectedYear: Int, selectedMonth: Int`), `monthlyOutflow: Long`, and `expenseGroups: List<AnalyticsDateGroupUiModel>`.
  - Starts at current year/month via `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())`.
  - Previous action decrements month by 1; next action increments month by 1.
  - `canNavigateNext` is false when selected month == current calendar month.
  - `monthlyOutflow` is computed as the sum of expenses belonging to that year/month.
  - Trend text is omitted per user requirement.
- **Alternatives Considered**: Querying distinct months via SQL - rejected as unnecessary complexity given local transaction volumes.

### 3. Date Grouping for Transactions
- **Choice**: Expenses in the selected month are grouped by `LocalDate` descending.
  - Group header label: "Today, <d> <Month>", "Yesterday, <d> <Month>", or "<d> <Month> <yyyy>".
  - Group total formatted amount: sum of expenses within that date group.
  - Items in group: Category icon, category color (or fallback receipt icon), description, time/note, and formatted deduction (`-120,000 T`).
  - Empty state: When no expenses exist for that month, show empty state message.

### 4. Database Queries for Expense Edit & Delete
- **Choice**: Update `ExpenseDao` with:
  ```kotlin
  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getExpenseById(id: Long): ExpenseEntity?

  @Update
  suspend fun updateExpense(expense: ExpenseEntity)

  @Query("DELETE FROM transactions WHERE id = :id")
  suspend fun deleteExpense(id: Long)
  ```
  And expose corresponding suspend methods on `ExpenseRepository`.
- **Rationale**: Reuses the existing `transactions` table and Room entity without modifying table schema, avoiding Room schema migrations.
- **Pocket Balance Impact**: Pocket balances are dynamically computed in Room queries via `SUM(top_ups) - SUM(expenses)`; updating or deleting an expense automatically updates pocket balances with no extra synchronisation code.

### 5. Dual Add/Edit Mode in Expense Flow
- **Choice**: Parameterize `RootFlowDestination.AddExpenseFlow(val expenseId: Long? = null, ...)` and `AddExpenseComponent.Config(val expenseId: Long? = null, ...)`.
  - When `expenseId != null`:
    - Screen title is "Edit Expense".
    - `AddExpenseComponent` loads the existing expense on init and pre-fills `amountText`, `descriptionText`, `selectedCategory`, and `selectedPocket`.
    - `TopAppBar` displays a Delete icon button in `actions`.
    - On Save: calls `expenseRepository.updateExpense(...)`.
    - On Delete: calls `expenseRepository.deleteExpense(expenseId)` and completes flow.
  - When `expenseId == null`:
    - Title is "Add Expense", Delete icon is absent, Save calls `addExpense(...)`.

### 6. Home Screen Cleanup
- **Choice**: Remove `expenses` from `HomeState` and `HomeScreen`.
  - `HomeScreen` retains `SmsPermissionBanner` and `UndecidedExpensesBanner` when relevant, plus the extended FAB "Add Expense".
  - When no banners are active, the screen is cleanly empty without placeholder cards.

## Risks / Trade-offs

- **[Timezone Consistency]** → Converting `Instant` to local year/month/day could drift if timezones differ between recording and viewing.
  - *Mitigation*: Consistently use `TimeZone.currentSystemDefault()` for all date extractions and groupings.
- **[Edit Pre-population Race]** → Async loading of an existing expense could cause initial empty state flicker.
  - *Mitigation*: Maintain an `isLoading` flag in `AddExpenseState` during initial fetch, or populate synchronously if pre-fetched.
- **[Accidental Expense Deletion]** → Tapping Delete might delete without warning.
  - *Mitigation*: In the TopAppBar, delete icon directly deletes or can show confirmation. Per spec, tapping the TopAppBar delete action executes deletion and finishes the flow.
