# Spec Delta

## ADDED Requirements

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
