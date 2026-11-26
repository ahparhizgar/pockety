# Spec Delta

## REMOVED Requirements

### Requirement: Automatic pocket assignment upon category selection
The system SHALL automatically associate an expense with a budget pocket when a category configured on that pocket is selected.

#### Scenario: Category matches an existing pocket
- **WHEN** user selects a category associated with a budget pocket
- **THEN** the pocket is automatically assigned to the expense and displayed in the pocket selector card with its name, icon, and remaining balance

#### Scenario: Category does not match any pocket
- **WHEN** user selects a category that is not linked to any budget pocket
- **THEN** no pocket is automatically assigned and the pocket selector card remains unlinked

### Requirement: Manual pocket assignment and unlinking
The system SHALL allow users to manually reassign or clear the budget pocket associated with an expense at any time.

#### Scenario: User unlinks an assigned pocket
- **WHEN** user taps the unlink button on an assigned pocket card
- **THEN** the pocket assignment is cleared while preserving the selected category

## ADDED Requirements

### Requirement: Pocket derived solely from category
The system SHALL derive the pocket for a new expense solely from its
category: selecting a category assigns the pocket configured with that
category, and the user SHALL NOT pick a pocket separately. A category
is mandatory when recording an expense.

#### Scenario: Category selected
- **WHEN** the user selects a category while adding an expense
- **THEN** the expense is assigned to the pocket configured with that category

#### Scenario: No category, no save
- **WHEN** the user tries to save an expense without selecting a category
- **THEN** the system blocks the save and asks for a category

#### Scenario: Uncategorized expense has no pocket
- **WHEN** an expense is recorded without a category (only where the
  product allows it — see proposal decision #5)
- **THEN** no pocket is assigned to it

#### Scenario: SMS expense without a detected category
- **WHEN** an SMS-parsed expense candidate has no detected category
- **THEN** no pocket is assigned to it
