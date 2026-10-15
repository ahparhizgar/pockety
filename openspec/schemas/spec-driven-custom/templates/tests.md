# Tests Specification

## Test Strategy & Capability Mapping

<!-- Map specifications/scenarios to test types according to the AGENTS.md conventions:
- Capability unit & component behavior tests (`<Capability>Test.kt`)
- Full capability integration/navigation flow tests (`<Capability>FlowE2eTest.kt`)
- UI & accessibility tests with keyboard interactions (`<Capability>E2eUiTest.kt`)
- Dedicated data/repository tests if applicable (`<Target>RepositoryTest.kt`)
-->

## Capability Tests

### `com.base.capability.<name>.<Capability>Test`
- **Target**: <!-- Component / business logic under test -->
- **Scenarios**:
  - [ ] Given <!-- initial state/context -->, When <!-- action -->, Then <!-- expected state/output -->

## Integration & E2E Flow Tests

### `com.base.capability.<name>.<Capability>FlowE2eTest`
- **Flow**: <!-- Navigation and integration flow from root component -->
- **Scenarios**:
  - [ ] Given <!-- initial state/context -->, When <!-- user navigates and performs actions -->, Then <!-- end-to-end verified state -->

## UI & Accessibility Tests

### `com.base.capability.<name>.<Capability>E2eUiTest`
- **Focus**: UI behavior, keyboard accessibility, previews
- **Scenarios**:
  - [ ] Given <!-- UI component loaded -->, When <!-- navigating via keyboard / typing -->, Then <!-- accessible and correct interaction -->
