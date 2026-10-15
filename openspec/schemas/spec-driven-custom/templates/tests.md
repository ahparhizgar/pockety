# Tests Specification

## Unit Tests (single component / class / function)

### `<name>/<Capability>Test`

**Target**: <!-- Component / business logic under test -->

**Scenarios**:

- Given <!-- initial state/context -->
    - When <!-- action -->,
        - [ ] Then <!-- expected state/output -->

## E2E Flow Tests

### `<name>/<Capability>FlowE2eTest`

**Flow**: <!-- Navigation and integration flow from root component -->

**Scenarios**:

- Given <!-- initial state/context -->
    - When <!-- user navigates and performs actions -->
        - [ ] Then <!-- end-to-end verified state -->
        - And <!-- user performs another action -->
            - [ ] Then <!-- end-to-end verified state -->
- Given <!-- another initial state/context -->
    - When <!-- user navigates and performs actions -->
        - [ ] Then <!-- end-to-end verified state -->

## UI & Accessibility Tests

### `<name>.<Capability>E2eUiTest`

**Scenarios**:

1. explain the scenario
2. explain the scenario
