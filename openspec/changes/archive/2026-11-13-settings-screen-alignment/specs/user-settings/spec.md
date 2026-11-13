# Spec Delta

## ADDED Requirements

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
