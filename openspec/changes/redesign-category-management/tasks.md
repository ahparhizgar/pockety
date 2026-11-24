# Tasks

## 1. Design in the `:design` module (approval gate)

- [x] 1.1 Build the redesigned Categories page in `:design` following the designer skill (feature folder with `Screen.kt`/`Component.kt`, selection-style list rows with colored icon tiles, search field, delete icon button per row, bottom create button) and verify `:design:compileKotlinJvm` passes
- [x] 1.2 Add the delete-confirmation dialog state and the populated, empty, no-search-results, and dark states; register all of them in `Catalog.kt` and verify the catalog screenshot tests capture them
- [x] 1.3 Send the design screenshots to the product owner and get explicit approval before starting group 2 — no app code changes before this gate

## 2. Shared category row component

- [x] 2.1 Extract the selection screen's private row into a shared category-row component parameterized by name, icon, tile color, optional description, optional selected state, and an optional trailing-action slot; rewire the selection screen to use it and verify the existing selection tests pass
- [ ] 2.2 Push and verify with the screenshot-diff routine that every existing selection screenshot is byte-identical (no visual regression)

## 3. Management component: search and delete

- [x] 3.1 Extend `CategoryManagementState` and the fake component with a search query, a filtered list, and a pending-deletion category; add `onSearchQueryChanged`, `onDeleteClicked`, `onDeleteConfirmed`, and `onDeleteDismissed` to the component, where confirm calls `CategoryRepository.deleteCategory` — verify with the new `CategoryManagementTest` scenarios in tests.md
- [x] 3.2 Add the EN and FA string resources for the delete action, the confirmation dialog title/message naming the category, and the confirm/cancel actions; verify both locales compile into the resources

## 4. Management screen redesign

- [x] 4.1 Rebuild `CategoryManagementScreen` as the approved design: centered column bounded at 640dp, search field, `LazyColumn` of shared rows with a delete trailing action, selection-style bottom create button replacing the floating button, existing empty state for no categories, and the no-results empty state for unmatched searches — verify the extended `CategoryManagementE2eUiTest` passes, including the keyboard journey
- [x] 4.2 Add the delete-confirmation `AlertDialog` bound to the pending-deletion state and update the screen previews (populated, empty, no-results, dialog, RTL) — verify the new previews are captured by the screenshot tests

## 5. Flow and data verification

- [x] 5.1 Extend `CategoryManagementFlowE2eTest` with the delete journey (Settings → Categories → delete a category assigned to an expense → expense becomes uncategorized, Settings count decreases) and verify it passes
- [x] 5.2 Extend `CategoryRepositoryTest` if needed so `deleteCategory` is covered for the expense/pocket SET_NULL behavior and verify it passes
- [ ] 5.3 Run the full verification gates (compile, jvmTest, coverage, detekt, ktfmt) via CI, push, pull, and run the screenshot-diff routine; verify CI is green and only the expected new/changed screenshots appear

## Workflow follow-up

- After implementation and review, sync the specs and archive the change with the openspec archive workflow.
