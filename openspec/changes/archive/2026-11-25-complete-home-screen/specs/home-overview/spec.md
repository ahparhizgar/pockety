# Home Overview — Delta

## ADDED Requirements

### Requirement: Monthly outflow hero card
The Home tab SHALL show a hero card for the current calendar month (per the
user's calendar setting) with the month label on a tappable date pill, the
month's total outflow in the display currency, a segmented progress bar of the
category shares, and a breakdown list of the top 4 categories by amount plus an
aggregated "Other" row when more groups remain. Each breakdown row SHALL show
the category's color dot, name, percentage share, and amount. Uncategorized
expenses SHALL form their own group. Activating the date pill SHALL open the
Analytics tab.

#### Scenario: Month with expenses in several categories
- **WHEN** the current month has expenses across five or more categories
- **THEN** the hero card shows the month total, the top 4 categories with their
  shares and amounts, and an "Other" row aggregating the rest

#### Scenario: Empty month
- **WHEN** the current month has no expenses
- **THEN** the hero card shows a zero total and no breakdown rows

#### Scenario: Open analytics from the date pill
- **WHEN** the user activates the date pill
- **THEN** the Analytics tab opens

### Requirement: Month-over-month comparison card
The Home tab SHALL show a comparison card against the previous calendar month
with the previous month's label and total and, when the previous month's total
is greater than zero, a badge with the percentage difference marked as saved
(current <= previous) or more (current > previous). The card's "All months"
action SHALL open the Analytics tab. When the previous month's total is zero,
the badge SHALL be hidden.

#### Scenario: Spending decreased
- **WHEN** the current month's outflow is lower than the previous month's
- **THEN** the badge shows the saved percentage with the saved styling

#### Scenario: No previous-month data
- **WHEN** the previous month's total is zero
- **THEN** the card shows the previous month's zero total without a badge

### Requirement: Active pockets section
The Home tab SHALL show an "Active Pockets" section with every pocket as a
card displaying its icon, name, and remaining balance, laid out in a grid whose
column count adapts to the available width. The section SHALL be hidden when
there are no pockets. Its "Manage All" action SHALL open the Pockets tab, and
activating a pocket card SHALL open that pocket's details.

#### Scenario: Pockets on a narrow screen
- **WHEN** pockets exist and the content width is phone-sized
- **THEN** the cards render in a two-column grid with all pockets reachable by
  scrolling

#### Scenario: Open pocket details
- **WHEN** the user activates a pocket card
- **THEN** that pocket's details destination opens
