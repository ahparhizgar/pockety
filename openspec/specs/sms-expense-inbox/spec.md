# SMS Expense Inbox Specification

## Purpose

Provides background bank transaction SMS extraction, candidate expense persistence with review statuses, an inbox review interface, and quick conversion of detected transactions into saved expenses.

## Requirements

### Requirement: Extract transaction amount from incoming SMS
The system SHALL intercept incoming SMS messages in the background, match their text against bank transaction patterns, and extract the transaction amount in Rials.

#### Scenario: SMS matches bank withdrawal pattern
- **WHEN** an incoming SMS arrives containing bank withdrawal keywords and a Rial amount
- **THEN** the system extracts the numeric amount, sender, raw body, and received timestamp

#### Scenario: SMS does not match bank patterns
- **WHEN** an incoming SMS arrives that does not match transaction patterns
- **THEN** the system ignores the SMS and does not create a candidate expense

### Requirement: Persist candidate expense with status
The system SHALL store matched transaction candidate expenses locally with a status of PENDING, REJECTED, or ACCEPTED.

#### Scenario: Initial storage of detected candidate
- **WHEN** an incoming bank SMS is successfully parsed
- **THEN** a candidate expense entry is saved with status PENDING

### Requirement: Review pending candidate expenses in Inbox
The system SHALL present all PENDING candidate expenses in an Inbox screen and allow the user to review them.

#### Scenario: View pending candidate list
- **WHEN** the user opens the Inbox screen
- **THEN** the system displays all candidate expenses currently in PENDING status

#### Scenario: Empty inbox state
- **WHEN** there are no PENDING candidate expenses
- **THEN** the Inbox screen indicates that all items have been reviewed

### Requirement: Dismiss candidate expense
The system SHALL allow users to dismiss a candidate expense from the Inbox screen, updating its status to REJECTED.

#### Scenario: User dismisses a candidate
- **WHEN** the user dismisses an item from the Inbox list
- **THEN** the candidate expense's status is changed to REJECTED and it is removed from the pending list

### Requirement: Convert candidate expense to new expense
The system SHALL allow users to select a candidate expense from the Inbox screen to create an expense with the extracted amount pre-filled, and mark the candidate as ACCEPTED upon saving.

#### Scenario: Open add expense with pre-filled amount
- **WHEN** the user taps a pending candidate expense item in the Inbox screen
- **THEN** the system opens the add expense screen with the amount input pre-filled with the candidate's extracted amount

#### Scenario: Candidate marked as accepted on expense save
- **WHEN** the user successfully saves the expense originating from a candidate expense
- **THEN** the candidate expense's status is updated to ACCEPTED
