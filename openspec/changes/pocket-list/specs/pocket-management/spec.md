# Spec Delta

## Purpose

Enables users to monitor budget pockets, view remaining balances dynamically updated against linked expenses, and access the pocket list from the bottom navigation bar.

## ADDED Requirements

### Requirement: Pocket List Presentation
The system SHALL display all configured budget pockets on the pockets screen accessible via the bottom navigation bar.

#### Scenario: Pockets tab selected
- **WHEN** user selects the Pockets tab in the bottom bar
- **THEN** the system displays the Pockets screen with the active budget pockets

#### Scenario: Empty pocket state
- **WHEN** user opens the Pockets screen and no pockets exist
- **THEN** the system displays an empty state indicating no budget pockets are configured

### Requirement: Pocket Balance Calculation
The system SHALL calculate and display the remaining balance for each pocket as the pocket budget amount minus the sum of transactions linked to that pocket.

#### Scenario: Pocket with no linked expenses
- **WHEN** a pocket has a budget amount of 1,000,000 and 0 linked transactions
- **THEN** the displayed balance is equal to the full budget amount

#### Scenario: Pocket with linked expenses within budget
- **WHEN** a pocket has a budget amount of 1,000,000 and linked transactions summing to 150,000
- **THEN** the displayed balance is 850,000 and the pocket is not marked as over-budget

#### Scenario: Pocket exceeding budget amount
- **WHEN** a pocket has a budget amount of 500,000 and linked transactions summing to 565,000
- **THEN** the pocket indicates an over-budget status and warning

### Requirement: Pocket Action Handlers
The system SHALL handle user interactions on pocket cards and list controls gracefully.

#### Scenario: User clicks pocket card
- **WHEN** user taps a pocket budget card
- **THEN** the interaction is handled without crashing (delegating to pocket detail navigation handler)

#### Scenario: User taps Add Pocket action
- **WHEN** user taps the Add Pocket floating action button
- **THEN** the interaction is handled without error as an initial placeholder
