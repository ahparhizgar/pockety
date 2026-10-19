# Pocket Management Specification

## Purpose

Enables users to monitor budget pockets, view remaining balances dynamically updated against linked expenses, and access the pocket list from the bottom navigation bar.

## Requirements

### Requirement: Pocket List Presentation
The system SHALL display all configured budget pockets on the pockets screen accessible via the bottom navigation bar.

#### Scenario: Pockets tab selected
- **WHEN** user selects the Pockets tab in the bottom bar
- **THEN** the system displays the Pockets screen with the active budget pockets

#### Scenario: Empty pocket state
- **WHEN** user opens the Pockets screen and no pockets exist
- **THEN** the system displays an empty state indicating no budget pockets are configured

### Requirement: Pocket Balance Calculation
The system SHALL calculate and display the remaining balance for each pocket as the pocket budget amount minus the sum of transactions linked to that pocket.

#### Scenario: Pocket with no linked expenses
- **WHEN** a pocket has a budget amount of 1,000,000 and 0 linked transactions
- **THEN** the displayed balance is equal to the full budget amount

#### Scenario: Pocket with linked expenses within budget
- **WHEN** a pocket has a budget amount of 1,000,000 and linked transactions summing to 150,000
- **THEN** the displayed balance is 850,000 and the pocket is not marked as over-budget

#### Scenario: Pocket exceeding budget amount
- **WHEN** a pocket has a budget amount of 500,000 and linked transactions summing to 565,000
- **THEN** the pocket indicates an over-budget status and warning

### Requirement: Pocket Action Handlers
The system SHALL handle user interactions on pocket cards and list controls gracefully.

#### Scenario: User clicks pocket card
- **WHEN** user taps a pocket budget card
- **THEN** the interaction is handled without crashing (delegating to pocket detail navigation handler)

#### Scenario: User taps Add Pocket action
- **WHEN** user taps the Add Pocket floating action button
- **THEN** the interaction is handled without error as an initial placeholder

### Requirement: Pocket Creation Flow Launch
The system SHALL launch the pocket settings flow in creation mode when initiated from the pocket list.

#### Scenario: User taps Add Pocket FAB
- **WHEN** user taps the Add Pocket floating action button on the pockets screen
- **THEN** the system navigates to the pocket settings flow in creation mode with default visual options and an empty name

### Requirement: Pocket Identity Customization
The system SHALL validate pocket name input and allow selecting an icon and accent color for the pocket.

#### Scenario: Blank pocket name validation
- **WHEN** user attempts to save a pocket with an empty or whitespace-only name
- **THEN** the system displays a name validation error and prevents saving

#### Scenario: Selecting accent color and icon
- **WHEN** user selects an accent color swatch and an icon from the picker grid
- **THEN** the pocket preview updates immediately to render the selected icon with the chosen accent color

### Requirement: Associated Category Configuration
The system SHALL allow users to associate an existing category or create a new category within the pocket settings flow, as well as clear an association.

#### Scenario: Linking an existing category
- **WHEN** user selects a category from the category selection sub-screen within the flow
- **THEN** the system returns to pocket settings displaying the linked category name and icon

#### Scenario: Clearing linked category
- **WHEN** user clears the associated category in pocket settings
- **THEN** the pocket settings updates to indicate no category is associated

### Requirement: Manual Top-Up and Balance Configuration
The system SHALL allow users to adjust or initialize the pocket balance amount via the manual top-up section.

#### Scenario: Entering and confirming top-up amount
- **WHEN** user enters a valid numeric amount and confirms top-up
- **THEN** the pocket's balance increases by the specified amount and the input resets

### Requirement: Pocket Persistence and Flow Completion
The system SHALL persist the pocket entity with its name, icon, color, category, and budget amount to local storage and complete the flow upon saving.

#### Scenario: Saving a new pocket successfully
- **WHEN** user submits valid pocket settings and taps the save action
- **THEN** the system persists the pocket with its selected properties to local storage and dismisses the pocket settings flow

### Requirement: Pocket Details Inspection
The system SHALL display detailed information for a selected pocket, including its name, visual icon, assigned accent color, formatted balance in Tomans, and action options.

#### Scenario: Navigating to pocket details from pocket list
- **WHEN** the user selects a pocket from the pocket list
- **THEN** the system displays the pocket details screen with the pocket's name, icon, accent color, and formatted current balance in Tomans

#### Scenario: Navigating back from pocket details
- **WHEN** the user clicks the back button in the top app bar
- **THEN** the system navigates back to the pocket list screen

### Requirement: Quick Top-Up Modal Bottom Sheet
The system SHALL provide a modal bottom sheet allowing users to quickly increase a pocket's budget balance using preset quick-amount options or custom numeric input.

#### Scenario: Opening quick top-up bottom sheet
- **WHEN** the user taps the Top-up button on the pocket details screen
- **THEN** the system presents the quick top-up modal bottom sheet showing preset amount chips and an amount input field

#### Scenario: Confirming top-up with preset amount
- **WHEN** the user selects a preset amount chip and confirms top-up
- **THEN** the pocket balance increases by the selected amount, the modal bottom sheet dismisses, and a confirmation feedback message is displayed

#### Scenario: Confirming top-up with custom amount
- **WHEN** the user enters a valid positive custom amount and confirms top-up
- **THEN** the pocket balance increases by the entered amount, the modal bottom sheet dismisses, and a confirmation feedback message is displayed

#### Scenario: Dismissing quick top-up bottom sheet
- **WHEN** the user dismisses the quick top-up bottom sheet without confirming
- **THEN** the bottom sheet closes and the pocket balance remains unchanged

### Requirement: Pocket Configuration Navigation
The system SHALL provide navigation from pocket details to pocket settings for editing pocket configuration.

#### Scenario: Launching configuration from action button
- **WHEN** the user taps the Configure button in the action bar
- **THEN** the system launches the pocket settings flow in edit mode for the current pocket

#### Scenario: Launching configuration from top app bar
- **WHEN** the user taps the settings icon in the top app bar
- **THEN** the system launches the pocket settings flow in edit mode for the current pocket

### Requirement: Pocket Transaction Feed Presentation
The system SHALL render a dedicated transactions section on the pocket details screen displaying transaction rows in the UI layer.

#### Scenario: Displaying transaction items
- **WHEN** viewing the pocket details screen
- **THEN** the system displays the expenses section header and minimalist transaction rows displaying the merchant or category icon, title, timestamp, note, and formatted deduction amount in Tomans

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
