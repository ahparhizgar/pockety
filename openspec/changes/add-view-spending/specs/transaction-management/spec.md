# Spec Delta

## Purpose

Provides local recording and viewing of simple expense transactions without requiring network connectivity or authentication.

## ADDED Requirements

### Requirement: Record expense transaction
The system SHALL allow users to record a new transaction with an amount and an optional description.

#### Scenario: Successfully record a transaction
- **WHEN** user enters a valid positive amount and a description and confirms saving
- **THEN** the transaction is saved locally and displayed in the transaction list

#### Scenario: Reject empty or zero amount
- **WHEN** user attempts to save a transaction with an empty or non-positive amount
- **THEN** the system prevents saving and displays a validation error

### Requirement: View transactions list
The system SHALL display the recorded transactions in reverse-chronological order on the home screen.

#### Scenario: Display recorded transactions
- **WHEN** user opens the application with previously recorded transactions
- **THEN** all transactions are shown with their amount and description

#### Scenario: Display empty state
- **WHEN** no transactions have been recorded
- **THEN** the system indicates an empty transaction history
