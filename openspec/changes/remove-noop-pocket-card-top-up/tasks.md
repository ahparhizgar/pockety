# Tasks

## 1. Pocket Card UI Refactoring

- [ ] 1.1 Remove `onTopUpClick` parameter, `PocketCardActions` composable, and unused imports from `PocketBudgetCard.kt` in `app/shared` and verify preview/compilation
- [ ] 1.2 Remove `onTopUpClick = {}` invocation argument from `PocketsScreen.kt` in `app/shared` and verify screen builds cleanly
- [ ] 1.3 Clean up unused `pockets_top_up_action` string resource from strings.xml if unreferenced and verify resources compile

## 2. Verification and Tests

- [ ] 2.1 Update/run `PocketListE2eUiTest` to verify that pocket cards render correctly without the top-up button
- [ ] 2.2 Run `PocketDetailsFlowE2eTest` and `PocketDetailsE2eUiTest` to verify that top-up remains fully functional inside Pocket Details

## Workflow follow-up

- Archive the change after implementation and CI checks pass.
- Verify the archived result and rebase onto main.
