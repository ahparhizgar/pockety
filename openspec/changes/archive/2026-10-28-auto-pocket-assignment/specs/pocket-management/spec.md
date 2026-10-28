# Spec Delta

## ADDED Requirements

### Requirement: Unlink Expense Transaction from Pocket Details
The system SHALL allow users to unlink an expense transaction from a pocket within pocket details, disconnecting the transaction from the pocket and restoring the pocket's balance without deleting the underlying transaction from the database.

#### Scenario: User unlinks expense transaction
- **WHEN** the user activates the unlink action on an expense transaction in pocket details
- **THEN** the transaction's pocket assignment is cleared (pocket ID set to null), the pocket's balance increases by the unlinked transaction amount, and the transaction remains recorded in the user's expense history

#### Scenario: Top-up transactions cannot be unlinked
- **WHEN** the user views a top-up transaction entry in pocket details
- **THEN** no unlink action is displayed for top-up transactions
