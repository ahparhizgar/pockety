# Design

## Context

The application's Room database already defines `pocketId` on `ExpenseEntity` (`transactions` table) and `categoryId` on `PocketEntity` (`pockets` table). The ledger calculation in `PocketDao` dynamically subtracts all transactions where `transactions.pocketId == pocket.id` from the total top-ups.

In `app/shared`, `DefaultAddExpenseComponent` currently only passes `categoryId` to `expenseRepository.addExpense` and omits `pocketId`. The `:design` module already defines the visual specification and interactive behavior for `PocketSelectorCard` (inside `editexpense`) and `SelectPocketScreen` (inside `selectpocket`), which serve as our single source of truth.

## Goals / Non-Goals

**Goals:**
- Inject `PocketRepository` into `DefaultAddExpenseComponent` to observe available budget pockets.
- Automatically assign a matching pocket (`pocket.categoryId == category.id`) when a category is selected in `AddExpenseComponent`.
- Render `PocketSelectorCard` in `AddExpenseScreen` reflecting linked (pocket icon, name, balance, unlink button) and unlinked states.
- Introduce `SelectPocketComponent` and `SelectPocketScreen` in `app/shared`, navigated via `AddExpenseFlowComponent` child stack.
- Support manual pocket selection and explicit unlinking ("No Pocket" or close icon).
- Persist `pocketId` when saving an expense via `ExpenseRepository.addExpense`.

**Non-Goals:**
- Retroactive assignment of existing historical transactions.
- Merchant or raw SMS keyword rules (auto-assignment is driven strictly by category, backed by manual selection).
- Creating new pockets directly from the pocket selection sheet in this slice (can be added as an extension).

## Decisions

### 1. Component-Level Auto-Assignment vs. Database Trigger
- **Decision**: Perform auto-assignment in `DefaultAddExpenseComponent` when `onCategorySelected` is invoked.
- **Rationale**: The user must see the auto-assigned pocket immediately in the UI (its name, icon, and remaining balance) and be able to unlink or change it before saving. A database trigger or background repository hook would be opaque and prevent user review.
- **Alternatives Considered**:
  - *Repository-level fallback*: If `pocketId == null` on insert, look up category's pocket. Rejected because if the user explicitly unlinked the pocket, the repository would incorrectly re-assign it.

### 2. Decompose Child Stack for Pocket Selection
- **Decision**: Add `AddExpenseFlowChildConfig.PocketSelection(val currentPocketId: Long?)` to `AddExpenseFlowComponent`, backed by a `SelectPocketComponent`.
- **Rationale**: Follows the existing architectural pattern used for `CategorySelection` (`AddExpenseFlowChildConfig.CategorySelection`). Decompose router manages back button, state preservation, and adapts cleanly across desktop and mobile screens.
- **Alternatives Considered**:
  - *In-screen Compose ModalBottomSheet*: Rejected to avoid fragmentation of navigation patterns and ensure testability through Decompose component assertions without UI coupling.

### 3. Pocket Selector Visual Design & Unlink Action
- **Decision**: Implement `PocketSelectorCard` matching `:design/features/editexpense/Component.kt`:
  - Header: Wallet icon (`Icons.Default.AccountBalanceWallet`) and title "Pocket".
  - Inner card:
    - *When linked*: Displays circular icon container, pocket title, balance formatted in Tomans, and an `IconButton` with `Icons.Default.Close` to unlink.
    - *When unlinked*: Displays placeholder icon, "Tap to link a pocket", and "Auto-deduct from pocket budget".
  - Clicking the card invokes `onSelectPocketClicked()`.
- **Rationale**: Matches `:design` directly as the single source of truth.

### 4. Pocket Selection Screen Features
- **Decision**: Implement `SelectPocketScreen` matching `:design/features/selectpocket/`:
  - Search field to filter by pocket name and category tag.
  - "No Pocket" option at the top to clear the selection.
  - Sectioned list displaying pockets with icon, background color, category tag badge, remaining balance (with over-budget indicator if negative), and active checkmark.

## Risks / Trade-offs

- **[Multiple pockets mapped to same category]** → In `DefaultAddExpenseComponent`, pick the first matching pocket (`firstOrNull { it.categoryId == category.id }`).
- **[Concurrent pocket deletion]** → Database schema uses `ON DELETE SET NULL` on `transactions.pocketId`.
- **[Flow reactivity]** → `DefaultAddExpenseComponent` collects `pocketRepository.observePockets()` in its component scope to have the latest pocket list and real-time balances available during editing.
