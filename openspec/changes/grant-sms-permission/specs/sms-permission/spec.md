# Spec Delta

## Purpose

Provides cross-platform SMS permission status tracking, user-prompted permission requests, and application settings fallback when permissions are permanently denied.

## ADDED Requirements

### Requirement: Query SMS permission status
The system SHALL expose the current grant status of the SMS reception permission as an observable state.

#### Scenario: Permission is granted
- **WHEN** the host operating system indicates SMS permission has been granted
- **THEN** the permission status reflects as granted

#### Scenario: Permission is not granted
- **WHEN** the host operating system indicates SMS permission has not been granted
- **THEN** the permission status reflects as not granted

### Requirement: Request SMS permission
The system SHALL trigger the platform permission request flow when requested by the user.

#### Scenario: Permission approved by user
- **WHEN** the user grants the SMS permission dialog
- **THEN** the system updates permission status to granted

#### Scenario: Permission denied by user
- **WHEN** the user dismisses or rejects the permission prompt
- **THEN** the system keeps permission status as not granted

### Requirement: Handle permanent permission denial
The system SHALL open the application system settings when the user attempts to request permission after it has been permanently denied.

#### Scenario: Navigate to settings on auto-rejection
- **WHEN** the user initiates permission request and the platform indicates the prompt cannot be displayed due to prior repeated denials
- **THEN** the system launches the device application settings screen for the user to grant permission manually
