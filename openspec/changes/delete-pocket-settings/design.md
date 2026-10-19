# Design

## Context

See `proposal.md` for motivation. Currently, `PocketSettingsComponent` manages editing and creating pockets, while `PocketSettingsScreen` renders identity, accent color, icon, and associated category cards.
In `:design`, `DangerZoneCard` is already designed with the warning icon, copy explaining that linked expenses will be preserved, and a "Delete Pocket" button. In `design/src/commonMain/kotlin/com/base/design/features/pocketsettings/Screen.kt`, `DangerZoneCard` is conditionally rendered when `!isNewPocket`.
Room schema for `transactions` already defines foreign key to `PocketEntity` with `onDelete = ForeignKey.SET_NULL`, and `pocket_top_ups` with `onDelete = ForeignKey.CASCADE`. Room will automatically preserve transaction history while setting `pocketId = null`, and cascade delete top-ups.

## Goals / Non-Goals

**Goals:**
- Add `deletePocket(id: Long)` to `PocketDao` and `PocketRepository`.
- Add `onDeleteClicked(): Job` to `PocketSettingsComponent` and expose deletion lifecycle via `onDeleted: () -> Unit` in factory / callbacks.
- Create `PocketDangerZoneCard` composable in `app/shared` matching the design in `:design` with theme colors and string resources.
- Show `PocketDangerZoneCard` on `PocketSettingsScreen` only when `!isNewPocket` (i.e. `pocketId != null`).
- Handle deletion completion in `PocketSettingsFlowComponent` by calling `onCompleted()`.

**Non-Goals:**
- Adding a confirmation dialog modal (the card itself functions as the danger zone action per `:design`).
- Modifying expense transaction records beyond the existing database foreign key cascade/set-null behavior.

## Decisions

### 1. Re-implement DangerZoneCard in `app/shared` following `:design`
- **Choice**: Implement `PocketDangerZoneCard` in `app/shared/src/commonMain/kotlin/com/base/capability/pocket_management/PocketSettingsCards.kt` using `stringResource` and theme tokens, rather than referencing `:design` directly (per AGENTS.md rule: `:design module is a totally separate module from the main app`).
- **Alternative**: Referencing `:design` classes directly in `app/shared` (prohibited by architecture rules).

### 2. Visibility condition based on `pocketId != null`
- **Choice**: In `PocketSettingsScreen`, check `val isNewPocket = state.pocketId == null`. Render `PocketDangerZoneCard` only if `!isNewPocket`.
- **Alternative**: Adding a separate boolean flag in `PocketSettingsState`. Using `pocketId != null` is consistent with existing `isNewPocket` check used for top bar and save button labels.

### 3. Deletion flow completion
- **Choice**: Deleting the pocket triggers `onDeleted: () -> Unit` callback passed into `PocketSettingsComponentFactory.create()`. `DefaultPocketSettingsFlowComponent` forwards this to `onCompleted()`.
- **Alternative**: Introducing a dedicated `onPocketDeleted` callback up the entire Decompose navigation stack. Forwarding to `onCompleted()` cleanly exits the flow and refreshes the parent pocket list.

## Risks / Trade-offs

- **[Risk]** Accidental tap on delete button immediately deleting data.
  → **Mitigation**: The design places the button inside a dedicated distinct "Danger Zone" card container with warning messaging explaining that expense history is preserved.
