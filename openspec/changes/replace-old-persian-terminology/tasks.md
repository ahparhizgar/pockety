# Tasks

## 1. Persian terminology correction

- [x] 1.1 Update the three identified Persian resource values, replacing «پاکتی» with «جیبک» before «پاکت» with «جیب»; verify the diff preserves keys, `%1$s`, surrounding copy, and half-spaces, and XML remains valid.
- [x] 1.2 Audit current user-facing Persian sources for remaining old terminology and correct any occurrences within scope; verify no old terms remain in active source roots and English text and stored content are unchanged.
- [x] 1.3 Perform the settings version, privacy policy, English locale, and keyboard smoke checks in tests.md; record observed results or concrete environment limitations.

## 2. Integration verification

- [ ] 2.1 Run AGENTS.md compilation, JVM/server tests, coverage, Detekt, and final ktfmt gates; record outcomes and resolve change-related failures. Verify resource compilation includes the affected shared resources.
- [ ] 2.2 Commit and push implementation on change/replace-old-persian-terminology, verify GitHub Actions CI for the final implementation commit, and hand back to Analyst with commit and CI links. Keep history linear and separate POC-37/POC-38 changes.

## Workflow follow-up

- Analyst archives with spec sync after implementation and CI pass, rebases onto latest origin/main, pushes linearly, and removes this worktree and remote change branch.
- Return POC-39 to CTO for final origin/main and CI verification and closure.
