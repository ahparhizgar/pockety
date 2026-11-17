# Spec Delta

## ADDED Requirements

### Requirement: Rounded settings option feedback
The system SHALL confine ripple and pressed feedback for display currency and calendar option cards to their visible 16.dp rounded boundary in selected and unselected states, preserving existing styling, dimensions, and shadows.

#### Scenario: Press currency options near corners
- **GIVEN** either currency option is selected or unselected
- **WHEN** the user presses near a corner inside the visible card
- **THEN** ripple and pressed feedback remain inside the rounded boundary
- **AND** card appearance and dimensions remain unchanged

#### Scenario: Press calendar options near corners
- **GIVEN** either calendar option is selected or unselected
- **WHEN** the user presses near a corner inside the visible card
- **THEN** ripple and pressed feedback remain inside the rounded boundary
- **AND** card appearance and dimensions remain unchanged

### Requirement: Accessible settings option activation
The system SHALL preserve radio selection semantics, keyboard focus and activation, and a single selection action for each currency and calendar option card. Selection SHALL continue updating and persisting the corresponding preference.

#### Scenario: Select options using keyboard
- **GIVEN** the Settings screen is displayed
- **WHEN** the user focuses and activates a currency or calendar option with the keyboard
- **THEN** that option is exposed as a selected radio option and its sibling as unselected
- **AND** the corresponding preference is saved

#### Scenario: Activate an already selected option
- **GIVEN** a currency or calendar option is selected
- **WHEN** the user activates that option
- **THEN** it remains selected and activation dispatches only one selection action
