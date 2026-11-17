# Spec Delta

## Purpose
Provides consistent English and Persian presentation for Analytics while preserving calendar preferences and expense information.

## ADDED Requirements

### Requirement: Analytics static text and accessibility localization
Analytics SHALL display all app-owned static labels and action descriptions in the selected application language, with complete English and Persian resources.

#### Scenario: Persian empty Analytics
- **WHEN** Analytics is opened in Persian with no expenses for the selected month
- **THEN** the title, monthly outflow label, empty state, and previous/next month accessibility descriptions are Persian

#### Scenario: English Analytics
- **WHEN** Analytics is opened in English with or without expenses
- **THEN** its app-owned labels and action descriptions are English without missing-resource output

### Requirement: Language-aware calendar presentation
Analytics SHALL localize month names and date prose for the selected language while using the independently selected calendar for date values, grouping, and month navigation.

#### Scenario: Relative and older dates
- **WHEN** expenses occur today, yesterday, and on an older day in the selected month
- **THEN** their headers display the localized equivalents of Today and Yesterday and localized month/date formatting for all three groups

#### Scenario: Language and calendar combinations
- **WHEN** Analytics is viewed with each combination of English/Persian language and Gregorian/Persian calendar
- **THEN** month names use the selected language and day/month/year values and navigation use the selected calendar

#### Scenario: Month navigation
- **WHEN** the user navigates backward then forward across a year boundary
- **THEN** localized month labels and expense groups track the calendar months and forward navigation remains disabled for the current month

### Requirement: Localized Analytics amount units
Analytics SHALL use the existing localized Toman convention for monthly, group, and expense amounts, preserving numerical values, grouping separators, and expense signs. Localization SHALL NOT introduce currency conversion or alter stored amounts or totals.

#### Scenario: Populated amounts
- **WHEN** populated Analytics is shown in Persian or English
- **THEN** monthly, group, and expense values retain their numerical meaning and show the localized Toman unit consistently with existing expense displays

#### Scenario: Empty total
- **WHEN** the selected month contains no expenses
- **THEN** the zero monthly total uses the localized Toman unit

### Requirement: Language updates preserve Analytics behavior
Analytics SHALL refresh app-owned presentation through supported language changes without stale text, preserving calendar preference, selected month, expense content, grouping, totals, tags, and keyboard actions. User-entered descriptions and custom category names SHALL remain unchanged.

#### Scenario: In-app language switch and return
- **WHEN** the user changes English to Persian and back through the supported in-app settings flow and returns to Analytics
- **THEN** all static and dynamic Analytics text follows the active language and the selected calendar and month and expense content remain unchanged

#### Scenario: System language flow
- **WHEN** the user changes application language through supported Android system settings and resumes Analytics
- **THEN** the resumed screen displays the active language consistently without stale English headers in Persian

#### Scenario: Keyboard operation
- **WHEN** the user activates month navigation and an expense using the keyboard in either language
- **THEN** navigation and expense opening work with the existing focus and accessibility semantics
