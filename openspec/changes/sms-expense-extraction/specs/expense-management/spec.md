# Spec Delta

## MODIFIED Requirements

### Requirement: Display undecided expenses banner when permission granted
The system SHALL display the "Undecided expenses" banner on the home screen when SMS permission is granted, indicating the current count and total amount of pending candidate expenses, and navigating to the Inbox screen when clicked.

#### Scenario: SMS permission is granted with pending candidate expenses
- **WHEN** the user views the home screen, SMS permission is granted, and pending candidate expenses exist
- **THEN** the system displays the "Undecided expenses" banner showing the count and formatted sum of pending review items

#### Scenario: Tap undecided expenses banner
- **WHEN** the user taps the "Undecided expenses" banner
- **THEN** the system navigates to the Inbox screen
