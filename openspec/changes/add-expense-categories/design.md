# Design

## Context

See `proposal.md` for user motivation and goals.
The application currently persists expenses locally via Room 3 in `AppDatabase` (version 4) within `app/shared`. Navigation is managed using Decompose child stacks under `RootComponent`. The `:design` module already provides Compose previews and UI components for `selectcategory`, `addcategory`, and `editexpense`.

## Goals / Non-Goals

**Goals:**
- Add `CategoryEntity` table and update `ExpenseEntity` with nullable `categoryId` referencing categories (`ON DELETE SET NULL`).
- Implement schema migration `MIGRATION_4_5` for Room.
- Decouple expense addition navigation from the root navigation by wrapping `AddExpenseComponent`, `CategorySelectionComponent`, and `AddCategoryComponent` inside an `AddExpenseFlowComponent`.
- Implement category selection and creation UI matching `:design` module implementations without hard-coded strings, using string resources and Material 3 design tokens.
- Keep categories purely user-defined in this slice (no pre-seeded categories).

**Non-Goals:**
- Pre-seeded system categories (deferred to future slices).
- Category management list screen (CRUD list / detail screen outside of expense addition).
- Auto-categorizing SMS candidate expenses.
- Category budgets, limits, or analytics.

## Decisions

### Decision 1: Room Database Migration (v4 -> v5)
- **Choice**: Add `categories` table with columns `id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL`, `name TEXT NOT NULL`, `icon TEXT NOT NULL`, `color INTEGER NOT NULL`, `createdAt INTEGER NOT NULL`. Add `categoryId INTEGER` column to `transactions` table with foreign key to `categories(id)` ON DELETE SET NULL.
- **Rationale**: Keeps expense category assignment optional. If a category is deleted in the future, associated transactions revert gracefully to uncategorized.
- **Alternatives Considered**: Require mandatory categories with a fallback row; rejected because expenses can naturally be uncategorized.

### Decision 2: Navigation via `AddExpenseFlowComponent`
- **Choice**: Introduce `AddExpenseFlowComponent` as a Decompose stack owner. `RootComponent` only pushes `AddExpenseFlowComponent.Config`. Inside the flow:
  - Child destinations: `AddExpense`, `CategorySelection`, and `AddCategory`.
  - Selecting a category or saving a new one updates the state/callback and returns to `AddExpense`.
  - Cancelling or saving the expense signals `onCompleted()` or `onCancelled()` back to `RootComponent`.
- **Rationale**: Aligns with `AGENTS.md` ("flows are separate navigations from the main root navigation... so when there is a related set of components, create a flow component for them").
- **Alternatives Considered**: Direct root stack navigation for each screen; rejected because it clutters root navigation with ephemeral child screens.

### Decision 3: Category Visual Token Representation
- **Choice**: Store `color` as `Long` (ARGB) and `icon` as a `String` key. In UI, use an icon registry / resolver that maps the string key to Material `ImageVector` instances matching the `:design/features/addcategory` icon set.
- **Rationale**: Clean separation of persistent data from platform UI objects while remaining flexible across targets.

### Decision 4: Vertical Slice Scope for `AddExpenseScreen`
- **Choice**: Add only the Category Selector row/card from `:design/features/editexpense/Component.kt` into `AddExpenseScreen`.
- **Rationale**: Maintains vertical slice agility without pulling in pockets, tags, or unneeded fields.

## Risks / Trade-offs

- **[Risk] Foreign key constraints on existing SQLite tables** → SQLite ALTER TABLE cannot directly add a foreign key constraint to an existing table. In Room migration, we alter `transactions` table to add column `categoryId INTEGER`. Foreign key validation is enforced by Room annotations and table recreation if necessary, or handled cleanly in migration SQL.
- **[Risk] State retention when navigating to category selection** → When pushing `CategorySelection` on the flow stack, `AddExpenseComponent` remains in the backstack, naturally retaining its amount and description state.
