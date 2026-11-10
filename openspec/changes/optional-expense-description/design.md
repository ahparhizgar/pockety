# Design

## Context

See `proposal.md` for background and motivation.
Currently, `AddExpenseComponent` enforces `isDescriptionValid = currentState.descriptionText.trim().isNotEmpty()`. If invalid, `error_empty_description` is attached to `state.descriptionError` and save is aborted.
In the database, `ExpenseEntity.description` is non-null `String` (`val description: String`). Saving an empty description as empty string `""` preserves database schema compatibility without requiring a Room schema migration.
In `AnalyticsScreen`, expense items render `expense.description` in a title `Text` composable with `testTag("expense-description")`. If `categoryName` is absent and `description` is blank or empty, having an empty title composable leaves unnecessary blank layout space.
The issue states: "if neither category and descriptoin is set, show only the amount in the expense list."

## Goals / Non-Goals

**Goals:**
- Allow users to save new expenses or edit existing expenses without providing a description.
- Keep `ExpenseEntity` schema unchanged (`description: String`), storing trimmed string or empty string `""`.
- In `AnalyticsScreen` (the expense list), when an expense has no category (`categoryName == null`) and no description (`description.isBlank()`), display only the amount and time (omitting the description text node).
- When a category exists but description is blank, display the category name or appropriate indicator so the item remains informative.

**Non-Goals:**
- Making `ExpenseEntity.description` nullable in the Room database (unnecessary and requires migration; empty string `""` suffices).
- Changing candidate expense ingestion or SMS auto-tracking data model.

## Decisions

### Decision 1: Database persistence of empty description
- **Choice**: Store empty string `""` in `ExpenseEntity.description` when description is blank or omitted.
- **Rationale**: `ExpenseEntity` already defines `description: String`. Persisting `""` avoids database schema bump and migration overhead while seamlessly supporting optional descriptions.
- **Alternative considered**: Make `ExpenseEntity.description` nullable (`String?`). Rejected because it requires a schema migration (v7 to v8) with no tangible benefit over empty string.

### Decision 2: Validation in `AddExpenseComponent`
- **Choice**: Only validate amount (`isAmountValid = amountNumber != null && amountNumber > 0`). Remove `isDescriptionValid` check and `error_empty_description` assignment from `onSaveClicked`.
- **Rationale**: Direct requirement from issue POC-15. Empty or whitespace-only descriptions are permitted and normalized via `.trim()`.

### Decision 3: UI display in `AnalyticsScreen`
- **Choice**: In `AnalyticsExpenseItem`, determine display text:
  - If `expense.description.isNotBlank()`, render `expense.description`.
  - Else if `expense.categoryName != null`, render `expense.categoryName`.
  - Else (neither category nor description is set), do not render the title `Text` composable, rendering only the formatted time (if present) and the amount on the trailing edge.
- **Rationale**: Matches the explicit requirement: "if neither category and descriptoin is set, show only the amount in the expense list."

## Risks / Trade-offs

- **[Existing tests expecting description error]** → Unit tests in `AddExpenseTest` that assert `state.descriptionError == UiString(Res.string.error_empty_description)` will be updated to reflect that empty descriptions are valid and no error is produced.
