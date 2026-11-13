# Spec Delta

## MODIFIED Requirements

### Requirement: Settings UI for calendar system selection
The system SHALL provide a settings screen accessible from the main navigation Settings tab that allows the user to inspect and select their preferred calendar system using structured option cards displaying title, descriptive subtitle, and calendar symbol indicator.

#### Scenario: Display current calendar preference in settings
- **WHEN** the user navigates to the Settings tab
- **THEN** the Settings screen displays the calendar system cards showing Gregorian and Persian options, with the active calendar system highlighted as selected

#### Scenario: Select calendar system option
- **WHEN** the user selects the Persian calendar option card on the Settings screen
- **THEN** the UI updates to show the Persian option card as selected and commits the change to persistent storage
