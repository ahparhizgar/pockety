# Expense Management Specification

## Purpose

Provides local recording and viewing of simple expenses without requiring network connectivity or authentication.

## Requirements

### Requirement: Record expense
The system SHALL allow users to record a new expense with an amount and an optional description.

#### Scenario: Successfully record an expense
- **WHEN** user enters a valid positive amount and a description and confirms saving
- **THEN** the expense is saved locally and displayed in the expense list

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
