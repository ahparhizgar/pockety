# Tasks

## 1. Settings category browse-and-add vertical slice

- [ ] 1.1 Add root-entry regression scenarios in com.base.feature.category.management using integrationKoin and reusable root/flow assertion helpers; confirm the current Settings action fails to open a management destination before implementation.
- [ ] 1.2 Implement dedicated category browsing Value state observing CategoryRepository, management browse/add flow, required callbacks, root destination/rendering and DI bindings; explicitly wire Main Settings Categories and remove its production default noop. Verify root → Settings → Categories integration and populated/empty state tests pass.
- [ ] 1.3 Implement the minimum browse screen with informational name/icon/color cards, empty state, Add and Back, extracted components and adaptive centered grid. Reuse existing add form and suitable category visuals. Verify populated/empty previews and keyboard UI entry/create/return tests pass.
- [ ] 1.4 Complete English/Persian resources for the new destination and any labels needed in the reused add journey, with RTL and compact/expanded previews using approved annotations. Verify locale UI checks and visible focus/reachable controls; reconcile shared strings with POC-41 through rebase.
- [ ] 1.5 Cover save/cancel and visible/platform Back at both depths, updated collection and Settings count, retained Settings preferences and unchanged expense/pocket associations through public component states. Verify CategoryManagementFlowE2eTest and relevant existing expense/pocket category selection/add regressions pass.

## 2. Integration verification and Developer handback

- [ ] 2.1 Run ./gradlew compileKotlinJvm compileKotlin --offline --continue --console=plain, ./gradlew jvmTest test --offline --continue --console=plain and ./gradlew koverVerify --offline --console=plain; retain exit statuses/logs and resolve failures introduced by the change.
- [ ] 2.2 Run ./gradlew :detektMain --offline --console=plain and, after all code changes, ./gradlew ktfmtFormat --offline --console=plain; inspect formatting diff and run ktfmtCheck. Record passing results or concrete external/pre-existing failures without claiming success.
- [ ] 2.3 Validate openspec validate fix-settings-categories --strict, rebase linearly onto latest origin/main, commit/push and open a PR or dispatch CI (change-branch pushes alone do not trigger the current CI workflow). Verify GitHub Actions CI for the exact implementation SHA and return branch/SHA/CI links plus verification evidence to Analyst, leaving archive to Analyst.

## Workflow follow-up

- Analyst verifies implementation and CI, loads archive skill, archives with spec sync, rebases onto origin/main and pushes HEAD:main without merge commits.
- Analyst removes the isolated worktree/remote change branch only after successful integration and reassigns POC-42 to CTO for origin/main and CI verification and final sign-off.
