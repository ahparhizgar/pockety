# Spec Delta

## ADDED Requirements

### Requirement: App language preference storage and default
The system SHALL provide persistent storage for the user's preferred application language, supporting ENGLISH ("en") and PERSIAN ("fa"), defaulting to ENGLISH when not explicitly configured.

#### Scenario: Default language preference
- **WHEN** user settings are initialized without a prior stored language choice
- **THEN** the active application language preference is ENGLISH

#### Scenario: Update and persist language preference
- **WHEN** the user selects Persian as their application language
- **THEN** the preference is saved to persistent storage and the active application language becomes PERSIAN

#### Scenario: Observe language preference reactively
- **WHEN** the application language preference changes from ENGLISH to PERSIAN
- **THEN** observing components receive an update with the new PERSIAN language preference

### Requirement: Language switcher touchpoint and platform routing
The system SHALL display an App language row in the Preferences & Region section showing the active language. On Android 13+ (API 33+), interacting with the language row SHALL request the system per-app language settings screen. On systems below API 33 or where system language settings are unavailable, interacting with the row SHALL open an in-app language selection dialog allowing the user to select ENGLISH or PERSIAN.

#### Scenario: Display current language in settings row
- **WHEN** the user navigates to the Settings screen
- **THEN** the App language row displays the current language title ("English" when ENGLISH, "فارسی" when PERSIAN)

#### Scenario: Open system language settings on Android 13+
- **WHEN** the user taps the App language row on an Android 13+ device supporting per-app locale settings
- **THEN** the system launches the system per-app language settings activity

#### Scenario: Open in-app language dialog on legacy Android or fallback platforms
- **WHEN** the user taps the App language row on a device where system per-app language settings are unavailable
- **THEN** the system presents an in-app dialog with English and Persian options

#### Scenario: Select language from in-app dialog
- **WHEN** the user selects Persian in the in-app language dialog
- **THEN** the application commits PERSIAN to persistent settings and dismisses the dialog

### Requirement: Right-to-Left (RTL) layout direction coordination
The system SHALL coordinate UI layout direction with the active application language, enforcing Right-to-Left (RTL) layout direction when Persian is active and Left-to-Right (LTR) layout direction when English is active.

#### Scenario: Persian activates RTL layout
- **WHEN** the active application language is PERSIAN
- **THEN** the application composition sets `LocalLayoutDirection` to `LayoutDirection.Rtl` and mirrorable components orient from right to left

#### Scenario: English activates LTR layout
- **WHEN** the active application language is ENGLISH
- **THEN** the application composition sets `LocalLayoutDirection` to `LayoutDirection.Ltr` and mirrorable components orient from left to right
