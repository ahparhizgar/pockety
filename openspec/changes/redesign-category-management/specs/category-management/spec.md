# Spec Delta

## MODIFIED Requirements

### Requirement: Browse categories independently from Settings
The system SHALL open a standalone category destination from Settings → Categories, showing existing categories as a single-column list modeled on the category selection screen — each row shows the category's icon on a tile filled with its color, and its name — or an empty state when none exist. The destination SHALL offer a search field that filters the list by category name and a category creation action, without selecting or assigning categories to an expense or pocket.

#### Scenario: Open existing categories
- **WHEN** the user activates Categories in Settings with existing categories
- **THEN** a category destination shows those categories as list rows with their icons and colors and an add action, without selection controls, a selected state, or an Uncategorized assignment action

#### Scenario: Open an empty category collection
- **WHEN** the user opens Categories with no saved categories
- **THEN** an explanatory empty state and an add action are visible

#### Scenario: Filter categories in the management destination
- **WHEN** the user types a search query in the management destination matching some category names
- **THEN** only the matching categories are shown in the list

### Requirement: Accessible localized category browsing
The standalone category journey SHALL support touch and keyboard operation, English and Persian UI resources, language-appropriate layout direction and usable compact and large-screen layouts.

#### Scenario: Keyboard journey
- **WHEN** the user operates Settings Categories, Add, Delete, the creation form, Save and Back using the keyboard
- **THEN** the complete browse-create-delete-return journey is operable with visible focus and no touch dependency

#### Scenario: Persian and large screen
- **WHEN** the user opens the destination in Persian or at a large window size
- **THEN** labels use the active language, Persian layout follows RTL, and content remains readable as a bounded centered list

## ADDED Requirements

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
