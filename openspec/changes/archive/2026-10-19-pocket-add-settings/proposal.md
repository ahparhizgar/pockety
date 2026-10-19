# Proposal

## Why

Users currently have visibility into their budget pockets via the pocket list screen, but cannot add new pockets or customize pocket identity and settings directly in the app. Introducing `PocketSettingsFlowComponent` and `PocketSettingsScreen` allows users to create and edit pockets with customizable name, accent color, icon, associated category, and initial balance, while laying the foundation for pocket lifecycle management.

## What Changes

- Add Room database migration `MIGRATION_6_7` to add `color` (Long) and `categoryId` (nullable Long foreign key) to `pockets` table, updating `PocketEntity`, `PocketDao`, and `PocketRepository`.
- Implement `PocketSettingsFlowComponent` (Decompose Flow navigation) anchored to the Root destination stack, managing transitions across:
  - `PocketSettingsComponent` (main pocket settings & creation screen)
  - `CategorySelectionComponent` (subscreen to associate an existing category)
  - `AddCategoryComponent` (subscreen to create a new category directly within the flow)
- Implement `PocketSettingsScreen` in `app/shared`:
  - Pocket identity section: interactive pocket icon avatar with color background and pocket name text field with validation.
  - Accent color picker: selection of 6 palette colors matching design specs.
  - Icon picker section: icon category filter chips and 5-column icon grid.
  - Associated category card: displays linked category with Change and Clear options, or empty linked state.
  - Manual top-up card: displays current balance, amount input, and top-up confirmation.
  - Sticky bottom action button ("Save Changes" / "Create Pocket") with saving progress state.
- Wire the Extended FAB on `PocketsScreen` to launch `PocketSettingsFlowComponent` in create mode (`pocketId = null`).
- Support editing API (`pocketId: Long?`) in `PocketSettingsComponent` for future touchpoints.
- Exclude monthly allocation auto-refill card, delete pocket section, and transaction linking logic as non-goals.
- Add comprehensive unit, integration, and UI tests adhering to project conventions.

## Capabilities

### New Capabilities

- `pocket-management`: Covers creating, viewing, and configuring budget pockets with custom name, icon, accent color, associated category, balance adjustments, and validation.

### Modified Capabilities

<!-- None -->

## Impact

- Database schema updated to version 7 (`MIGRATION_6_7`).
- `PocketEntity`, `PocketItemModel`, `PocketDao`, and `PocketRepository` gain color and category relationship support.
- `RootComponent` and `MainComponent` wire `PocketSettingsFlowComponent` navigation.
- Shared resources gain string definitions for pocket settings labels, errors, and actions.
