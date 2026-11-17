# Tasks

## 1. Localized debug identity

- [ ] 1.1 Add default/English and Persian debug-only app_name overlays; build debug and inspect resolved packaged labels for `Debug Pockety` and `Debug جیبک`, recording locale evidence from tests.md.

## 2. Debug icon suite

- [x] 2.1 Use the icon-resource skill to create reproducible bug-badged Pockety artwork and debug-only legacy square/round assets for all five densities, adaptive and monochrome resources; retain editable source and regeneration instructions, and verify the generated file set without changing production resources.
- [ ] 2.2 Resolve foreground qualifier shadowing, then inspect merged/packaged debug resources and deliver native-density, circle/squircle/rounded-square and light/dark monochrome preview evidence demonstrating recognizable identity and an unclipped bug badge.

## 3. Variant integration and delivery

- [ ] 3.1 Build debug and release Android artifacts; verify localized release labels and artwork against the main baseline, both application IDs, unchanged signing behavior and unchanged desktop/web branding; record commands, results and any device-only limitations in the handoff.
- [x] 3.2 Run AGENTS.md compilation, jvmTest/test, koverVerify, detektMain and final ktfmtFormat gates; resolve failures caused by this change, report unrelated/environment failures precisely, and verify GitHub Actions CI passes for the final implementation head.
- [ ] 3.3 Commit and push the implementation on this change branch, keeping history linear with current main; return the branch/head, CI link and verification/preview evidence to Analyst for archive.

## Workflow follow-up

- Analyst verifies implementation and CI, archives with spec synchronization, rebases linearly onto main, pushes and cleans up the worktree/remote change branch.
- Return POC-37 to CTO for final origin/main and CI verification and closure.
