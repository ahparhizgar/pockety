# Tasks

## 1. Language-independent Analytics state and date presentation

- [ ] 1.1 Add root-to-Analytics regression coverage in com.base.feature.expense.analytics for language changes preserving selected calendar/month, expense data, totals, and navigation; verify observable state using existing integration fakes and factory conventions.
- [ ] 1.2 Replace component-built date prose with typed presentation inputs and add bilingual date patterns/month-name resources and client resource resolution; update reusable factories and verify Today/Yesterday/older labels for all four language/calendar combinations, including calendar boundaries.
- [ ] 1.3 Complete the four Persian static keys and audit all Analytics app-owned text and action descriptions; verify actual English/Persian labels and descriptions on empty/populated screens with UI assertions.

## 2. Amount presentation and supported language refresh

- [ ] 2.1 Replace literal T in monthly/group/item/zero amounts with existing localized Toman resources while preserving numeric values, separators, and signs; verify rendered values and original descriptions/category names, and document the audited currency-preference limitation without adding conversion.
- [ ] 2.2 Verify the supported fallback settings language flow and coordinate resource locale with appLanguage where required using the smallest existing-architecture integration; add keyboard-driven English → Persian → English UI regression coverage and verify no stale dynamic/static text or calendar/month reset.
- [ ] 2.3 Preserve keyboard navigation, accessibility descriptions and tags and maintain empty/populated @ScreenPreviews and affected public @ComponentPreviews; verify keyboard month/expense activation, current-month next disabling, and preview compilation.

## 3. Integration verification and Developer handback

- [ ] 3.1 Verify Android system per-app language resume behavior on an available device/emulator and record evidence; if unavailable, explicitly report the platform validation limitation and retain automated shared-flow coverage.
- [ ] 3.2 Run required compilation, jvmTest/test, koverVerify, detektMain and ktfmtFormat gates from AGENTS.md; inspect results and record any environmental failures accurately, rerunning affected checks after formatting changes when necessary.
- [ ] 3.3 Validate OpenSpec strictly, commit/push implementation, rebase linearly against latest origin/main preserving concurrent Persian keys, and verify GitHub Actions on the implementation commit; return the issue to Analyst with commit, CI links, gate results, and remaining limitations.

## Workflow follow-up

- Analyst archives with spec synchronization only after Developer implementation and successful CI; rebase linearly onto main, push without merge commits, clean up the worktree/remote change branch, and return to CTO for final sign-off.
