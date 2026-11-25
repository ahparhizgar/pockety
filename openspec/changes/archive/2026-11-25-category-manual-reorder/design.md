# Design

## Context

Categories are ordered in exactly one place: `CategoryDao.observeCategories()`
(`ORDER BY createdAt DESC`). Every category list (standalone management destination,
category selection, settings pickers) consumes `CategoryRepository.observeCategories()`,
so applying the manual order in the data layer propagates it everywhere with no
per-screen sorting code. The management destination was just redesigned
(`redesign-category-management`) as a selection-style list with a shared row component
that has a trailing-action slot; reorder builds on that list.

## Decisions

### Persistent order: `sortOrder` column + migration 9 → 10
- `CategoryEntity` gains `sortOrder: Int` (default 0 so existing constructors and fake
  factories keep compiling).
- Room database version 9 → 10. `MIGRATION_9_10`:
  1. `ALTER TABLE categories ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0`
  2. Backfill that preserves the currently visible order exactly:
     `UPDATE categories SET sortOrder = (SELECT COUNT(*) FROM categories AS c WHERE c.createdAt > categories.createdAt OR (c.createdAt = categories.createdAt AND c.id > categories.id))`
     The newest category (today shown first) gets 0.
- The exported Room schema (`app/shared/schemas/com.base.db.AppDatabase/10.json`) is
  produced with the change, mirroring 9.json plus the new column.
- DAO ordering becomes `ORDER BY sortOrder ASC, createdAt DESC` (the tie-breaker only
  matters for rows written before the backfill or by older builds).

### Repository reorder operation
- `CategoryRepository.reorderCategories(orderedIds: List<Long>)` — a DAO `@Transaction`
  that writes `sortOrder = index` for each id. The fake repository re-sorts its
  in-memory list the same way.
- `addCategory` assigns `sortOrder = MAX(sortOrder) + 1` (0 when the table is empty),
  so new categories append at the end.
- `deleteCategory` leaves gaps in the sequence; ordering stays correct because only
  relative values matter, and the next reorder rewrites a dense sequence.

### Drag interaction on the management destination
- Add the Compose Multiplatform reorderable library
  (`sh.calvin.reorderable:reorderable`) — it supports the app's targets (Android,
  desktop/JVM, iOS, wasm) and provides `rememberReorderableLazyListState` +
  `ReorderableItem` for `LazyColumn` with long-press drag.
- The shared category row gains an optional leading slot; on the management
  destination it hosts a drag handle (`Icons.Default.DragHandle`, content description
  from a new string resource). Only the handle starts a drag, so row content and the
  delete button keep their own gestures. While dragging, the row renders elevated
  (shadow + slightly raised container color), matching the `:design` prototype.
- During a drag the list reorders locally in the component state; on drop the
  component persists once via `onCategoriesReordered(orderedIds)` →
  `CategoryManagementComponent.onCategoryMoved(fromIndex, toIndex)` applies live
  moves to state, and the screen calls the persist callback when the drag ends.
  Simpler equivalent accepted during implementation: persist the full id order on
  every drop only, with local visual reorder handled by the library state.
- Reorder applies to the full list; while a search query is active the handles are
  hidden and dragging is disabled (reordering a filtered subset would be ambiguous).
  Search results keep the manual relative order.

### Keyboard operation (repo accessibility rule)
- The drag handle is focusable. With the handle focused, ArrowUp/ArrowDown moves the
  row one position (the component applies the move and persists immediately; focus
  follows the row because the handle's test tag / semantics stay attached to the
  category id). This path is covered by the E2E UI test's keyboard journey.

### Selection page
- No UI changes. It consumes the same ordered flow; the Uncategorized entry remains
  pinned first and outside the order, and search filtering preserves relative order
  (filtering never re-sorts).

## Design-first gate

The drag-handle row and the dragging visual state are first built in the `:design`
module (category list feature + Catalog states: populated with handles, dragging),
sent to the product owner as screenshots, and approved before any app code changes —
the same gate as the redesign change.

## Risks

- The reorderable library version must match the project's Compose version; pin it in
  `gradle/libs.versions.toml` and verify on CI (all targets compile in the verify job).
- Migration correctness is covered by repository tests asserting the preserved order
  semantics at the data layer (the backfill ordering rule is expressed as: existing
  rows keep their relative `createdAt DESC` order).
