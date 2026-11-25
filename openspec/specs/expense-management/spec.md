# Expense Management Specification

## Purpose

Provides local recording and viewing of simple expenses without requiring network connectivity or authentication.

## Requirements

### Requirement: Record expense
The system SHALL allow users to record a new expense with an amount, an optional description, an optional category assignment, and an optional pocket assignment.

#### Scenario: Successfully record an expense
- **WHEN** user enters a valid positive amount and a description and confirms saving
- **THEN** the expense is saved locally and displayed in the expense list

#### Scenario: Successfully record an expense without description
- **WHEN** user enters a valid positive amount, leaves the description blank or empty, and confirms saving
- **THEN** the expense is saved locally without validation error and displayed in the expense list

#### Scenario: Successfully record an expense with a category
- **WHEN** user enters a valid positive amount, a description, selects a category, and confirms saving
- **THEN** the expense is saved locally with the assigned category ID and displayed in the expense list

#### Scenario: Successfully record an expense without a category
- **WHEN** user enters a valid positive amount and a description without choosing a category (or explicitly choosing Uncategorized) and confirms saving
- **THEN** the expense is saved locally with no category assignment and displayed in the expense list

#### Scenario: Successfully record an expense with a linked pocket
- **WHEN** user enters a valid positive amount, a description, has a pocket assigned (automatically or manually), and confirms saving
- **THEN** the expense is saved locally with the assigned pocket ID and the pocket's balance deducts the expense amount

#### Scenario: Successfully record an expense without a pocket
- **WHEN** user saves an expense without any pocket assigned or after unlinking
- **THEN** the expense is saved locally with no pocket ID and no pocket balance is altered

#### Scenario: Reject empty or zero amount
- **WHEN** user attempts to save an expense with an empty or non-positive amount
- **THEN** the system prevents saving and displays a validation error

### Requirement: View expenses list
The system SHALL display the recorded expenses on the Analyse screen filtered by the selected calendar month, grouped by date in reverse-chronological order, and SHALL NOT display the expenses list on the home screen.

#### Scenario: Display recorded expenses
- **WHEN** user views the Analyse screen with previously recorded expenses in the selected month
- **THEN** all expenses recorded in that calendar month are shown grouped by date in reverse-chronological order with their category icon, description, and amount

#### Scenario: Display recorded expenses without category or description
- **WHEN** an expense has no category assigned and no description (or blank description)
- **THEN** the expense item displays only the amount without an empty description placeholder or title

#### Scenario: Display empty state
- **WHEN** user views a month in the Analyse screen where no expenses have been recorded
- **THEN** the system indicates an empty expense history for that month

#### Scenario: Home screen does not show expense list
- **WHEN** user views the Home screen
- **THEN** no transaction list or empty-expenses placeholder is displayed on the Home screen

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

### Requirement: Automatic pocket assignment upon category selection
The system SHALL automatically associate an expense with a budget pocket when a category configured on that pocket is selected.

#### Scenario: Category matches an existing pocket
- **WHEN** user selects a category associated with a budget pocket
- **THEN** the pocket is automatically assigned to the expense and displayed in the pocket selector card with its name, icon, and remaining balance

#### Scenario: Category does not match any pocket
- **WHEN** user selects a category that is not linked to any budget pocket
- **THEN** no pocket is automatically assigned and the pocket selector card remains unlinked

### Requirement: Manual pocket assignment and unlinking
The system SHALL allow users to manually reassign or clear the budget pocket associated with an expense at any time.

#### Scenario: User unlinks an assigned pocket
- **WHEN** user taps the unlink button on an assigned pocket card
- **THEN** the pocket assignment is cleared while preserving the selected category

#### Scenario: User manually selects a pocket
- **WHEN** user taps the pocket card and selects a pocket from the pocket selection screen
- **THEN** the selected pocket is assigned to the expense overriding any prior auto-assignment

#### Scenario: User chooses No Pocket in pocket selection
- **WHEN** user selects the No Pocket option from the pocket selection screen
- **THEN** the pocket assignment is cleared

### Requirement: Month selection and monthly outflow summary
The system SHALL allow users to switch between months using navigation controls on the Analyse screen and SHALL display the total outflow amount for the selected month.

#### Scenario: Navigate to previous month
- **WHEN** user taps the previous month navigation control
- **THEN** the selected month changes to the preceding month, the monthly outflow updates to reflect that month's sum, and the transaction list updates

#### Scenario: Navigate to next month
- **WHEN** user taps the next month navigation control on a past month
- **THEN** the selected month advances to the following month, updating the total outflow and transaction list

#### Scenario: Next month control disabled on current month
- **WHEN** the selected month is the current calendar month
- **THEN** the next month navigation control is disabled

### Requirement: View and edit existing expense
The system SHALL allow users to tap an expense item in the transaction list to view and edit its details.

#### Scenario: Tap expense to open edit mode
- **WHEN** user taps an expense item in the transaction list
- **THEN** the system navigates to the expense form in edit mode with the screen titled "Edit Expense" and the expense's amount, description, category, and pocket pre-populated

#### Scenario: Save edited expense successfully
- **WHEN** user modifies fields of an existing expense in edit mode and saves
- **THEN** the expense is updated in local storage and the updated values reflect in the transaction list and pocket balances

### Requirement: Delete expense from edit mode
The system SHALL allow users to delete an existing expense via a delete action in the TopAppBar when in edit mode.

#### Scenario: Delete action visible in edit mode
- **WHEN** user opens the expense form in edit mode
- **THEN** the TopAppBar displays a Delete action button

#### Scenario: Delete action hidden in add mode
- **WHEN** user opens the expense form in create mode
- **THEN** the TopAppBar does not display a Delete action button

#### Scenario: Confirming expense deletion
- **WHEN** user taps the Delete action button in edit mode
- **THEN** the expense is deleted from local storage, its pocket association is cleared, the flow is dismissed, and the transaction list updates
