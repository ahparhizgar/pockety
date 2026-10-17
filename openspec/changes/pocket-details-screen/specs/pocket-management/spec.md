# Spec Delta

## Purpose

Enables users to inspect pocket details, quickly top up pocket balances via an accessible modal bottom sheet, navigate to pocket configuration, and view pocket transaction entries.

## ADDED Requirements

### Requirement: Pocket Details Inspection
The system SHALL display detailed information for a selected pocket, including its name, visual icon, assigned accent color, formatted balance in Tomans, and action options.

#### Scenario: Navigating to pocket details from pocket list
- **WHEN** the user selects a pocket from the pocket list
- **THEN** the system displays the pocket details screen with the pocket's name, icon, accent color, and formatted current balance in Tomans

#### Scenario: Navigating back from pocket details
- **WHEN** the user clicks the back button in the top app bar
- **THEN** the system navigates back to the pocket list screen

### Requirement: Quick Top-Up Modal Bottom Sheet
The system SHALL provide a modal bottom sheet allowing users to quickly increase a pocket's budget balance using preset quick-amount options or custom numeric input.

#### Scenario: Opening quick top-up bottom sheet
- **WHEN** the user taps the Top-up button on the pocket details screen
- **THEN** the system presents the quick top-up modal bottom sheet showing preset amount chips and an amount input field

#### Scenario: Confirming top-up with preset amount
- **WHEN** the user selects a preset amount chip and confirms top-up
- **THEN** the pocket balance increases by the selected amount, the modal bottom sheet dismisses, and a confirmation feedback message is displayed

#### Scenario: Confirming top-up with custom amount
- **WHEN** the user enters a valid positive custom amount and confirms top-up
- **THEN** the pocket balance increases by the entered amount, the modal bottom sheet dismisses, and a confirmation feedback message is displayed

#### Scenario: Dismissing quick top-up bottom sheet
- **WHEN** the user dismisses the quick top-up bottom sheet without confirming
- **THEN** the bottom sheet closes and the pocket balance remains unchanged

### Requirement: Pocket Configuration Navigation
The system SHALL provide navigation from pocket details to pocket settings for editing pocket configuration.

#### Scenario: Launching configuration from action button
- **WHEN** the user taps the Configure button in the action bar
- **THEN** the system launches the pocket settings flow in edit mode for the current pocket

#### Scenario: Launching configuration from top app bar
- **WHEN** the user taps the settings icon in the top app bar
- **THEN** the system launches the pocket settings flow in edit mode for the current pocket

### Requirement: Pocket Transaction Feed Presentation
The system SHALL render a dedicated transactions section on the pocket details screen displaying transaction rows in the UI layer.

#### Scenario: Displaying transaction items
- **WHEN** viewing the pocket details screen
- **THEN** the system displays the expenses section header and minimalist transaction rows displaying the merchant or category icon, title, timestamp, note, and formatted deduction amount in Tomans
