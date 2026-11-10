# Design

## Context

See `proposal.md` for background and motivation.
Currently, `PocketEntity` models budget pockets with `name`, `categoryTag`, `icon`, `createdAt`, `color`, and `categoryId`. Pocket top-ups are stored in `pocket_top_ups` (`PocketTopUpEntity`) with `pocketId`, `amount`, `description`, and `createdAt`. Pocket balances are calculated reactively via `totalTopUpAmount - spentAmount`.
In `:design` prototypes (`com.base.design.features.pocketsettings`), `MonthlyAllocationCard` is designed with an allocated refill amount display/tuning trigger, currency symbol, and an "Auto-refill on 1st of month" switch. However, `PocketSettingsScreen` and `PocketSettingsComponent` in `app/shared` currently only have pocket identity, color, icon, and associated category cards, without monthly top-up settings.
The requirement specifically mandates:
1. Support monthly pocket top-up settings in the pocket settings screen.
2. Execute the actual top-up without scheduling system background jobs, ensuring top-up transactions are created when the app opens.

## Goals / Non-Goals

**Goals:**
- Extend `PocketEntity` and Room database schema (v8 -> v9) with `monthlyTopUpAmount` (Long), `autoTopUpEnabled` (Boolean), and `lastTopUpMonth` (String? formatted as "YYYY-MM" or null).
- Provide migration `MIGRATION_8_9` with unit/migration test coverage in `MigrationTest`.
- Implement `PocketMonthlyAllocationCard` in `PocketSettingsCards.kt` (following AGENTS.md conventions, `@ComponentPreviews`, keyboard-accessible amount adjustment / tune dialog or bottom sheet, and auto-refill switch).
- Expose monthly top-up configuration in `PocketSettingsState`, `PocketSettingsComponent`, and persist it during `onSaveClicked()`.
- Implement a recurring top-up trigger or service (e.g. `PocketRecurringTopUpService` or in `PocketRepository`) executed on application start (invoked in `DefaultRootComponent` or `RootComponentFactory` initialization), which queries active pockets and records top-up transactions for any pocket due in the current year-month.
- Avoid system background jobs (e.g. WorkManager or alarms); strictly trigger recurring top-ups during app startup / on-app-open.

**Non-Goals:**
- System-level background scheduling (WorkManager, AlarmManager, or background OS daemons).
- Multi-currency support beyond the current app currency (Tomans).

## Decisions

### Decision 1: Database schema and tracking last applied month
- **Choice**: Add three columns to `pockets` table in Room:
  - `monthlyTopUpAmount: Long = 0L`
  - `autoTopUpEnabled: Boolean = false`
  - `lastTopUpMonth: String? = null` (e.g., "2026-11")
- **Rationale**: Storing `lastTopUpMonth` as year-month string (`"YYYY-MM"`) directly reflects calendar month cycles based on `Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())`. Checking if `lastTopUpMonth != currentMonth` is atomic, idempotent, and resilient against clock drift across multiple days within the same month.
- **Alternatives considered**:
  - `lastTopUpTimestamp: Instant?`: Requires converting to local month during queries and is slightly more prone to edge cases around timezone changes. "YYYY-MM" is explicit, concise, and clean.

### Decision 2: Execution point on app open
- **Choice**: Execute a check during `DefaultRootComponent` initialization (`scope.launch { pocketRecurringTopUpService.processMonthlyTopUps() }` or `pocketRepository.processMonthlyTopUps()`).
- **Rationale**: `DefaultRootComponent` is the root entry point for all platform targets (Android, Desktop, iOS, Web). Invoking it during root setup guarantees that every time the app opens or restarts, any pending monthly top-ups are evaluated and executed before or alongside initial screen renders. Top-ups will immediately reflect in `PocketsComponent` and `PocketDetailsComponent` flows.
- **Alternatives considered**:
  - Activity `onResume` on Android: Platform-dependent and violates KMP shared business logic. Root component is multiplatform and runs across Android, Desktop, and iOS.

### Decision 3: Idempotent top-up transaction generation
- **Choice**: When `processMonthlyTopUps()` runs:
  - Retrieve current `currentYearMonth = "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}"`.
  - For each pocket where `autoTopUpEnabled == true`, `monthlyTopUpAmount > 0`, and `lastTopUpMonth != currentYearMonth`:
    1. Insert `PocketTopUpEntity(pocketId = pocket.id, amount = pocket.monthlyTopUpAmount, description = "Monthly Top-Up", createdAt = now)`.
    2. Update pocket `lastTopUpMonth = currentYearMonth`.
  - Perform this in a Room database transaction to prevent partial state updates.
- **Rationale**: Guarantees exactly one top-up per pocket per calendar month. If an app is opened 10 times in November, only the first open triggers the top-up transaction. If an app was not opened for 2 months, opening in November applies the November top-up and advances the cursor to November without spamming prior inactive months.

### Decision 4: UI layout and interaction for monthly allocation in Pocket Settings
- **Choice**: Introduce `PocketMonthlyAllocationCard` in `PocketSettingsCards.kt`:
  - Displays the allocated monthly top-up amount and auto-refill switch.
  - Tapping the tune/adjust button or amount opens a numeric input sheet/dialog allowing the user to update `monthlyTopUpAmount`.
  - Toggling switch updates `autoTopUpEnabled` in state.
  - If a user creates a new pocket with `monthlyTopUpAmount > 0` and `autoTopUpEnabled == true`, setting initial `lastTopUpMonth = currentYearMonth` if initial balance already covered it, or let initial setup specify whether the current month should be funded immediately.

## Risks / Trade-offs

- **[Room Schema Migration]** → Bumping DB version from 8 to 9.
  *Mitigation*: Provide explicit `MIGRATION_8_9` in `AppDatabase.kt` and add automated migration test in `MigrationTest.kt`.
- **[App open latency]** → Querying pockets and inserting top-ups on app launch.
  *Mitigation*: The check runs asynchronously in a coroutine launched in `DefaultRootComponent.scope`, taking only a few milliseconds for local SQLite operations and not blocking UI rendering.
