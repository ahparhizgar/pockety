# Spec Delta

## ADDED Requirements

### Requirement: Pocket Deletion Danger Zone
The system SHALL provide a danger zone card in pocket settings allowing users to delete an existing pocket. The danger zone card SHALL NOT be displayed when creating a new pocket.

#### Scenario: Danger zone visible in edit mode
- **WHEN** user opens pocket settings for an existing pocket (`pocketId != null`)
- **THEN** the system displays the Danger Zone card with a "Delete Pocket" action

#### Scenario: Danger zone hidden in create mode
- **WHEN** user opens pocket settings in creation mode (`pocketId == null`)
- **THEN** the system does not display the Danger Zone card

#### Scenario: Delete pocket confirmation and completion
- **WHEN** user clicks "Delete Pocket" in the Danger Zone card
- **THEN** the system deletes the pocket from storage, unlinks any associated expenses (setting their pocket reference to null), removes associated top-ups, and finishes the pocket settings flow
