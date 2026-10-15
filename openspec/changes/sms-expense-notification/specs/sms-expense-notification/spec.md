# Spec Delta

## Purpose

Enables instant recording or dismissal of detected bank expenses by dispatching silent system notifications upon incoming SMS messages, allowing users to tap the notification to open the prefilled expense creation flow or tap a "Deny" action button directly on the notification to reject the candidate expense without opening the app.

## ADDED Requirements

### Requirement: Silent system notification for candidate bank expense
The system SHALL post a silent notification when a bank transaction SMS is detected and saved as a candidate expense if notification permission is available.

#### Scenario: Post notification on candidate expense extraction
- **WHEN** an incoming SMS is parsed as a bank transaction, saved as an undecided candidate expense, and notification permission is present
- **THEN** the system displays a system notification displaying the parsed amount, an action to deny the candidate, and prompting the user to save the expense

#### Scenario: Notification is silent without vibration
- **WHEN** a candidate expense notification is posted
- **THEN** the notification produces no sound and no vibration

#### Scenario: Gracefully skip notification when notification permission is not granted
- **WHEN** a candidate expense is saved but notification permission is not granted (e.g. on Android 13+)
- **THEN** the candidate expense is preserved in the repository and no notification is posted without throwing an error or crashing

### Requirement: Open prefilled expense creation from notification tap
The system SHALL open the Add Expense screen pre-filled with the candidate expense amount and link when the user taps the notification body.

#### Scenario: Tap candidate expense notification
- **WHEN** the user taps the candidate expense notification
- **THEN** the application opens to the Add Expense screen with the amount field prefilled with the candidate amount and the candidate expense ID linked

### Requirement: Deny candidate expense directly from notification
The system SHALL allow users to reject a candidate expense directly from the notification without opening the application.

#### Scenario: Tap deny action on candidate expense notification
- **WHEN** the user taps the "Deny" action on the candidate expense notification
- **THEN** the candidate expense status is updated to REJECTED in the local repository and the notification is dismissed without launching the UI
