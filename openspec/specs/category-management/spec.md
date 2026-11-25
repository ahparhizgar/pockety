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
The system SHALL open a standalone category destination from Settings → Categories, showing existing category names, icons and colors in the user's manual category order, or an empty state when none exist. The destination SHALL offer category creation without selecting or assigning categories to an expense or pocket.

#### Scenario: Open existing categories
- **WHEN** the user activates Categories in Settings with existing categories
- **THEN** a category destination shows those categories in the user's manual order and an add action without selection controls or an Uncategorized assignment action

#### Scenario: Open an empty category collection
- **WHEN** the user opens Categories with no saved categories
- **THEN** an explanatory empty state and an add action are visible

#### Scenario: Filter categories in the management destination
- **WHEN** the user types a search query in the management destination matching some category names
- **THEN** only the matching categories are shown in the list

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
- **WHEN** the user operates Settings Categories, Add, Delete, the creation form, Save and Back using the keyboard
- **THEN** the complete browse-create-delete-return journey is operable with visible focus and no touch dependency

#### Scenario: Persian and large screen
- **WHEN** the user opens the destination in Persian or at a large window size
- **THEN** labels use the active language, Persian layout follows RTL, and content remains readable as a bounded centered list

### Requirement: Delete category from the standalone destination
The system SHALL offer a delete action for each category in the standalone category destination. Activating it SHALL ask for confirmation, naming the category, before anything is deleted. Confirming SHALL remove the category from the destination, from category selection, and from the Settings category count, and expenses and pockets that referenced it SHALL become uncategorized. Cancelling SHALL leave everything unchanged.

#### Scenario: Delete a category with confirmation
- **WHEN** the user activates the delete action for a category and confirms the deletion
- **THEN** the category is removed from the list and is no longer offered in category selection

#### Scenario: Cancel a category deletion
- **WHEN** the user activates the delete action for a category and cancels the confirmation
- **THEN** the category remains in the list and nothing else changes

#### Scenario: Expenses of a deleted category become uncategorized
- **WHEN** a category that is assigned to existing expenses or pockets is deleted
- **THEN** those expenses and pockets keep all their other data and show as uncategorized

### Requirement: Reorder categories manually
The system SHALL let the user reorder categories on the standalone Categories destination by dragging a row's drag handle to a new position, and SHALL persist the resulting order immediately. The same manual order SHALL be used by every category list, including the category selection page, where the Uncategorized entry remains first and outside the manual order. Reordering SHALL be fully operable with a keyboard: a focused drag handle moves its row up or down with the arrow keys. A newly created category SHALL be appended at the end of the order. Upgrading the app SHALL preserve the order categories were displayed in before the upgrade until the user reorders them.

#### Scenario: Drag a category to a new position
- **WHEN** the user drags a category row by its handle above another category and drops it
- **THEN** the list shows the new order, the order is persisted, and reopening the destination keeps it

#### Scenario: Reorder with the keyboard
- **WHEN** the user focuses a row's drag handle and presses the down arrow key
- **THEN** the category moves one position down and the focus stays on its handle

#### Scenario: Selection follows the manual order
- **WHEN** the user has reordered categories and then opens the category selection page
- **THEN** the categories appear in the same manual order below the Uncategorized entry

#### Scenario: New category is appended
- **WHEN** the user creates a category after having reordered the list
- **THEN** the new category appears at the end of the order

#### Scenario: Search keeps the manual order
- **WHEN** the user searches on the standalone destination or the selection page
- **THEN** the matching categories keep their relative manual order
