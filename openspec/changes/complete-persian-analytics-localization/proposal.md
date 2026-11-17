# Proposal

## Why
Analytics still displays English app-owned text when Persian is selected. Missing translations and component-built date and currency strings make the experience inconsistent.

## What Changes
- Complete Persian Analytics labels and accessibility descriptions while retaining English resources.
- Localize relative dates, month/year labels, older dates, and currency presentation using resources and the supported language update flow.
- Keep calendar selection independent of language and reuse existing localized Toman conventions without changing stored amounts or financial calculations.
- Preserve expense descriptions, custom category names, grouping, totals, tags, selection, and keyboard navigation.
- Add focused regression coverage and maintain populated/empty previews. No redesign or unrelated screen localization.

## Capabilities

### New Capabilities
- `analytics-localization`: Language-aware Analytics labels, dynamic date/currency presentation, and updates through supported language flows.

### Modified Capabilities
None. Existing calendar and settings contracts remain intact; the new capability specifies their Analytics presentation integration.

## Impact
Shared client Analytics component/screen, English/Persian Compose resources, calendar month-name presentation helpers where necessary, and feature tests/previews. No server, database, API, schema, or dependency changes expected. Coordinate shared resource edits with POC-37/38/39 through linear rebases.
