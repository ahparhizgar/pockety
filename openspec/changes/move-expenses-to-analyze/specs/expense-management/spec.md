# Spec Delta

## MODIFIED Requirements

### Requirement: View expenses list
The system SHALL display the recorded expenses on the Analyse screen filtered by the selected calendar month, grouped by date in reverse-chronological order, and SHALL NOT display the expenses list on the home screen.

#### Scenario: Display recorded expenses
- **WHEN** user views the Analyse screen with previously recorded expenses in the selected month
- **THEN** all expenses recorded in that calendar month are shown grouped by date in reverse-chronological order with their category icon, description, and amount

#### Scenario: Display empty state
- **WHEN** user views a month in the Analyse screen where no expenses have been recorded
- **THEN** the system indicates an empty expense history for that month

#### Scenario: Home screen does not show expense list
- **WHEN** user views the Home screen
- **THEN** no transaction list or empty-expenses placeholder is displayed on the Home screen

## ADDED Requirements

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
