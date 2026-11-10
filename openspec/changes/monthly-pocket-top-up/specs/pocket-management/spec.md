# Spec Delta

## ADDED Requirements

### Requirement: Pocket Monthly Top-Up Configuration
The system SHALL allow users to configure recurring monthly top-up settings for a budget pocket within the pocket settings flow, including setting a monthly allocation amount and enabling or disabling auto-refill.

#### Scenario: User configures monthly allocation amount and enables auto-refill
- **WHEN** user enters a valid positive monthly top-up amount and enables the auto-refill toggle in pocket settings
- **THEN** the monthly top-up settings are persisted with the pocket upon saving

#### Scenario: User disables auto-refill for a pocket
- **WHEN** user turns off the auto-refill switch in pocket settings and saves
- **THEN** the pocket is saved with auto-refill disabled and will not be automatically topped up at the start of new months

#### Scenario: Display existing monthly top-up configuration in edit mode
- **WHEN** user opens pocket settings for an existing pocket that has monthly auto-refill configured
- **THEN** the pocket settings screen displays the configured monthly allocation amount and auto-refill toggle state

### Requirement: Automatic On-App-Open Monthly Pocket Top-Up
The system SHALL evaluate all active pockets on application launch and automatically generate a top-up transaction for each pocket that has auto-refill enabled and has not yet received its monthly top-up for the current calendar month. The system SHALL NOT schedule or rely on system background workers or alarms for monthly top-up execution.

#### Scenario: App launch in a new month with auto-refill enabled pocket
- **WHEN** the application opens in a new calendar month (e.g. November) and a pocket has auto-refill enabled with an allocation of 500,000 Tomans and has not been topped up for November
- **THEN** the system generates a pocket top-up transaction for 500,000 Tomans with description "Monthly Top-Up", updates the pocket's balance, and marks the pocket as topped up for the current month

#### Scenario: App launch when monthly top-up was already applied
- **WHEN** the application opens and a pocket with auto-refill enabled has already received its monthly top-up for the current calendar month
- **THEN** no duplicate top-up transaction is created and the pocket balance remains unchanged

#### Scenario: App launch with auto-refill disabled
- **WHEN** the application opens and a pocket has auto-refill disabled (even if monthly amount is set)
- **THEN** no automatic top-up transaction is generated for that pocket

#### Scenario: App launch across multiple elapsed months
- **WHEN** the application opens after several months without being opened and a pocket has auto-refill enabled
- **THEN** the system executes the recurring top-up for the current due cycle, records the top-up transaction, and advances the pocket's last top-up month without generating redundant duplicate runs for missed inactive periods
