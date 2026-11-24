# Tasks

## 1. Design in the `:design` module (approval gate)

- [x] 1.1 Extend the redesigned category list in `:design` with a drag handle on each row (leading slot of the shared row component) and a dragging visual state; register the new states in `Catalog.kt`
- [x] 1.2 Send the design screenshots to the product owner and get explicit approval before starting group 2 — no app code changes before this gate

## 2. Data layer: persistent manual order

- [x] 2.1 Add `sortOrder` to `CategoryEntity`, bump the database to version 10 with `MIGRATION_9_10` (add column + order-preserving backfill), register the migration, and add the exported schema `10.json`
- [x] 2.2 Order the DAO query by `sortOrder`, add the transactional reorder update, make `addCategory` append at the end, and mirror ordering/reorder in `FakeCategoryRepository` — verify with the extended `CategoryRepositoryTest`

## 3. Management component and screen: drag reorder

- [x] 3.1 Add the reorderable library to the version catalog and the shared module; extend `CategoryManagementComponent` with move/reorder callbacks that update state live and persist the id order on drop — verify with the extended `CategoryManagementTest`
- [x] 3.2 Add the optional leading slot to the shared category row, render drag handles on the management list (hidden while searching), wire long-press drag with the dragging visual state, and make a focused handle move its row with ArrowUp/ArrowDown — verify with the extended `CategoryManagementE2eUiTest` keyboard journey
- [x] 3.3 Add `onCategoryMoved(fromIndex, toIndex)` (local state reorder) and `onCategoryMoveFinished()` (persists via `reorderCategories`) to the component; `FakeCategoryManagementComponent` mirrors both.

## 4. Verification

- [x] 4.1 Cover the reorder journey with tests: repository ordering/reorder/append/delete cases, component move + persist-on-finish, selection list showing the manual order, the v9→v10 migration test, and the keyboard reorder UI journey
- [x] 4.2 Run the full verification gates (compile, jvmTest, coverage, detekt, ktfmt) via CI, push, pull, and run the screenshot-diff routine; verify CI is green and only the expected new/changed screenshots appear

## Workflow follow-up

- After implementation and review, sync the specs and archive the change with the openspec archive workflow.
