# Proposal

## Why

Categories are always listed newest-first (`ORDER BY createdAt DESC`). Users cannot
arrange them by importance: the category they use most may sit far down the list both
in Settings → Categories and in the category selection list shown when categorizing an
expense. The product owner asked for manual sorting: the user arranges the order on
the Categories page opened from Settings, and that same order applies in the category
selection page.

## What Changes

- Add a persistent manual order for categories (a new `sortOrder` column, database
  version 9 → 10 with a migration that preserves the currently visible order, so
  nothing visibly changes on upgrade until the user reorders).
- On the standalone Categories destination (Settings → Categories), each row gains a
  drag handle. The user drags a row up or down to reorder; the new order is saved
  immediately. Reordering is also fully keyboard-operable: a focused drag handle
  moves its row with the arrow keys.
- The category list everywhere follows the manual order, because ordering is applied
  in the data layer: the standalone Categories destination, the category selection
  page (the Uncategorized entry stays first and is not part of the order), and any
  other category list. Searching still filters within that order.
- A newly created category is appended at the end of the order.
- Design first: the drag-handle rows are designed in the `:design` module following
  the designer skill and are approved by the product owner before the app
  implementation starts.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `category-management`: a new requirement covers manually reordering categories
  from the standalone destination (drag and keyboard, persisted, applied to every
  category list including selection), and the browse requirement's ordering changes
  from creation order to the user's manual order.

## Impact

- Data layer: `CategoryEntity` gains `sortOrder`; Room migration 9 → 10 with an
  order-preserving backfill; the DAO orders by `sortOrder`; `CategoryRepository`
  gains a reorder operation (real and fake implementations).
- `app/shared` category management feature: drag-and-drop list with drag handles,
  reorder callback on `CategoryManagementComponent`.
- A drag/reorder library for Compose Multiplatform may be added (see design.md).
- New string resources (EN + FA) for the drag handle / move actions.
- `:design` module: the category list design gains drag handles and a dragging
  state, registered in the Catalog, per the designer skill — the design approval
  gate.
- Tests: repository tests for ordering/reorder/migration behavior, component tests
  for the reorder callback, and an E2E UI test for the reorder journey; the
  selection order is covered by repository/component tests.
- No server or API changes. Database schema changes (with migration, per repo
  rules).
