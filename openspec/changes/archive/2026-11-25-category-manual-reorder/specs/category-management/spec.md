# Category Management — Delta

## MODIFIED Requirements

### Requirement: Browse categories independently from Settings
The system SHALL open a standalone category destination from Settings → Categories, showing existing category names, icons and colors in the user's manual category order, or an empty state when none exist. The destination SHALL offer category creation without selecting or assigning categories to an expense or pocket.

#### Scenario: Open existing categories
- **WHEN** the user activates Categories in Settings with existing categories
- **THEN** a category destination shows those categories in the user's manual order and an add action without selection controls or an Uncategorized assignment action

#### Scenario: Open an empty category collection
- **WHEN** the user opens Categories with no saved categories
- **THEN** an explanatory empty state and an add action are visible

## ADDED Requirements

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
