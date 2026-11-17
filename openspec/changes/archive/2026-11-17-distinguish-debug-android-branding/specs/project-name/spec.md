# Spec Delta

## MODIFIED Requirements

### Requirement: Application naming
The system SHALL use the name "Pockety" (or "جیبک" for Persian localization) in all user-facing contexts, build configuration, and system identifiers, except that Android debug launcher labels SHALL prefix the localized name with "Debug ".

#### Scenario: User installs the application
- **WHEN** the user installs a production application on their device
- **THEN** the application launcher displays the name "Pockety" (or "جیبک")

#### Scenario: English debug launcher label
- **WHEN** the Android debug application is displayed in an English launcher locale
- **THEN** its label is "Debug Pockety"

#### Scenario: Persian debug launcher label
- **WHEN** the Android debug application is displayed in a Persian launcher locale
- **THEN** its label is "Debug جیبک", with the Debug prefix visible and the localized base name preserved
