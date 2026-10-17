# Proposal

## Why

Users can view and create pockets from the pockets list, but cannot tap into a pocket to inspect its balance, configure its settings, or quickly add funds. Implementing the Pocket Details screen provides an interactive pocket inspection view with quick top-up via a modal bottom sheet and seamless navigation to configuration, completing the pocket management experience.

## What Changes

- Add pocket top-up support in `PocketDao` and `PocketRepository` (`topUpPocket(id: Long, amount: Long)` / `observePocket(id: Long): Flow<PocketItemModel?>`).
- Implement `PocketDetailsComponent` (Decompose) with state representing pocket identity (name, icon, accent color, balance), quick top-up bottom sheet state, dynamic feedback confirmation, and mock transaction UI items.
- Wire navigation in `MainComponent` and `RootComponent`:
  - Tapping a pocket card in `PocketsScreen` invokes `onPocketClick(pocketId)` navigating to `PocketDetailsComponent`.
  - Tapping "Configure" or settings icon on `PocketDetailsScreen` launches `PocketSettingsFlowComponent(pocketId = pocketId)`.
  - Tapping "Back" pops `PocketDetailsComponent` back to the pocket list.
- Implement `PocketDetailsScreen` in `app/shared`:
  - Top app bar with back navigation, pocket title ("Pocket Details"), tune / configure action icon, and more options.
  - Pocket identity and balance section: circular/squircle icon badge with accent color background, pocket title, large minimalist balance display formatted with commas and Tomans (`T`), and "Balance" subtitle.
  - Action bar with two primary buttons:
    - "Top-up" button (filled button with `+` icon) launching the Quick Top-up modal bottom sheet.
    - "Configure" button (filled tonal/surface container high with `tune` icon) navigating to pocket settings in edit mode.
  - Quick Top-up modal bottom sheet:
    - Preset quick-amount chips (+50,000 T, +100,000 T, +200,000 T, +500,000 T).
    - Custom numeric amount input field with keyboard accessibility options/actions.
    - Confirm button updating the pocket's balance in Room through `PocketRepository` and dismissing the sheet.
  - Dynamic feedback confirmation toast/banner when top-up completes (e.g., "Pocket topped up by +100,000 T!").
  - Expenses section UI layer: section header ("Expenses") and minimalist flush transaction rows showing sample expenses with icon, merchant name, timestamp, note, formatted deduction (`-XX,XXX T`), and row action feedback (linking to Room transactions data layer is explicitly out of scope).
- Add previews (`@ScreenPreviews`, `@ComponentPreviews`) and automated tests (unit, flow E2E, and UI E2E) adhering to project rules and conventions.

## Capabilities

### New Capabilities

- `pocket-management`: Covers viewing pocket details, executing quick balance top-ups via modal bottom sheet, navigating to pocket settings/configure, and presenting transaction feed UI.

### Modified Capabilities

<!-- None -->

## Impact

- `PocketDao` and `PocketRepository` gain pocket top-up and observation methods.
- `RootComponent` and `MainComponent` wire `PocketDetailsComponent` into root stack navigation.
- Shared resources gain strings for pocket details labels, actions, quick top-up amounts, and confirmations.
