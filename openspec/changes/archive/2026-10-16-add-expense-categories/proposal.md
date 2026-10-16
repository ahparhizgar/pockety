# Proposal

## Why

Users need a way to categorize expenses to organize their personal finances. Currently, expenses only support an amount and description without any categorization. Adding category capabilities allows users to define custom categories with distinct colors and icons, assign an optional category when recording an expense, and keep expenses organized.

## What Changes

- **Category Data Model & Storage**: Introduce `CategoryEntity` (`id`, `name`, `icon`, `color`, `createdAt`) and Room migration from schema v4 to v5. Update `ExpenseEntity` with a nullable `categoryId` referencing categories (`ON DELETE SET NULL`).
- **Add Expense Flow Architecture**: Introduce `AddExpenseFlowComponent` to manage child navigation between expense input and category selection, keeping the navigation lifecycle clean and decoupled from `RootComponent`.
- **Add Expense Category Section**: Integrate the category display/selector card into `AddExpenseScreen` based on `:design` specifications, allowing users to see the current category (or "Uncategorized") and trigger selection.
- **Category Selection Screen**: Provide a category selection interface matching `:design/features/selectcategory`, supporting selecting an existing category, clearing assignment (Uncategorized), searching, and navigating to category creation.
- **Add Category Screen**: Provide a category creation screen matching `:design/features/addcategory`, supporting naming, color selection, and icon selection.

## Capabilities

### New Capabilities
- `category-management`: Manages user categories (creation, persistence, and listing).

### Modified Capabilities
- `expense-management`: Extends expense recording with an optional category selection and assignment.

## Impact

- Database schema updated to version 5 with migration `MIGRATION_4_5`.
- `ExpenseEntity`, `ExpenseDao`, and `ExpenseRepository` updated to support `categoryId`.
- Root navigation delegates adding an expense to `AddExpenseFlowComponent`.
- New UI screens and Decompose components in `app/shared`.
