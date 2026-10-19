# Spec Delta

## Purpose

Enables tracking pocket balances dynamically derived from top-up deposits minus expenses, records auditable top-up transactions, and displays linked expenses alongside top-ups within pocket details.

## ADDED Requirements

### Requirement: Pocket Top-Up Transaction Recording
The system SHALL record pocket top-ups as individual transaction entries associated with a pocket, capturing the amount, optional description, and timestamp.

#### Scenario: Topping up a pocket
- **WHEN** a top-up of amount 100,000 Tomans is performed for a pocket with an optional description
- **THEN** the system stores a top-up transaction entry linked to that pocket with the amount, description, and creation timestamp

#### Scenario: Initial pocket creation allocation
- **WHEN** a new pocket is created with an initial allocation amount
- **THEN** the system creates the pocket and automatically records an initial top-up transaction entry for that amount

### Requirement: Pocket Balance Derived from Top-Ups and Expenses
The system SHALL calculate the available balance of a pocket as the sum of all top-up transaction amounts for that pocket minus the sum of all expenses linked to that pocket.

#### Scenario: Pocket balance calculation with top-ups and expenses
- **WHEN** a pocket has top-up transactions totaling 500,000 Tomans and linked expenses totaling 120,000 Tomans
- **THEN** the calculated balance for the pocket is 380,000 Tomans

#### Scenario: Pocket balance when expenses exceed top-ups
- **WHEN** a pocket has top-up transactions totaling 200,000 Tomans and linked expenses totaling 250,000 Tomans
- **THEN** the calculated balance is -50,000 Tomans and the pocket indicates an over-budget status

### Requirement: Pocket Details Combined Activity Feed
The system SHALL display an integrated chronological activity list in pocket details showing both linked expenses and top-up transactions.

#### Scenario: Viewing combined pocket transactions
- **WHEN** the user views the pocket details screen for a pocket with both top-up transactions and linked expenses
- **THEN** the system displays all transactions ordered by timestamp descending, presenting top-ups with a positive amount indicator and expenses with a deduction indicator

#### Scenario: Pocket details with no transactions
- **WHEN** a pocket has no top-ups and no linked expenses
- **THEN** the system displays an empty transactions placeholder
