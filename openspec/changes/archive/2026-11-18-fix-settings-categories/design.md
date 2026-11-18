# Design

## Context
See proposal.md for motivation. Verified origin/main f6e0a84: MainComponent.createSettingsChild supplies only onPrivacyPolicyClick; SettingsComponentFactory and DefaultSettingsComponent default onCategoriesClick to an empty callback. CategorySelectionComponent observes CategoryRepository but invokes an assignment callback for rows and Uncategorized. No standalone category-management UI exists. AddCategoryComponent already validates, persists and reports completion. PocketSettingsFlowComponent provides the local list/add navigation pattern, but completion assigns the new category to a pocket and must not be reused wholesale.

## Goals / Non-Goals
**Goals:** Isolate management navigation from assignment, retain Main and Settings underneath the root destination, reuse category persistence and the add form.
**Non-Goals:** No selection-mode flags that mix management and assignment contracts; no database changes, uniqueness redesign or unrelated Settings callback rewiring.

## Decisions
- Introduce CategoryManagementFlowComponent with browse and AddCategory children and a root CategoryManagementFlow destination. Register root child creation/rendering and DI using existing flow conventions. Main explicitly wires Categories to push this destination. Keeping Main on the root back stack preserves the selected Settings tab and state; replacing Main would lose that context.
- Give browsing a dedicated component with Value state populated from CategoryRepository.observeCategories(). Its actions are Add and Back; category cards are informational. Reuse icon resolution and suitable extracted public visual components, passing states/callbacks. Do not reuse the selector through a noop selection callback or expose Uncategorized/Active badges: these imply assignment and conceal wiring mistakes. Search is optional reuse only if inexpensive, not an acceptance dependency.
- Reuse AddCategoryComponent/Screen unmodified where practical. Completion and cancel pop one local child to browsing; root Back closes the flow. Platform Back from Add also returns to browsing. Reactive observation updates both browsing and the existing Settings category count without manual refresh. No callback invokes expense or pocket components.
- Make onCategoriesClick mandatory in the production Settings factory/component signatures and update call sites explicitly, including test fixtures. Do not expand scope to Labels or platform language actions. Ensure new production navigation callbacks have no default noops.
- Use Scaffold/Surface, PocketyTopAppBar and existing spacing/theme. Browse uses centered bounded content with an adaptive LazyVerticalGrid; empty state retains Add. Add/Back use focusable Material controls and resource descriptions. New/touched journey strings have English and Persian resources using current terminology. Keep shared resources additive and rebase against POC-41 changes.
- Add @ScreenPreviews/@ComponentPreviews for populated/empty compact and large/RTL states. Extract private sections passing the whole Decompose component; public reusable components accept state/callbacks. Apply keyboard options/actions to any introduced text field.

## Risks / Trade-offs
- Nested Back handling can accidentally close the whole flow → assert platform and visible Back at each depth.
- Shared add UI has existing hard-coded labels → localize labels required for this journey, coordinate overlaps with POC-41; avoid a broad audit.
- Reactive counts can lag in tests → advance coroutine work and assert component/UI state, not repository calls.
- Additional flow adds wiring points → root-entry integration test must use actual Main/Settings and real category components.

## Migration Plan
No schema migration or new dependencies. Ship shared-client navigation/UI together. Revert the feature commit to roll back; saved categories use the existing schema. Developer rebases the branch linearly and returns after checks and GitHub Actions CI pass; Analyst archives/syncs and rebases onto main before CTO sign-off.
