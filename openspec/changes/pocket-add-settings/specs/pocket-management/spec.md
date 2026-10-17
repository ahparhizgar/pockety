# Spec Delta

## Purpose

Enables users to configure, customize, and manage budget pockets, including pocket identity (name, icon, accent color), category association, and initial balance.

## ADDED Requirements

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
