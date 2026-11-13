# Spec Delta

## Purpose
Provides users with transparent access to Pockety's offline-first privacy policy and security commitments directly within the application.

## ADDED Requirements

### Requirement: Privacy policy presentation and core commitments
The system SHALL provide a dedicated Privacy Policy screen displaying Pockety's offline-first privacy principles, including explicit commitments: no personal data is sent or monetized, no SMS data or message content is sent to remote servers (on-device processing only), and only anonymous telemetry and aggregated diagnostics are collected.

#### Scenario: Display privacy policy commitments
- **WHEN** the user navigates to the Privacy Policy screen
- **THEN** the screen displays the privacy policy hero header, the three commitment cards ("We do not send personal data", "We do not send SMS data", and "Anonymous usage data only"), key takeaway bullet points, and the policy update version badge

### Requirement: Privacy policy screen back navigation
The system SHALL provide a top app bar on the Privacy Policy screen with a back navigation button that returns the user to the previous screen.

#### Scenario: Navigate back from privacy policy
- **WHEN** the user taps the back navigation button in the top app bar of the Privacy Policy screen
- **THEN** the system navigates back to the originating screen
