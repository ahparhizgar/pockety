# Category Management Specification

## Purpose

Enables users to create and manage custom expense categories with distinct names, colors, and icons for organizing financial records.

## Requirements

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

### Requirement: Browse categories independently from Settings
The system SHALL open a standalone category destination from Settings → Categories, showing existing category names, icons and colors, or an empty state when none exist. The destination SHALL offer category creation without selecting or assigning categories to an expense or pocket.

#### Scenario: Open existing categories
- **WHEN** the user activates Categories in Settings with existing categories
- **THEN** a category destination shows those categories and an add action without selection controls or an Uncategorized assignment action

#### Scenario: Open an empty category collection
- **WHEN** the user opens Categories with no saved categories
- **THEN** an explanatory empty state and an add action are visible

### Requirement: Create categories from the standalone destination
The system SHALL let users open the existing category creation form from the standalone destination and return to that destination after saving or cancelling. Successful creation SHALL update the displayed collection and Settings category count.

#### Scenario: Save a category
- **WHEN** the user saves a valid category opened from Settings Categories
- **THEN** the destination shows the new category with its saved name, icon and color and Settings reflects the updated count on return

#### Scenario: Cancel category creation
- **WHEN** the user returns from the creation form without saving
- **THEN** the category destination is restored without adding a category

### Requirement: Return from category browsing safely
The system SHALL return from the category destination to the same Settings context using visible Back or platform Back, preserving settings preferences and existing expense and pocket category associations.

#### Scenario: Return to Settings
- **WHEN** the user invokes Back from the category destination
- **THEN** Settings remains the selected tab with its existing preference values and no expense or pocket association is changed

### Requirement: Accessible localized category browsing
The standalone category journey SHALL support touch and keyboard operation, English and Persian UI resources, language-appropriate layout direction and usable compact and large-screen layouts.

#### Scenario: Keyboard journey
- **WHEN** the user operates Settings Categories, Add, the creation form, Save and Back using the keyboard
- **THEN** the complete browse-create-return journey is operable with visible focus and no touch dependency

#### Scenario: Persian and large screen
- **WHEN** the user opens the destination in Persian or at a large window size
- **THEN** labels use the active language, Persian layout follows RTL, and content remains readable with adaptive category columns and bounded centered content
