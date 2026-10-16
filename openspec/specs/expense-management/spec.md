# Expense Management Specification

## Purpose

Provides local recording and viewing of simple expenses without requiring network connectivity or authentication.

## Requirements

### Requirement: Record expense
The system SHALL allow users to record a new expense with an amount, an optional description, and an optional category assignment.

#### Scenario: Successfully record an expense
- **WHEN** user enters a valid positive amount and a description and confirms saving
- **THEN** the expense is saved locally and displayed in the expense list

#### Scenario: Successfully record an expense with a category
- **WHEN** user enters a valid positive amount, a description, selects a category, and confirms saving
- **THEN** the expense is saved locally with the assigned category ID and displayed in the expense list

#### Scenario: Successfully record an expense without a category
- **WHEN** user enters a valid positive amount and a description without choosing a category (or explicitly choosing Uncategorized) and confirms saving
- **THEN** the expense is saved locally with no category assignment and displayed in the expense list

#### Scenario: Reject empty or zero amount
- **WHEN** user attempts to save an expense with an empty or non-positive amount
- **THEN** the system prevents saving and displays a validation error

### Requirement: View expenses list
The system SHALL display the recorded expenses in reverse-chronological order on the home screen.

#### Scenario: Display recorded expenses
- **WHEN** user opens the application with previously recorded expenses
- **THEN** all expenses are shown with their amount and description

#### Scenario: Display empty state
- **WHEN** no expenses have been recorded
- **THEN** the system indicates an empty expense history

### Requirement: Display auto-track SMS banner when permission missing
The system SHALL display the "Auto-track bank SMS" banner on the home screen when SMS permission is not granted.

#### Scenario: User has not granted SMS permission
- **WHEN** the user views the home screen and SMS permission is not granted
- **THEN** the system displays the "Auto-track bank SMS" banner inviting the user to enable auto-tracking

#### Scenario: User taps auto-track SMS banner
- **WHEN** the user taps the "Auto-track bank SMS" banner
- **THEN** the system requests SMS permission or directs the user to app settings if previously permanently denied

### Requirement: Display undecided expenses banner when permission granted
The system SHALL display the "Undecided expenses" banner on the home screen when SMS permission is granted, indicating the current count and total amount of pending candidate expenses, and navigating to the Inbox screen when clicked.

#### Scenario: SMS permission is granted with pending candidate expenses
- **WHEN** the user views the home screen, SMS permission is granted, and pending candidate expenses exist
- **THEN** the system displays the "Undecided expenses" banner showing the count and formatted sum of pending review items

#### Scenario: Tap undecided expenses banner
- **WHEN** the user taps the "Undecided expenses" banner
- **THEN** the system navigates to the Inbox screen

