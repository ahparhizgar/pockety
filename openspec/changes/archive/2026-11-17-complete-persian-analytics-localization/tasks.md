# Tasks

## 1. Language-independent Analytics state and date presentation

- [x] 1.1 Add root-to-Analytics regression coverage in com.base.feature.expense.analytics for language changes preserving selected calendar/month, expense data, totals, and navigation; verify observable state using existing integration fakes and factory conventions.
- [x] 1.2 Replace component-built date prose with typed presentation inputs and add bilingual date patterns/month-name resources and client resource resolution; update reusable factories and verify Today/Yesterday/older labels for all four language/calendar combinations, including calendar boundaries.
- [x] 1.3 Complete the four Persian static keys and audit all Analytics app-owned text and action descriptions; verify actual English/Persian labels and descriptions on empty/populated screens with UI assertions.

## 2. Amount presentation and supported language refresh

- [x] 2.1 Replace literal T in monthly/group/item/zero amounts with existing localized Toman resources while preserving numeric values, separators, and signs; verify rendered values and original descriptions/category names, and document the audited currency-preference limitation without adding conversion.
- [x] 2.2 Verify the supported fallback settings language flow and coordinate resource locale with appLanguage where required using the smallest existing-architecture integration; add keyboard-driven English → Persian → English UI regression coverage and verify no stale dynamic/static text or calendar/month reset.
- [x] 2.3 Preserve keyboard navigation, accessibility descriptions and tags and maintain empty/populated @ScreenPreviews and affected public @ComponentPreviews; verify keyboard month/expense activation, current-month next disabling, and preview compilation.

## 3. Integration verification and Developer handback

- [ ] 3.1 Verify Android system per-app language resume behavior on an available device/emulator and record evidence; if unavailable, explicitly report the platform validation limitation and retain automated shared-flow coverage.
- [x] 3.2 Run required compilation, jvmTest/test, koverVerify, detektMain and ktfmtFormat gates from AGENTS.md; inspect results and record any environmental failures accurately, rerunning affected checks after formatting changes when necessary.
- [x] 3.3 Validate OpenSpec strictly, commit/push implementation, rebase linearly against latest origin/main preserving concurrent Persian keys, and verify GitHub Actions on the implementation commit; return the issue to Analyst with commit, CI links, gate results, and remaining limitations.

## Verification record

- Exact-head GitHub Actions CI passed on `30d2d8b732c75585ee39052af3971bf92d5f5a66` (run [44202503307](https://github.com/ahparhizgar/pockety/actions/runs/44202503307)): compilation, JVM and platform tests, coverage, Detekt, Ktfmt, and Design Web build all succeeded.
- `ktfmtFormat` and strict OpenSpec validation passed locally. Local compilation and tests were not run per the Developer no-local-build constraint; CI ran these gates remotely.
- Task 3.1 remains open: Android system per-app language resume behavior could not be checked because no Android device/emulator (`adb`) is available in this environment. Automated shared-flow coverage passed.
- Currency preference audit confirmed no shared preference-aware amount formatter exists; existing Toman presentation was localized without inventing a Rials conversion.

## Workflow follow-up

- Analyst archives with spec synchronization only after Developer implementation and successful CI; rebase linearly onto main, push without merge commits, clean up the worktree/remote change branch, and return to CTO for final sign-off.

## Analyst archive review

- Independently confirmed completed/success CI run 44202503307 on exact implementation head 30d2d8b; Compile, Tests, Coverage, Detekt, Ktfmt check, and Design Web succeeded.
- Reviewed production Root/Main/Settings keyboard language-switch UI regression and retained-state assertions.
- Archived with task 3.1 intentionally incomplete under the authorized archive/sync handoff; Android device validation remains a reported limitation, not a passed check.
- Post-sync strict main-spec validation passed (12/12). Full strict validation also inspects the pre-existing unrelated move-expenses-to-analyze change and reports its omitted expense-list scenario; this change does not modify that artifact.
