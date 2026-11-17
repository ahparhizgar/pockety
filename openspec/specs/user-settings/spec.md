# user-settings Specification

## Purpose
Provides persistent storage, retrieval, and reactive observation for user-configurable application preferences, starting with calendar system selection.

## Requirements

### Requirement: Calendar system preference storage and default
The system SHALL provide persistent storage for the user's preferred calendar system, defaulting to GREGORIAN when not explicitly configured, and supporting switching to PERSIAN or GREGORIAN.

#### Scenario: Default calendar system preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the active calendar system preference is GREGORIAN

#### Scenario: Update and persist calendar system preference
- **WHEN** the user selects PERSIAN as their calendar system in settings
- **THEN** the preference is saved to persistent storage and the active calendar system becomes PERSIAN

#### Scenario: Observe calendar preference reactively
- **WHEN** the calendar system preference is changed from GREGORIAN to PERSIAN
- **THEN** all observing components and screens receive an update with the new PERSIAN calendar system

### Requirement: Settings UI for calendar system selection
The system SHALL provide a settings screen accessible from the main navigation Settings tab that allows the user to inspect and select their preferred calendar system using structured option cards displaying title, descriptive subtitle, and calendar symbol indicator.

#### Scenario: Display current calendar preference in settings
- **WHEN** the user navigates to the Settings tab
- **THEN** the Settings screen displays the calendar system cards showing Gregorian and Persian options, with the active calendar system highlighted as selected

#### Scenario: Select calendar system option
- **WHEN** the user selects the Persian calendar option card on the Settings screen
- **THEN** the UI updates to show the Persian option card as selected and commits the change to persistent storage

### Requirement: Display currency preference storage and default
The system SHALL provide persistent storage for the user's preferred display currency, defaulting to TOMANS when not explicitly configured, and supporting switching between TOMANS and RIALS.

#### Scenario: Default currency preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the active currency preference is TOMANS

#### Scenario: Update and persist currency preference
- **WHEN** the user selects Rials as their display currency in settings
- **THEN** the preference is saved to persistent storage and the active currency becomes RIALS

#### Scenario: Observe currency preference reactively
- **WHEN** the currency preference is changed from TOMANS to RIALS
- **THEN** observing components receive an update with the new RIALS currency preference

### Requirement: End-of-day reminder preference storage and default
The system SHALL provide persistent storage for enabling or disabling the daily end-of-day reminder notification, defaulting to enabled (true).

#### Scenario: Default end-of-day reminder preference
- **WHEN** user settings are initialized without a prior stored choice
- **THEN** the end-of-day reminder preference is enabled (true)

#### Scenario: Toggle end-of-day reminder preference
- **WHEN** the user toggles the end-of-day reminder switch to disabled (false)
- **THEN** the preference is saved to persistent storage as disabled

### Requirement: Full Settings screen section hierarchy and navigation
The system SHALL organize the Settings screen into defined visual sections matching the design prototype: Organize & Taxonomy (Categories count badge and Labels navigation rows), Preferences & Region (Language row, Display Currency card, and Calendar System card), Notifications & Automation (End-of-day reminder switch), and Application Version footer.

#### Scenario: Display full settings section hierarchy
- **WHEN** the user navigates to the Settings tab
- **THEN** the screen renders Organize & Taxonomy, Preferences & Region, Notifications & Automation sections, and the app version badge

#### Scenario: Navigate to categories from settings
- **WHEN** the user taps the Categories row in the Organize & Taxonomy section
- **THEN** the settings component dispatches navigation to the category management flow

#### Scenario: Select display currency option card
- **WHEN** the user taps the Rials currency option card in the Preferences & Region section
- **THEN** the UI updates to show Rials as selected and commits the change to persistent storage

#### Scenario: Toggle end-of-day reminder
- **WHEN** the user interacts with the end-of-day reminder switch
- **THEN** the switch updates its checked state and commits the preference to persistent storage

### Requirement: Privacy & Security settings section and touchpoint
The system SHALL provide a "Privacy & Security" section on the Settings screen containing a navigation row to open the Privacy Policy, with a title, descriptive subtitle, and icon indicator.

#### Scenario: Display privacy policy touchpoint in settings
- **WHEN** the user views the Settings screen
- **THEN** the "Privacy & Security" section is displayed with the "Privacy Policy" navigation row and subtitle "Offline-first commitments & anonymous telemetry"

#### Scenario: Navigate to privacy policy from settings
- **WHEN** the user taps the Privacy Policy navigation row in Settings
- **THEN** the system navigates to the Privacy Policy screen

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

### Requirement: Rounded settings option feedback
The system SHALL confine ripple and pressed feedback for display currency and calendar option cards to their visible 16.dp rounded boundary in selected and unselected states, preserving existing styling, dimensions, and shadows.

#### Scenario: Press currency options near corners
- **GIVEN** either currency option is selected or unselected
- **WHEN** the user presses near a corner inside the visible card
- **THEN** ripple and pressed feedback remain inside the rounded boundary
- **AND** card appearance and dimensions remain unchanged

#### Scenario: Press calendar options near corners
- **GIVEN** either calendar option is selected or unselected
- **WHEN** the user presses near a corner inside the visible card
- **THEN** ripple and pressed feedback remain inside the rounded boundary
- **AND** card appearance and dimensions remain unchanged

### Requirement: Accessible settings option activation
The system SHALL preserve radio selection semantics, keyboard focus and activation, and a single selection action for each currency and calendar option card. Selection SHALL continue updating and persisting the corresponding preference.

#### Scenario: Select options using keyboard
- **GIVEN** the Settings screen is displayed
- **WHEN** the user focuses and activates a currency or calendar option with the keyboard
- **THEN** that option is exposed as a selected radio option and its sibling as unselected
- **AND** the corresponding preference is saved

#### Scenario: Activate an already selected option
- **GIVEN** a currency or calendar option is selected
- **WHEN** the user activates that option
- **THEN** it remains selected and activation dispatches only one selection action
