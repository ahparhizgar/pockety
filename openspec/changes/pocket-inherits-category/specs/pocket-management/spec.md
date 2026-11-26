# Spec Delta

## REMOVED Requirements

### Requirement: Associated Category Configuration
The system SHALL allow users to associate an existing category or create a new category within the pocket settings flow, as well as clear an association.

#### Scenario: Linking an existing category
- **WHEN** user selects a category from the category selection sub-screen within the flow
- **THEN** the system returns to pocket settings displaying the linked category name and icon

#### Scenario: Clearing linked category
- **WHEN** user clears the associated category in pocket settings
- **THEN** the pocket settings updates to indicate no category is associated

## ADDED Requirements

### Requirement: Category-first pocket creation
The system SHALL require a category when creating a pocket: the user
first selects a category and the pocket inherits that category's name,
icon and color. At most one pocket SHALL exist per category.

#### Scenario: Creating a pocket starts from a category
- **WHEN** the user starts creating a pocket
- **THEN** the system first asks for a category before the pocket details

#### Scenario: Pocket inherits category identity
- **WHEN** the user picks a category while creating a pocket
- **THEN** the pocket name, icon and color are prefilled from that category

#### Scenario: Second pocket for the same category is rejected
- **WHEN** the user tries to save a pocket for a category that already has one
- **THEN** the system rejects the save with an error naming the existing pocket

### Requirement: Pocket identity follows its category
The system SHALL keep a pocket's displayed name and icon in sync with
its category's identity at creation time (whether they stay editable
afterwards is decision #2 in the proposal).

#### Scenario: Category badge is redundant
- **WHEN** a pocket is shown in the pockets list under the 1:1 model
- **THEN** no separate category tag badge is shown, because the pocket
  itself is the category's budget
