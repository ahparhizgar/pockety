# project-name Specification

## Purpose
Manages the project's brand identity and system-wide naming to reflect the Pockety application name.

## Requirements

### Requirement: Application naming
The system SHALL use the name "Pockety" (or "جیبک" for Persian localization) in all user-facing contexts, build configuration, and system identifiers.

#### Scenario: User installs the application
- **WHEN** the user installs the application on their device
- **THEN** the application launcher displays the name "Pockety" (or "جیبک")

### Requirement: Consistent Persian product terminology
The system SHALL use «جیبک» for the app brand and «جیب» for the financial pocket term, including grammatically appropriate plural and compound forms, throughout current Persian user-facing text. Historical documentation and stored user-authored content SHALL remain unchanged.

#### Scenario: Persian privacy policy copy
- **WHEN** a user opens the privacy policy in Persian
- **THEN** the hero subtitle uses «جیبک» and the personal-data description uses «جیب‌ها»
- **AND** neither displays the old brand «پاکتی» or pocket term «پاکت»

#### Scenario: Persian settings version
- **WHEN** a user opens settings in Persian with application version 1.2.3
- **THEN** the version label displays «جیبک نسخه 1.2.3» with the supplied version preserved

#### Scenario: Compatibility of terminology corrections
- **WHEN** the corrected Persian copy is delivered
- **THEN** formatting placeholders, other Persian wording and spacing, English copy, identifiers, and stored user content remain unchanged
