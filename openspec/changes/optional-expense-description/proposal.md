# Proposal

## Why

Currently, expense creation and editing strictly require a non-empty description. Users often want to quickly record an expense with only an amount (and optionally a category or pocket) without having to type a description. Making description optional streamlines quick expense tracking while preserving clean list presentation when neither category nor description is provided.

## What Changes

- Make expense `description` optional during creation and updating in `AddExpenseComponent` (saving empty string or trimming whitespace). Remove the empty description validation check and error display.
- Update expense list item rendering in `AnalyticsScreen`: when neither category nor description is present, omit the empty description line and display only the amount (and timestamp if present) cleanly.
- Ensure consistent empty description fallback handling across expense lists (such as `PocketDetailsComponent` and `AnalyticsComponent`).

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `expense-management`: Update `Record expense` requirement to explicitly treat description as optional (no validation error on empty description), and update `View expenses list` requirement to specify display behavior when an expense has no description and no category.

## Impact

- `AddExpenseComponent`: Validation logic updated to allow empty description; `saveOrUpdateExpense` handles empty/blank descriptions.
- `AnalyticsScreen` / `AnalyticsComponent`: UI rendering adjusted to cleanly display expenses without description and without category (showing only the amount and time).
- `AddExpenseTest` / `AddViewExpenseFlowE2eTest`: Test assertions adjusted to reflect that empty description is accepted.
