# Spec Delta

## Purpose

Enables users to create and manage custom expense categories with distinct names, colors, and icons for organizing financial records.

## ADDED Requirements

### Requirement: Create custom category
The system SHALL allow users to create a new expense category by specifying a unique name, an accent color, and an icon.

#### Scenario: Successfully create a category
- **WHEN** user enters a non-blank category name, chooses an accent color and an icon, and confirms creation
- **THEN** the new category is saved locally and made available for expense selection

#### Scenario: Prevent category creation with empty name
- **WHEN** user attempts to save a category with an empty or blank name
- **THEN** the system prevents saving and requires a valid name

### Requirement: Select category for expense
The system SHALL allow users to select from existing categories or choose "Uncategorized" when recording an expense.

#### Scenario: Select an existing category
- **WHEN** user chooses an existing category in the category selection screen
- **THEN** the selection is registered and the user is returned to the expense form with the category applied

#### Scenario: Select Uncategorized option
- **WHEN** user chooses the "Uncategorized" option in the category selection screen
- **THEN** any category assignment is cleared and the expense form shows "Uncategorized"

### Requirement: Search categories
The system SHALL allow users to filter categories by search term in the category selection screen.

#### Scenario: Filter matches category name
- **WHEN** user types a search query matching existing category names
- **THEN** only matching categories are shown in the selection list

#### Scenario: Empty search results
- **WHEN** user types a search query matching no existing categories
- **THEN** an empty state is displayed with an option to create a new category
