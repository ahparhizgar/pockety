# Spec Delta

## MODIFIED Requirements

### Requirement: Record expense
The system SHALL allow users to record a new expense with an amount, an optional description, an optional category assignment, and an optional pocket assignment.

#### Scenario: Successfully record an expense
- **WHEN** user enters a valid positive amount and a description and confirms saving
- **THEN** the expense is saved locally and displayed in the expense list

#### Scenario: Successfully record an expense without description
- **WHEN** user enters a valid positive amount, leaves the description blank or empty, and confirms saving
- **THEN** the expense is saved locally without validation error and displayed in the expense list

#### Scenario: Successfully record an expense with a category
- **WHEN** user enters a valid positive amount, a description, selects a category, and confirms saving
- **THEN** the expense is saved locally with the assigned category ID and displayed in the expense list

#### Scenario: Successfully record an expense without a category
- **WHEN** user enters a valid positive amount and a description without choosing a category (or explicitly choosing Uncategorized) and confirms saving
- **THEN** the expense is saved locally with no category assignment and displayed in the expense list

#### Scenario: Successfully record an expense with a linked pocket
- **WHEN** user enters a valid positive amount, a description, has a pocket assigned (automatically or manually), and confirms saving
- **THEN** the expense is saved locally with the assigned pocket ID and the pocket's balance deducts the expense amount

#### Scenario: Successfully record an expense without a pocket
- **WHEN** user saves an expense without any pocket assigned or after unlinking
- **THEN** the expense is saved locally with no pocket ID and no pocket balance is altered

#### Scenario: Reject empty or zero amount
- **WHEN** user attempts to save an expense with an empty or non-positive amount
- **THEN** the system prevents saving and displays a validation error

### Requirement: View expenses list
The system SHALL display the recorded expenses in reverse-chronological order on the home screen.

#### Scenario: Display recorded expenses
- **WHEN** user opens the application with previously recorded expenses
- **THEN** all expenses are shown with their amount and description

#### Scenario: Display recorded expenses without category or description
- **WHEN** an expense has no category assigned and no description (or blank description)
- **THEN** the expense item displays only the amount without an empty description placeholder or title

#### Scenario: Display empty state
- **WHEN** no expenses have been recorded
- **THEN** the system indicates an empty expense history
