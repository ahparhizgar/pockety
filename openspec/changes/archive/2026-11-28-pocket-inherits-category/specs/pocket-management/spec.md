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
The system SHALL start pocket creation by asking the user to pick zero or more categories. Picking exactly one category continues to the simple setup; picking none or several continues to the advanced setup. A "No Category" entry skips the pick and opens the advanced setup directly.

#### Scenario: Creating a pocket starts from categories
- **WHEN** the user starts creating a pocket
- **THEN** the system first asks them to pick categories before the pocket details

#### Scenario: One category picked
- **WHEN** the user picks exactly one category and continues
- **THEN** the simple setup opens with the pocket identity inherited from that category

#### Scenario: Zero or several categories picked
- **WHEN** the user picks no category or several categories and continues
- **THEN** the advanced setup opens with the picked categories as the pocket's links

### Requirement: Pocket identity follows its category in the simple setup
In the simple setup the system SHALL prefill the pocket's name, icon and color from its category and SHALL keep the name non-editable; the identity follows the category until the user switches to the advanced setup through a confirmation dialog, which makes the identity independently editable and allows linking several categories.

#### Scenario: Pocket inherits category identity
- **WHEN** the simple setup opens for a category
- **THEN** the pocket name, icon and color match the category and the name field is read-only

#### Scenario: Switching to the advanced setup
- **WHEN** the user confirms switching from the simple setup to the advanced setup
- **THEN** the identity becomes editable, the live link to the category is broken, and the current selection carries over as the first link

#### Scenario: Back from the advanced setup
- **WHEN** the user goes back from an advanced setup that was entered from the simple setup, before saving
- **THEN** the simple setup returns, manual edits are discarded, and the identity follows the category again

### Requirement: Pocket setup mode is derived when editing
When the settings of an existing pocket open, the system SHALL derive its setup mode: the pocket opens in the simple setup when it is linked to exactly one category and its name and icon exactly equal that category's; otherwise it opens in the advanced setup.

#### Scenario: Simple pocket reopens in the simple setup
- **WHEN** the user opens the settings of a pocket whose name and icon match its only category
- **THEN** the simple setup is shown with the identity following the category

#### Scenario: Customized pocket reopens in the advanced setup
- **WHEN** the user opens the settings of a pocket whose name or icon differs from its category, or that is linked to several categories or none
- **THEN** the advanced setup is shown with the identity editable

### Requirement: No category badge on pocket cards
Pocket cards SHALL NOT display a separate category label; the pocket's own name and icon identify it.

#### Scenario: Pocket shown in the pockets list
- **WHEN** a pocket is shown in the pockets list
- **THEN** no category tag badge appears on its card
