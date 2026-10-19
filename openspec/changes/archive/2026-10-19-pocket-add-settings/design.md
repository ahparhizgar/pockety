# Design

## Context

See proposal.md - Why.
The application currently features a Room database at version 6 with a `pockets` table (`id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`) and bottom-bar navigation rendering `PocketsScreen`. `PocketsScreen` renders budget cards and an Extended FAB (`Add Pocket`), but the FAB click handler is currently a no-op placeholder.
The project follows Decompose with flow components for multi-step interactions (such as `AddExpenseFlowComponent` orchestrating expense addition, category selection, and category creation). `:design` contains complete prototypes for `PocketSettingsScreen` and visual options (colors and categorized icons).

## Goals / Non-Goals

**Goals:**
- Implement Room database migration `MIGRATION_6_7` adding `color: Long` (default `0xFF316342`) and nullable `categoryId: Long?` to the `pockets` table.
- Update `PocketEntity`, `PocketDao`, and `PocketRepository` to persist and retrieve pocket color and associated category.
- Implement `PocketSettingsFlowComponent` with child navigation stack:
  - `Main(pocketId: Long? = null)`: Main settings and form editing screen (`PocketSettingsComponent`).
  - `CategorySelection(currentCategoryId: Long?)`: Reusable category selection screen (`CategorySelectionComponent`).
  - `AddCategory`: Reusable category creation screen (`AddCategoryComponent`).
- Implement `PocketSettingsComponent` supporting both creation (`pocketId = null`) and editing (`pocketId: Long`) with form state, validation, and async saving (`Job`).
- Implement `PocketSettingsScreen` in `app/shared` adhering to `:design` visual specifications:
  - Pocket identity section (icon preview with selected background color, pocket name field).
  - Accent color picker (swatch row with 6 palette colors).
  - Categorized icon picker (filter chips and 5-column grid).
  - Associated category card (linked category with Change and Clear actions, or unlinked placeholder).
  - Manual top-up card (balance display and custom top-up input).
  - Sticky bottom save action button with saving state indicator.
- Wire FAB on `PocketsScreen` through `MainComponent` to launch `PocketSettingsFlowComponent` on `RootComponent`.

**Non-Goals:**
- Monthly allocation auto-refill card (`MonthlyAllocationCard`).
- Pocket deletion / danger zone card (`DangerZoneCard`).
- Transaction / expense linking logic.
- Edit trigger on `PocketBudgetCard` (editing is enabled via `PocketSettingsComponent(pocketId = ...)` API for future touchpoints).

## Decisions

### 1. Decompose Flow Component Architecture (`PocketSettingsFlowComponent`)
- **Decision**: Wrap pocket settings and its sub-screens (`CategorySelectionComponent`, `AddCategoryComponent`) into a dedicated `PocketSettingsFlowComponent`.
- **Rationale**: Follows project conventions established by `AddExpenseFlowComponent`. When the user taps "Change Category" on `PocketSettingsScreen`, the flow pushes `CategorySelection`; if they choose to create a category, it pushes `AddCategory`. Once complete, the flow pops back to `PocketSettingsComponent` and updates the selected category without polluting root navigation.
- **Alternatives Considered**: Direct root navigation transitions. Rejected because coordinating intermediate state (e.g. preserved pocket name and icon while selecting a category) would require complex global state.

### 2. Database Schema Migration (`MIGRATION_6_7`)
- **Decision**: Increment database version from 6 to 7. Add `color INTEGER NOT NULL DEFAULT 3236674` (`0xFF316342`) and `categoryId INTEGER REFERENCES categories(id) ON DELETE SET NULL` to `pockets`.
- **Rationale**: `PocketSettingsScreen` enables accent color selection and category association. Persisting color ensures the pocket badge and card visually reflect the selected theme. `categoryId` enables foreign key integrity with existing categories while preserving `categoryTag` for display fallback.
- **Alternatives Considered**: Keeping color purely in-memory. Rejected because users expect chosen colors to persist.

### 3. Component State & Contract
- **Decision**: Define `PocketSettingsState` with immutable properties, error types (`UiString`), and Decompose `Value<PocketSettingsState>`. Use non-suspending methods returning `Job` for asynchronous operations (`onSaveClicked(): Job`).
- **Rationale**: Conforms strictly to Decompose rules and AGENTS.md guidelines.

### 4. Adaptive & Accessible UI
- **Decision**: Port `PocketSettingsScreen` components using `Surface`, `Scaffold`, `widthIn(max = 640.dp)` centered column, and explicit `KeyboardOptions`/`KeyboardActions` for text fields.
- **Rationale**: Adheres to user rules regarding large screen adaptivity, keyboard accessibility, and Compose semantic hierarchy.

## Risks / Trade-offs

- **[Database Migration Integrity]** → Risk of SQLite schema mismatch during migration.
  *Mitigation*: Implement and run Room `MigrationTest` covering v6 to v7 transition with `BundledSQLiteDriver`.
- **[Flow State Preservation]** → Navigating to category selection must not lose uncommitted name, color, or icon edits.
  *Mitigation*: The `PocketSettingsComponent` remains alive in the Decompose backstack while `CategorySelectionComponent` is active. Category changes are passed via callback directly into the active component instance.
