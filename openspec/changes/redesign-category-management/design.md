# Design

## Context

See proposal.md for motivation. Current state: `CategoryManagementScreen` renders an
adaptive grid of icon cards with an add button and no per-category actions.
`CategorySelectionScreen` already implements the target look: a bounded centered
column with a search field, list rows (44dp rounded tile filled with the category
color, white icon, name), and a bottom create button. The selection row composable is
private to the selection screen file. The data layer already supports deletion:
`CategoryRepository.deleteCategory(id)` exists and expense/pocket category references
are `ON DELETE SET NULL`. `CategoryManagementState` currently holds only the category
list; the component exposes `onAddClicked` / `onBackClicked`.

## Goals / Non-Goals

**Goals:**

- One shared category-row implementation behind both the selection and management
  lists, so the two pages cannot drift apart visually.
- Delete discoverable by touch and keyboard, always confirmed, with the list and
  Settings count updating from the existing repository observation.
- The redesigned page designed and approved in the `:design` module before any app
  code changes.

**Non-Goals:**

- Editing a category (name/color/icon) from the management page — not requested.
- Reordering categories, bulk actions, or undo for deletion.
- Any change to the selection screen's behavior or to the add-category flow.

## Decisions

- **Extract the selection row into a shared component** in the category feature,
  parameterized by name, icon, tile color, optional description, an optional
  selected state, and an optional trailing-action slot. Selection passes its
  selected state and no trailing action; management passes no selection and a
  delete action. Alternatives considered: duplicating the row in the management
  screen (rejected — copy-paste guarantees visual drift, and the designer skill
  requires extraction), or leaving selection untouched and building a lookalike
  (same problem).
- **Delete is a trailing icon button on each row** (delete icon, error-tinted),
  visible without gestures and reachable by keyboard, as the accessibility
  requirement demands a keyboard-operable delete. Alternatives: swipe-to-delete
  or long-press (rejected — hidden, pointer-only, poor on desktop), per-row
  overflow menu (rejected — a second tap to reach the only row action).
- **Confirmation is a Material3 AlertDialog** naming the category, with a
  destructive confirm action and a cancel action. The component holds the
  pending-deletion category in state; confirm calls
  `categoryRepository.deleteCategory(id)` and clears the pending state, cancel
  only clears it. The list itself refreshes through the existing categories
  observation, so no manual list mutation is needed.
- **Search mirrors selection**: the management state gains a query and a filtered
  list derived the same way the selection state derives its filtered categories,
  reusing the selection screen's search-field styling and its no-results empty
  state. The page keeps its own empty state for "no categories at all".
- **Layout mirrors selection**: `LazyColumn` of rows in a centered column bounded
  at 640dp, search field on top, and the selection-style bottom create-category
  surface button replacing the current floating add button.
- **Design gate**: implement the page first in `:design` (designer skill: feature
  folder with `Screen.kt`/`Component.kt`, registered in `Catalog.kt`) with
  populated, empty, no-search-results, delete-confirmation, and dark states.
  Its catalog screenshots go to the product owner; app implementation starts only
  after approval.

## Risks / Trade-offs

- Extracting the shared row touches the working selection screen → its screenshot
  set is byte-compared after every push by the screenshot-diff routine, so any
  visual regression in selection is caught immediately; behavior is covered by
  the existing selection tests.
- The confirmation dialog renders in a separate desktop window → screenshot
  capture of dialog previews is supported since the DialogAware capturer fix
  (2026-11-24), so the dialog state can be screenshot-tested like any other.
- Deletion is irreversible and silently makes expenses uncategorized → mitigated by
  the mandatory confirmation naming the category; undo is a deliberate non-goal.
