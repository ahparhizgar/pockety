# Tests

- `PocketSettingsTest` (component, `com.base.feature.pocket.settings`):
  picking a category prefills the pocket name, icon and color from it;
  under Option A, saving a second pocket for the same category is
  rejected; under Option B the prefilled values stay editable.
- `AddExpenseTest` (component, `com.base.feature.expense.add`):
  under Option A, selecting a category derives its pocket and saving
  without a category is blocked; the pocket picker is gone.
- `PocketRepositoryTest` / migration test: Room migration 10 → 11
  applies the unique index and the legacy-data rules (Option A).
- Existing E2E tests (`AddViewExpenseE2eUiTest`, pocket settings UI
  tests) updated to the new flows; test tags kept where screens survive.
- Screenshot previews: category-first pocket creation (prefilled),
  Add Expense with derived pocket, Add Expense with no category
  (save blocked).
