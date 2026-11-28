# Spec Delta

## MODIFIED Requirements

### Requirement: Automatic pocket assignment upon category selection
The system SHALL automatically associate an expense with a budget pocket when a category linked to that pocket is selected. A pocket may be linked to several categories; selecting any of them assigns that pocket. When several pockets link the same category, the first matching pocket is assigned.

#### Scenario: Category matches an existing pocket
- **WHEN** user selects a category linked to a budget pocket
- **THEN** the pocket is automatically assigned to the expense and displayed in the pocket selector card with its name, icon, and remaining balance

#### Scenario: Category does not match any pocket
- **WHEN** user selects a category that is not linked to any budget pocket
- **THEN** no pocket is automatically assigned and the pocket selector card remains unlinked

#### Scenario: SMS expense without a detected category
- **WHEN** an SMS-parsed expense candidate has no detected category
- **THEN** no pocket is assigned to it
