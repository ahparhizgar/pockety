---
name: openspec-apply-tests
description: Apply and implement test specifications from an OpenSpec change into actual test code. Use when generating or scaffolding test suites from tests.md before production implementation (TDD Red phase), or when user says "apply tests", "implement tests", or "openspec apply tests".
allowed-tools: Bash(openspec:*)
license: MIT
compatibility: Requires openspec CLI.
metadata:
  author: pockety
  version: "1.0"
---

Transform test specifications from an OpenSpec change's `tests.md` into concrete, executable test files in the codebase (TDD Red Phase).

**Store selection & Project check:** Same as standard OpenSpec skills. If `--store <id>` is specified or needed, keep it sticky.

**Input**: Optionally specify a change name (e.g. `openspec-apply-tests sms-expense-notification`). If omitted, infer from context or active change.

---

## Purpose & TDD Philosophy

`openspec-apply-tests` is the bridge between the planning phase and the coding phase:
1. **Reads `tests.md`**: Parses behavioral test specifications, scenarios, and targeted files.
2. **Generates / Updates Test Code**: Places tests into their appropriate capability packages under `app/shared/src/commonTest/kotlin/com/base/capability/<capability_name>/` (or platform source sets like `app/androidApp/src/test/kotlin/` if Android-specific).
3. **Applies Minimal Stubs (Compilable Red)**: If new types, methods, or components referenced by the tests don't exist yet, creates minimal stub definitions (`TODO("Not implemented")`, dummy methods, or state properties) so that the codebase **compiles**.
4. **Verifies Compilation & Red State**: Ensures compilation passes and the new tests fail cleanly as expected before production implementation begins.

---

## Steps

### 1. Select the change & Query OpenSpec CLI

- If a change name is provided, use it. Otherwise, query OpenSpec:
  ```bash
  openspec list --json
  ```
- Fetch change status and schema metadata to locate artifacts dynamically:
  ```bash
  openspec status --change "<name>" --json
  ```
  Inspect:
  - `changeRoot`: the base path for this change
  - `schemaName`: verify schema (e.g. `spec-driven-custom`)
- View or verify change contents:
  ```bash
  openspec show "<name>" --type change --json
  ```
- Locate `tests.md` from the resolved `changeRoot` (e.g. `<changeRoot>/tests.md`) and read its specifications. Also read `design.md` or delta specs if context on types and contracts is needed.

### 2. Map Test Files to Capability Structure

Strictly follow `AGENTS.md` rules for structuring tests:
- Base package: `com.base.capability.<capability_name>`
- File naming conventions:
  - `<Capability>Test.kt` or `<Requirement>Test.kt` for component / unit behavior.
  - `<Capability>FlowE2eTest.kt` for navigation & flow integration using `RootComponent`.
  - `<Capability>E2eUiTest.kt` for UI interaction & keyboard accessibility testing.
  - `<Target>RepositoryTest.kt` for repository integration tests.
  - Platform-specific tests (e.g. Android Notification Manager) placed in the corresponding module (`app/androidApp/src/test/kotlin/...`).

### 3. Generate Test Implementation

Follow all testing rules defined in `AGENTS.md`:
- **Kotest BehaviorSpec**: Structure tests with `Given`, `When`, `Then` blocks.
  - **Never combine When and Then** (`When("selecting x")`, `Then("y happens")`).
- **Fake Factories**: Use `Fake*Factory.create()` with default parameters instead of raw constructors. Never inline fake implementations as objects; use reusable fake classes.
- **Decompose Assertions**: Use extension assertions like `root.assertAddExpense()` or `root.assertHomeComponent()`.
- **Keyboard Accessibility**: For UI tests, verify interactions using keyboard actions.
- **Component Scope**: Don't check internal implementation details; assert public state or user-observable outcomes.

### 4. Provide Minimal Compilable Stubs

Tests should compile! If the tests introduce new methods, events, or state properties:
- Add stub declarations to the target interfaces/classes:
  ```kotlin
  fun someNewAction() {
    TODO("Not implemented yet")
  }
  ```
- Add stub fields or events needed by the tests.
- **Do NOT implement production business logic** — leave that for `openspec-apply-change` (the Green phase).

### 5. Format & Verify Compilation (Level 1)

1. Format code:
   ```bash
   ./gradlew ktfmtFormat
   ```
2. Verify compilation:
   ```bash
   unset ANDROID_PREFS_ROOT; ./gradlew compileTestKotlinJvm compileDebugUnitTestKotlin --offline --continue 2>&1 | grep -E "e: |FAILED"
   ```
3. Run tests to confirm the **Red Phase** (they should fail due to `TODO()` or unimplemented assertions, NOT compilation errors):
   ```bash
   unset ANDROID_PREFS_ROOT; ./gradlew jvmTest --tests "com.base.capability.<capability_name>.*"
   ```

### 6. Report Summary

Present to the user:
- The generated test files and their locations.
- Any stubbed methods or symbols created.
- Confirmation that tests compile and fail cleanly (TDD Red phase confirmed).
- Suggest proceeding to `openspec-apply-change` to implement the Green phase.
