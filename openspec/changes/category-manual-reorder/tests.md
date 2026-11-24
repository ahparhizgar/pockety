# Tests Specification

Tests live in the repo's feature packages under
`app/shared/src/commonTest/kotlin/com/base/feature/category/...` (per AGENTS.md),
extending the existing management/selection test files where they exist.

## Repository & Data Tests

### `category/selection/CategoryRepositoryTest` (extend)

- Given categories created at different times
    - When `reorderCategories` is called with a new id order,
        - [ ] Then `observeCategories` emits the categories in that order
- Given existing categories
    - When a new category is added,
        - [ ] Then it is emitted last in `observeCategories`
- Given categories with assigned sort orders
    - When one is deleted and the rest are read,
        - [ ] Then the remaining categories keep their relative order

## Unit Tests (single component / class / function)

### `category/management/CategoryManagementTest` (extend)

- Given a state with existing categories in manual order
    - When the user moves a category from one position to another,
        - [ ] Then the state list reflects the move immediately
        - And the persisted order matches the state order (verified through the
          component's observed list after the move)
- Given a search query is active
    - When the displayed (filtered) list is considered,
        - [ ] Then reordering is not offered for the filtered subset (component
          exposes the full list for reorder only when the query is blank)

## E2E Flow Tests

### `category/management/CategoryManagementFlowE2eTest` (extend)

- Given the user created several categories in Settings → Categories
    - When the user reorders them on the browse destination,
        - [ ] Then leaving and reopening Categories keeps the new order
        - And the category selection page (opened from an expense flow) shows the
          same order below the Uncategorized entry

## UI & Accessibility Tests

### `category/management/CategoryManagementE2eUiTest` (extend)

1. Rows on the management destination show a drag handle; there is no drag handle
   on the selection page.
2. Keyboard journey: Tab reaches a row's drag handle; ArrowDown moves the category
   one position down and the new order persists after leaving and returning;
   ArrowUp moves it back.
3. While a search query is typed, drag handles are hidden.
4. Screenshot previews cover the management screen with drag handles (and the
   selection screen's screenshots stay byte-identical — it has no visual change).
