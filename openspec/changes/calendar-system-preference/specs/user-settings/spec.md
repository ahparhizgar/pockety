# Spec Delta

## Purpose

Provides persistent storage, retrieval, and reactive observation for user-configurable application preferences, starting with calendar system selection.

## ADDED Requirements

### Requirement: Calendar system preference storage and default
The system SHALL provide persistent storage for the user's preferred calendar system, defaulting to GREGORIAN when not explicitly configured, and supporting switching to PERSIAN or GREGORIAN.

#### Scenario: Default calendar system preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the active calendar system preference is GREGORIAN

#### Scenario: Update and persist calendar system preference
- **WHEN** the user selects PERSIAN as their calendar system in settings
- **THEN** the preference is saved to persistent storage and the active calendar system becomes PERSIAN

#### Scenario: Observe calendar preference reactively
- **WHEN** the calendar system preference is changed from GREGORIAN to PERSIAN
- **THEN** all observing components and screens receive an update with the new PERSIAN calendar system

### Requirement: Settings UI for calendar system selection
The system SHALL provide a settings screen accessible from the main navigation Settings tab that allows the user to inspect and select their preferred calendar system.

#### Scenario: Display current calendar preference in settings
- **WHEN** the user navigates to the Settings tab
- **THEN** the Settings screen displays the currently active calendar system preference

#### Scenario: Select calendar system option
- **WHEN** the user selects the Persian calendar option on the Settings screen
- **THEN** the UI updates to show Persian calendar as selected and commits the change to persistent storage
