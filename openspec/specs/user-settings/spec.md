# user-settings Specification

## Purpose
Provides persistent storage, retrieval, and reactive observation for user-configurable application preferences, starting with calendar system selection.

## Requirements

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
The system SHALL provide a settings screen accessible from the main navigation Settings tab that allows the user to inspect and select their preferred calendar system using structured option cards displaying title, descriptive subtitle, and calendar symbol indicator.

#### Scenario: Display current calendar preference in settings
- **WHEN** the user navigates to the Settings tab
- **THEN** the Settings screen displays the calendar system cards showing Gregorian and Persian options, with the active calendar system highlighted as selected

#### Scenario: Select calendar system option
- **WHEN** the user selects the Persian calendar option card on the Settings screen
- **THEN** the UI updates to show the Persian option card as selected and commits the change to persistent storage

### Requirement: Display currency preference storage and default
The system SHALL provide persistent storage for the user's preferred display currency, defaulting to TOMANS when not explicitly configured, and supporting switching between TOMANS and RIALS.

#### Scenario: Default currency preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the active currency preference is TOMANS

#### Scenario: Update and persist currency preference
- **WHEN** the user selects Rials as their display currency in settings
- **THEN** the preference is saved to persistent storage and the active currency becomes RIALS

#### Scenario: Observe currency preference reactively
- **WHEN** the currency preference is changed from TOMANS to RIALS
- **THEN** observing components receive an update with the new RIALS currency preference

### Requirement: End-of-day reminder preference storage and default
The system SHALL provide persistent storage for enabling or disabling the daily end-of-day reminder notification, defaulting to enabled (true).

#### Scenario: Default end-of-day reminder preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the end-of-day reminder preference is enabled (true)

#### Scenario: Toggle end-of-day reminder preference
- **WHEN** the user toggles the end-of-day reminder switch to disabled (false)
- **THEN** the preference is saved to persistent storage as disabled

### Requirement: Full Settings screen section hierarchy and navigation
The system SHALL organize the Settings screen into defined visual sections matching the design prototype: Organize & Taxonomy (Categories count badge and Labels navigation rows), Preferences & Region (Language row, Display Currency card, and Calendar System card), Notifications & Automation (End-of-day reminder switch), and Application Version footer.

#### Scenario: Display full settings section hierarchy
- **WHEN** the user navigates to the Settings tab
- **THEN** the screen renders Organize & Taxonomy, Preferences & Region, Notifications & Automation sections, and the app version badge

#### Scenario: Navigate to categories from settings
- **WHEN** the user taps the Categories row in the Organize & Taxonomy section
- **THEN** the settings component dispatches navigation to the category management flow

#### Scenario: Select display currency option card
- **WHEN** the user taps the Rials currency option card in the Preferences & Region section
- **THEN** the UI updates to show Rials as selected and commits the change to persistent storage

#### Scenario: Toggle end-of-day reminder
- **WHEN** the user interacts with the end-of-day reminder switch
- **THEN** the switch updates its checked state and commits the preference to persistent storage
