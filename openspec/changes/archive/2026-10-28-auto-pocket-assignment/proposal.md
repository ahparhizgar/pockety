# Proposal

## Why

Currently, when users record expenses (manually or via SMS candidates), transactions are saved without any pocket assignment, even when the selected category is linked to a budget pocket. This leaves pocket balances untouched unless transactions are manually wired up in test fixtures. Introducing automatic category-to-pocket assignment streamlines expense tracking and fulfills the core envelope budgeting promise: selecting a category automatically routes expense deductions to the corresponding pocket, while still giving users the flexibility to manually reassign or unlink the pocket.

## What Changes

- **Automatic Pocket Assignment**: When a category is selected during expense creation, the system automatically assigns the pocket configured with that category (if one exists).
- **Manual Pocket Selection & Unlink**: Users can manually open a pocket selection screen from `AddExpenseScreen` to choose any budget pocket, or tap an unlink action to clear the pocket assignment.
- **Pocket Selector Card**: In `AddExpenseScreen`, render the pocket selector card matching `:design` specifications, displaying pocket icon, name, formatted remaining balance, and an unlink button when linked, or a tap-to-link placeholder when unlinked.
- **Select Pocket Screen**: Provide a dedicated pocket selection destination in the add expense flow supporting pocket search, "No Pocket" (unlinking), and a listing of available pockets with balances.
- **Persistence & Deduction**: When saving an expense, persist `pocketId` so that the pocket's balance automatically reflects the deduction in the activity ledger. Only applies to new transactions and subsequent edits (no retroactive batch migration).
- **Pocket Details Transaction Unlinking**: In `PocketDetailsScreen`, update the transaction action on linked expense rows to unlink the transaction (setting `pocketId = null`) rather than deleting it from the database, and replace the trash/delete icon with an "X" close icon (`Icons.Default.Close`).

## Capabilities

### Modified Capabilities
- `expense-management`: Add requirements for automatic pocket assignment upon category selection, manual pocket selection and unlinking in the expense creation flow, and persisting the pocket linkage with the saved expense.
- `pocket-management`: Add requirement for unlinking expense transactions from pocket details, restoring pocket balance while preserving the expense in storage, and using an X icon instead of a delete icon.

## Impact

- **UI / Decompose Navigation**:
  - `AddExpenseComponent` / `AddExpenseState`: Holds `selectedPocket: PocketItemModel?`, handles `onPocketSelected`, `onPocketCleared`, `onSelectPocketClicked`, and auto-assignment on `onCategorySelected`.
  - `AddExpenseFlowComponent`: Adds `PocketSelection` child config and screen navigation.
  - `AddExpenseScreen`: Incorporates `PocketSelectorCard` matching `:design/features/editexpense`.
  - `SelectPocketComponent` & `SelectPocketScreen`: Implements pocket selection with search and "No Pocket" option matching `:design/features/selectpocket`.
  - `PocketDetailsComponent` / `PocketDetailsScreen`: Changes delete action on expense rows to `onUnlinkTransaction`, updates trailing icon to "X" close icon, and displays restoration confirmation banner.
- **Data & Repositories**:
  - `ExpenseRepository`: Passes `pocketId` to `transactions` table (database column already exists).
  - Injects `PocketRepository` into `AddExpenseComponent` to observe/resolve pockets.
  - `PocketDao` & `PocketRepository`: Adds `unlinkExpense(id: Long)` updating `transactions.pocketId = NULL`.
