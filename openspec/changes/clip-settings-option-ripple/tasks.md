# Tasks

## 1. Rounded option interaction

- [x] 1.1 Update shared OptionSelectionCard to contain selection indication within its 16.dp Surface shape, using the supported selectable Surface overload or the documented inner selectable fallback; verify unchanged styling, dimensions, shadow, caller test tags, and one interaction owner by diff and preview inspection.
- [x] 1.2 Extend settings UI tests using keyboard focus and activation for both currency and calendar options in both directions; verify public selected state, selected/unselected RadioButton semantics and one callback per activation, including already-selected cards, per tests.md.
- [ ] 1.3 Perform corner-press visual checks for both option families in selected/unselected states; record platform, revision and results/evidence, comparing dimensions and selected shadow against baseline; report any unmet acceptance check explicitly.

## 2. Integration verification and handback

- [ ] 2.1 Run AGENTS.md compilation, jvmTest/test, koverVerify, detektMain and ktfmtFormat gates using prescribed offline flags; inspect exit results, resolve introduced failures, and record environmental or baseline failures with evidence.
- [ ] 2.2 Validate OpenSpec strictly, commit implementation and verification evidence, push the change branch and verify GitHub Actions for the exact implementation head; provide Analyst the commit, CI run/result links and acceptance results before archive.

## Workflow follow-up

- Developer returns POC-38 to Analyst after implementation and passing CI; keep POC-37 changes separate.
- Analyst archives with spec sync, rebases linearly onto origin/main, pushes without merge commits, verifies CI, cleans the worktree/remote branch, and returns the issue to CTO for final sign-off.
