# Spec Delta

## MODIFIED Requirements

### Requirement: Month selection and monthly outflow summary
The system SHALL allow users to switch between months using previous/next pills labelled with the adjacent month names on the Analyse screen, SHALL display the total outflow amount for the selected month, and SHALL show a trend pill comparing the selected month's outflow with the previous month's outflow — the percentage difference and the previous month's total — whenever the previous month's total is greater than zero.

#### Scenario: Navigate to previous month
- **WHEN** user taps the previous month navigation control
- **THEN** the selected month changes to the preceding month, the monthly outflow updates to reflect that month's sum, and the transaction list updates

#### Scenario: Navigate to next month
- **WHEN** user taps the next month navigation control on a past month
- **THEN** the selected month advances to the following month, updating the total outflow and transaction list

#### Scenario: Next month control disabled on current month
- **WHEN** the selected month is the current calendar month
- **THEN** the next month navigation control is disabled

#### Scenario: Trend pill compares with previous month
- **WHEN** the selected month and the previous month both have recorded outflow and the previous month's total is greater than zero
- **THEN** a trend pill shows the percentage difference against the previous month together with the previous month's total

#### Scenario: Trend pill hidden without previous data
- **WHEN** the previous month's total outflow is zero
- **THEN** no trend pill is shown

## ADDED Requirements

### Requirement: Category spending breakdown for the selected month
The system SHALL display, for the selected month on the Analyse screen, a category spending card with a donut chart of each category's share of the month's outflow, a center label naming the top-spent category and its percentage share, and one row per category group showing its color, name, percentage share and total amount. Expenses without a category SHALL form an Uncategorized group. The card SHALL NOT be displayed when the selected month has no expenses.

#### Scenario: Donut and rows reflect the month's categories
- **WHEN** the selected month has expenses in several categories
- **THEN** the donut segments and the allocation rows show each category's share of the month total, ordered from the largest to the smallest share

#### Scenario: Top spent center label
- **WHEN** the category spending card is displayed
- **THEN** the donut center shows the largest category's percentage share and name as the top spent

#### Scenario: Uncategorized group
- **WHEN** the selected month has expenses without a category
- **THEN** they appear as an Uncategorized group in the donut and the rows

### Requirement: Expenses ledger presentation
The system SHALL present the selected month's expenses as a flat ledger under an "Expenses" header that shows the total number of expenses in the month. Each ledger row SHALL show the expense's category icon on a tile tinted with the category color, the category name as the title, the description as the subtitle, and the amount; rows within a date group SHALL be separated by a thin divider and each date group SHALL be headed by its date label and total.

#### Scenario: Ledger header shows the item count
- **WHEN** the selected month has recorded expenses
- **THEN** the Expenses header shows the total count of expenses in that month

#### Scenario: Ledger row content
- **WHEN** an expense with a category and a description is shown in the ledger
- **THEN** its row shows the category icon and name, the description under it, and the amount
