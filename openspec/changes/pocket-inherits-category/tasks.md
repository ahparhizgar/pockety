# Tasks

> DRAFT — unchecked. Nothing here is approved or started. Pick an
> option in the proposal first; tasks are grouped by option.

## 0. Decision gate

- [ ] 0.1 Amir picks Option A (full 1:1), B (middle path) or C (keep current)
- [ ] 0.2 If A: lock or editable inherited name/icon/color? (proposal decision #2)
- [ ] 0.3 If A: legacy-data rules decided — pockets with no category,
  pockets sharing a category, expenses whose category disagrees with
  their pocket (proposal decision #3)
- [ ] 0.4 If A: category-deletion rule decided (proposal decision #4)
- [ ] 0.5 If A: uncategorized expenses allowed or not? (proposal decision #5)

## 1. Design gate (Option A only)

- [ ] 1.1 Build the category-first pocket-creation flow in `:design`
  and get Amir's approval before app implementation
- [ ] 1.2 Design the derived-pocket display in Add Expense (read-only
  line, or hidden) and the blocked-save state without a category

## 2. Data (Option A only)

- [ ] 2.1 Unique index on `pockets.categoryId` (partial, NULLs allowed
  only if legacy unlinked pockets are kept) + Room migration 10 → 11
- [ ] 2.2 Data repair for legacy cases per decision 0.3 (migration or
  one-time repair on first launch after upgrade)
- [ ] 2.3 Category-deletion rule implemented per decision 0.4
  (today the FK is `SET_NULL`, which orphans the pocket under A)

## 3. Pocket creation (A: full rework / B: prefill only)

- [ ] 3.1 `PocketSettingsComponent.onCategoryChanged` prefills name,
  icon and color from the picked category (A: category is picked first
  and is mandatory; B: prefill only, all fields stay editable)
- [ ] 3.2 One-pocket-per-category enforced at save time (A only):
  saving a second pocket for a category is rejected with an error
- [ ] 3.3 Remove the category tag badge from `PocketBudgetCard` (A only —
  badge == pocket identity once the model is 1:1)

## 4. Add Expense (A only; B needs no structural change)

- [ ] 4.1 Remove `PocketSelectorCard` and the pocket-selection child
  from `AddExpenseFlowComponent`
- [ ] 4.2 Category becomes mandatory; save blocked without one
- [ ] 4.3 Pocket derived from the category at save time
  (`firstOrNull { it.categoryId == category.id }` — already the
  auto-assignment rule; now the only rule)
- [ ] 4.4 SMS candidates: no detected category means no pocket (explicit)

## 5. Tests

- [ ] 5.1 Component tests: inheritance prefill, uniqueness rejection,
  derivation at save, blocked save without a category (A)
- [ ] 5.2 Component tests: prefill is editable, override still works (B)
- [ ] 5.3 Migration test 10 → 11 incl. the legacy-data rules (A)
- [ ] 5.4 E2E tests for the new creation flow and the new add-expense flow

## 6. Verification

- [ ] 6.1 Existing pocket/expense UI tests keep passing (test tags kept
  where the screens survive)
- [ ] 6.2 CI green; screenshot diff reviewed and sent
- [ ] 6.3 Specs synced and change archived after Amir's sign-off
