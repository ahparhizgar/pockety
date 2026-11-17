# Tasks

## 1. Settings category browse-and-add vertical slice

- [x] 1.1 Add root-entry regression scenarios in com.base.feature.category.management using integrationKoin and reusable root/flow assertion helpers; confirm the current Settings action fails to open a management destination before implementation.
- [x] 1.2 Implement dedicated category browsing Value state observing CategoryRepository, management browse/add flow, required callbacks, root destination/rendering and DI bindings; explicitly wire Main Settings Categories and remove its production default noop. Verify root → Settings → Categories integration and populated/empty state tests pass.
- [x] 1.3 Implement the minimum browse screen with informational name/icon/color cards, empty state, Add and Back, extracted components and adaptive centered grid. Reuse existing add form and suitable category visuals. Verify populated/empty previews and keyboard UI entry/create/return tests pass.
- [x] 1.4 Complete English/Persian resources for the new destination and any labels needed in the reused add journey, with RTL and compact/expanded previews using approved annotations. Verify locale UI checks and visible focus/reachable controls; reconcile shared strings with POC-41 through rebase.
- [x] 1.5 Cover save/cancel, visible Back, and flow Back handling from Add; cover platform Back from browsing, updated collection and Settings count, retained Settings preferences, and preserved expense/pocket category flows. Verify CategoryManagementFlowE2eTest and relevant existing expense/pocket category selection/add regressions pass.

## 2. Integration verification and Developer handback

- [x] 2.1 GitHub Actions verified compile, JVM tests and coverage on the exact implementation SHA; local Gradle compile/test commands were not run per Developer environment constraints.
- [x] 2.2 Ran ktfmtFormat locally; GitHub Actions Detekt and ktfmt check both passed.
- [x] 2.3 Validated OpenSpec, dispatched and verified GitHub Actions CI on the exact implementation SHA, and returned branch/SHA/CI evidence to Analyst. Analyst owns archive and integration.

## Workflow follow-up

- Analyst verifies implementation and CI, loads archive skill, archives with spec sync, rebases onto origin/main and pushes HEAD:main without merge commits.
- Analyst removes the isolated worktree/remote change branch only after successful integration and reassigns POC-42 to CTO for origin/main and CI verification and final sign-off.
