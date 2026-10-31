# Proposal

## Why

Currently, all recorded expenses are displayed in a flat list directly on the Home screen, crowding the home overview and lacking monthly time-frame filtering and inspection capabilities. Moving the transaction list to the dedicated Analyse tab with a month selection card provides users with clear monthly spending insights, cleans up the Home screen, and allows users to tap expenses to view, edit, or delete them.

## What Changes

- **Move Expenses to Analyse Tab**: Relocate the transaction history feed from the Home screen into the Analyse tab (Analytics tab).
- **Month Selection Card**: Introduce a month selector card on the Analyse screen showing `< Previous`, `Next >`, the current selected month label (e.g., `October 2026`), and total monthly outflow. Category Spending section and trend percentage are explicitly out of scope.
- **Date-Grouped Transaction List**: Display expenses filtered by the selected month, grouped by date in reverse-chronological order (e.g., `Today, 24 Oct`, `Yesterday, 23 Oct`) with date headers and total sums.
- **Clean Home Screen**: Remove the expenses list and empty-expenses placeholder from the Home screen. The SMS tracking and undecided review banners remain; when no banners are present, the Home screen remains cleanly empty. The "Add Expense" FAB remains accessible.
- **Click to View and Edit Expense**: Make transaction items clickable. Tapping an item opens the expense in edit mode within `AddExpenseFlow`.
- **TopAppBar Delete Action**: Provide a Delete icon button in the TopAppBar of the expense form when in edit mode (hidden in add mode). Tapping delete removes the expense and closes the flow.
- **Update Design Module**: Update the `:design` module's `editexpense` screen/component to reflect the TopAppBar delete action.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `expense-management`: Modify the expense list viewing requirement to display transactions on the Analyse screen filtered by month and grouped by date instead of the Home screen; add requirements for viewing, editing, and deleting recorded expenses.

## Impact

- **UI & Navigation**:
  - `MainComponent` & `MainScreen`: Instantiate and display `AnalyticsComponent` and `AnalyticsScreen` on the `Analytics` tab.
  - `HomeComponent` & `HomeScreen`: Strip expense observation and list rendering.
  - `AddExpenseFlowComponent` & `AddExpenseComponent`: Accept optional `expenseId` parameter to support view/edit mode and TopAppBar delete action.
  - `:design`: Update `editexpense` screens to include the TopAppBar delete action.
- **Data & Persistence**:
  - `ExpenseDao` & `ExpenseRepository`: Add `getExpenseById`, `updateExpense`, `deleteExpense`, and date/month filtered expense querying.
- **Tests**:
  - Update `AddViewExpenseFlowE2eTest` and `AddViewExpenseE2eUiTest` to verify expenses via the Analyse tab.
  - Add unit and integration tests for `AnalyticsComponent` and expense edit/delete operations.
