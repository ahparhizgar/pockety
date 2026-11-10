# Proposal

## Why

On the pocket list screen, each `PocketBudgetCard` currently renders a "Top-up" action button with an empty no-op click handler (`onTopUpClick = {}`). This creates a misleading and non-functional interaction affordance for users. Users already perform pocket top-up actions seamlessly within the Pocket Details screen via the quick top-up sheet. Removing the no-op action cleans up the card UI and eliminates dead interaction paths.

## What Changes

- Remove the unused `onTopUpClick` parameter and the `PocketCardActions` composable from `PocketBudgetCard` in `app/shared`.
- Remove the unused `onTopUpClick = {}` invocation argument from `PocketsScreen.kt`.
- Clean up unused string resources (`pockets_top_up_action`) if not referenced elsewhere.
- Synchronize design component references in `design/` where appropriate.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `pocket-management`: Update Pocket Action Handlers requirement to specify that pocket budget cards handle card tap navigation without exposing a card-level top-up action button.

## Impact

- Composable APIs: `PocketBudgetCard` signature loses the unused `onTopUpClick: () -> Unit = {}` parameter.
- Screens: `PocketsScreen` removes `onTopUpClick = {}` parameter passing.
- Resources: `pockets_top_up_action` string resource can be removed or retained if unused elsewhere.
