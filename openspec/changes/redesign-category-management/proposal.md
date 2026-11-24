# Proposal

## Why

The Categories destination opened from Settings presents categories as a plain grid of
icon cards. The design looks unfinished next to the rest of the app, and the page offers
no per-category management action — a category cannot be deleted there at all. The
category selection screen already has a polished list design users know; the management
page should look like it, minus the selection behavior, and gain the missing delete
action.

## What Changes

- Redesign the standalone Categories destination (Settings → Categories) from the
  adaptive card grid to a single-column list modeled on the category selection screen:
  bounded centered content, a search field, rows with a rounded color icon tile and the
  category name, and a bottom create-category button in place of the floating button.
- Rows have no selection behavior: no selected state, no "active" badge, and no
  Uncategorized row — this page manages categories, it does not assign them.
- Add a delete action to each category row. Deleting asks for confirmation first;
  after deletion the category disappears from the list and from category selection.
  Expenses and pockets that used the category become uncategorized (the database
  already nulls the reference on delete).
- Keep existing behavior otherwise: back navigation to Settings, the add-category
  flow and its return behavior, the empty state, and the Settings category count.
- Design first: the new page is designed in the `:design` module following the
  designer skill and is approved by the product owner before the app implementation
  starts.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `category-management`: the standalone browse requirement changes presentation
  (selection-style list instead of the card grid, still without selection controls
  or an Uncategorized action), and a new requirement covers deleting a category
  from the standalone destination, including confirmation and the effect on
  expenses and pockets that referenced it.

## Impact

- `app/shared` category management feature: `CategoryManagementScreen` layout and
  `CategoryManagementComponent` (delete callback, confirmation state, search state).
- New string resources (EN + FA) for the delete action and the confirmation dialog.
- Data layer: no changes — `CategoryRepository.deleteCategory` already exists and
  expense/pocket category references already use `ON DELETE SET NULL`.
- `:design` module: a design version of the page plus Catalog registration, per the
  designer skill, used for the design approval gate.
- Tests: component tests for delete/confirmation/search, an E2E UI test for the
  delete journey, and updated screenshot previews for the redesigned page.
- No server, API, or database schema changes.
