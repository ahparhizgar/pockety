# Tests Specification

Tests live in the repo's feature packages under
`app/shared/src/commonTest/kotlin/com/base/feature/category/...` (per AGENTS.md),
extending the existing management/selection test files where they exist.

## Unit Tests (single component / class / function)

### `category/management/CategoryManagementTest` (new)

**Target**: `CategoryManagementComponent` state and callbacks, using the fake
category repository.

**Scenarios**:

- Given a state with existing categories
    - When the user activates delete for a category,
        - [ ] Then the state holds that category as pending deletion and nothing is deleted yet
- Given a category pending deletion
    - When the user confirms the deletion,
        - [ ] Then the category is removed from the repository and from the state list, and no category is pending deletion
- Given a category pending deletion
    - When the user cancels the deletion,
        - [ ] Then the category remains in the repository and in the state list, and no category is pending deletion
- Given a state with existing categories
    - When the user enters a search query matching only some category names,
        - [ ] Then the displayed list contains only the matching categories
- Given a state with existing categories
    - When the search query is cleared,
        - [ ] Then the displayed list contains all categories again

## E2E Flow Tests

### `category/management/CategoryManagementFlowE2eTest` (extend)

**Flow**: Root → Settings → Categories, exercising the redesigned destination
end to end.

**Scenarios**:

- Given categories exist and one of them is assigned to an expense
    - When the user opens Categories from Settings, deletes that category and confirms,
        - [ ] Then the category list no longer shows it
        - And the expense still exists and is uncategorized
        - And returning to Settings shows the decreased category count
- Given the redesigned Categories destination
    - When the user adds a category through the existing creation form and saves,
        - [ ] Then the new category appears in the list with its name, icon and color

## Repository & Data Tests

### `category/selection/CategoryRepositoryTest` (extend if not already covered)

- Given a category assigned to an expense and a pocket
    - When `deleteCategory` is called for that category,
        - [ ] Then the category is gone
        - And the expense and pocket remain with a null category reference

## UI & Accessibility Tests

### `category/management/CategoryManagementE2eUiTest` (extend)

**Scenarios**:

1. The destination renders categories as list rows with a colored icon tile and
   name; there is no Uncategorized row, no selected state, and no active badge.
2. Keyboard journey: Tab reaches the search field, the rows' delete buttons, the
   create button and Back; Enter on a delete button opens the confirmation
   dialog; the dialog's Confirm and Cancel are keyboard-operable; Confirm
   removes the row, Cancel keeps it.
3. Typing a query filters the rows; a query with no matches shows the
   no-results empty state; clearing the query restores all rows.
4. With no categories at all, the explanatory empty state and the create action
   are shown.
5. Screenshot previews cover the redesigned screen: populated, empty,
   no-search-results, delete-confirmation dialog, dark mode, and RTL — and the
   selection screen's existing screenshots stay byte-identical after the shared
   row extraction (verified by the screenshot-diff routine).
