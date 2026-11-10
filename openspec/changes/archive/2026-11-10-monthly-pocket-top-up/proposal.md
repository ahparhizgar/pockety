# Proposal

## Why

Users who manage recurring monthly allowances or budgets across pockets currently have to manually top up each pocket every month. Adding support for configured monthly top-ups eliminates this manual chore, providing automatic balance replenishment at the beginning of each monthly cycle while maintaining clear audit trails through recorded top-up transactions without relying on complex background system services.

## What Changes

- Add monthly top-up configuration in Pocket Settings (`MonthlyAllocationCard` or equivalent monthly allocation card) allowing users to enable auto-refill, adjust the recurring monthly allocation amount, and persist this configuration to the pocket.
- Enhance the pocket data model to persist monthly top-up settings (`monthlyTopUpAmount` and `autoTopUpEnabled` / recurring status) and track the last applied monthly top-up timestamp (`lastTopUpMonth` or date/time).
- Implement top-up transaction generation on application open: upon app startup (e.g. during `RootComponent` initialization), evaluate eligible pockets whose monthly auto-refill is enabled and due for the current calendar month, generating dedicated top-up transactions without requiring system background workers or OS alarms.
- Display monthly top-up details in the pocket details activity ledger alongside existing manual top-ups and expenses.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `pocket-management`: Add requirements and scenarios for monthly top-up configuration in pocket settings and automatic on-app-open execution of due monthly pocket top-up transactions.

## Impact

- **Database**: Add `monthlyAllocationAmount` (or `monthlyTopUpAmount`) and auto-refill configuration/tracking to Room `PocketEntity` / `pockets` table, creating migration (v8 -> v9) with Room migration tests.
- **Components & UI**:
  - `PocketSettingsComponent` & `PocketSettingsScreen`: Add state and controls for monthly top-up allocation amount and auto-refill toggle.
  - `RootComponent` / Pocket recurring service: Execute on-app-open evaluation and record top-up transactions when a new calendar month is reached.
  - `PocketRepository`: Support updating pocket monthly refill settings and querying/applying due recurring top-ups.
- **Tests**:
  - Unit tests for `PocketSettingsComponent` (configuring and persisting monthly allocation).
  - Integration/flow tests for on-app-open recurring top-up transaction recording.
  - UI tests for pocket settings monthly allocation card and auto-refill switch interactions.
