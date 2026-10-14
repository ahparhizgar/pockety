# Spec Delta

## ADDED Requirements

### Requirement: Display auto-track SMS banner when permission missing
The system SHALL display the "Auto-track bank SMS" banner on the home screen when SMS permission is not granted.

#### Scenario: User has not granted SMS permission
- **WHEN** the user views the home screen and SMS permission is not granted
- **THEN** the system displays the "Auto-track bank SMS" banner inviting the user to enable auto-tracking

#### Scenario: User taps auto-track SMS banner
- **WHEN** the user taps the "Auto-track bank SMS" banner
- **THEN** the system requests SMS permission or directs the user to app settings if previously permanently denied

### Requirement: Display undecided expenses banner when permission granted
The system SHALL display the "Undecided expenses" banner on the home screen when SMS permission is granted.

#### Scenario: SMS permission is granted
- **WHEN** the user views the home screen and SMS permission is granted
- **THEN** the system replaces the SMS permission banner with the "Undecided expenses" banner showing 0 pending review items
