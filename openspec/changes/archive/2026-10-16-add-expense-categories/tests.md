# Tests Specification

## Unit Tests (single component / class / function)

### `category_management/CategoryRepositoryTest`
**Target**: `CategoryRepository` and `AppDatabase` Room migration
**Scenarios**:
- Given database at version 4
    - When migration `MIGRATION_4_5` executes
        - [ ] Then `categories` table exists with required columns
        - [ ] Then `transactions` table contains nullable `categoryId` column
- Given an empty `CategoryRepository`
    - When `addCategory("Dining", "restaurant", 0xFFE57373)` is called
        - [ ] Then the returned category ID is positive
        - [ ] Then `observeCategories()` emits the newly created category
- Given an existing category assigned to an expense
    - When the category is deleted
        - [ ] Then the expense's `categoryId` is set to null

### `category_management/AddCategoryTest`
**Target**: `AddCategoryComponent`
**Scenarios**:
- Given an empty category name input
    - When `onSaveClicked` is triggered
        - [ ] Then validation error is shown and category is not saved
- Given a valid category name, selected color, and selected icon
    - When `onSaveClicked` is triggered
        - [ ] Then category is saved to repository and `onCompleted` callback is called

### `category_management/CategorySelectionTest`
**Target**: `CategorySelectionComponent`
**Scenarios**:
- Given existing categories in repository
    - When a category item is selected
        - [ ] Then `onCategorySelected` callback is invoked with the chosen category
- Given an existing category selection
    - When the "Uncategorized" item is selected
        - [ ] Then `onCategorySelected` is invoked with null (clearing category)
- Given a list of categories
    - When search query is entered
        - [ ] Then list filters to categories matching query name

### `adding_expense/AddExpenseTest`
**Target**: `AddExpenseComponent`
**Scenarios**:
- Given an expense with selected category
    - When `onSaveClicked` is invoked with valid amount and description
        - [ ] Then expense is persisted in repository with `categoryId`

## E2E Flow Tests

### `adding_expense/AddExpenseFlowE2eTest`
**Flow**: Navigation between Root, AddExpense, CategorySelection, and AddCategory
**Scenarios**:
- Given app on Main screen
    - When user opens Add Expense
        - [ ] Then `AddExpenseFlowComponent` is active showing `AddExpenseComponent`
    - When user taps "Change Category"
        - [ ] Then `CategorySelectionComponent` is pushed on the flow stack
    - When user taps "Create New Category"
        - [ ] Then `AddCategoryComponent` is pushed on the flow stack
    - When user enters "Coffee", selects color and icon, and saves
        - [ ] Then category is saved, and flow pops back to `CategorySelectionComponent` or `AddExpenseComponent` with "Coffee" selected
    - When user enters amount and description, and taps save
        - [ ] Then expense is saved with "Coffee" category, flow completes, and Root displays the expense

## UI & Accessibility Tests

### `com.base.capability.category_management.CategorySelectionE2eUiTest`
**Focus**: UI behavior, search input, keyboard accessibility, and option selection
**Scenarios**:
1. User searches for a category using keyboard actions (ImeAction.Search) and selects the filtered category using keyboard/touch.
2. User selects the "Uncategorized" item and verifies active selection badge.
3. User navigates to "Create New Category" button via keyboard/focus and triggers navigation.

### `com.base.capability.category_management.AddCategoryE2eUiTest`
**Focus**: Category creation form keyboard accessibility and live preview updates
**Scenarios**:
1. User enters name with keyboard, navigates to accent color and icon options, and triggers save via keyboard Done action.
