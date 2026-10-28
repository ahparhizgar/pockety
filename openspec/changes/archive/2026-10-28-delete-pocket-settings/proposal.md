# Proposal

## Why

Currently, the pocket settings screen enables creating and editing a budget pocket, but users cannot delete an existing pocket. The UI design in `:design` includes a `DangerZoneCard` ("Delete Pocket") specifically for existing pockets. We need to implement pocket deletion in the pocket settings screen, displayed only in edit mode (`pocketId != null`) and hidden in new/add mode (`pocketId == null`), adhering strictly to `:design`.

## What Changes

- Add pocket deletion capability to `PocketRepository` and Room DAO (`deletePocket(id: Long)`).
- Update `PocketSettingsComponent` to support `onDeleteClicked(): Job` and a deletion completion callback (`onDeleted: () -> Unit`).
- Expose `DangerZoneCard` in `app/shared` matching `:design` (with proper localization and theme tokens).
- Integrate `DangerZoneCard` into `PocketSettingsScreen` so it is conditionally displayed only when editing an existing pocket (`!isNewPocket`).
- Connect pocket deletion through `PocketSettingsFlowComponent` to trigger dismissal/completion when a pocket is deleted.
- Add unit, flow, and UI tests verifying that the danger zone card is hidden in add mode, visible in edit mode, and triggers deletion and flow exit when clicked.

## Capabilities

### Modified Capabilities
- `pocket-management`: Add pocket deletion requirement and specification for the Danger Zone card conditional visibility in pocket settings.

## Impact

- Database / Repository: `PocketDao` and `PocketRepository` gain `deletePocket(id: Long)`. Note that `transactions.pocketId` foreign key is `ON DELETE SET NULL`, and `pocket_top_ups.pocketId` foreign key is `ON DELETE CASCADE`.
- Decompose: `PocketSettingsComponent` and `PocketSettingsComponentFactory` interface/implementations gain delete action and callback.
- UI: `PocketSettingsScreen` and `PocketSettingsCards.kt` add `PocketDangerZoneCard`.
- Resources: Localization strings for Danger Zone title, description, and delete pocket button action.
