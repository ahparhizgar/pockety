# Tests Specification

## UI & Accessibility Tests

### `pocket/PocketListE2eUiTest`

**Target**: Pocket list screen cards and interactions

**Scenarios**:

1. Pocket card displays name, category tag, and remaining balance without top-up button:
   - Given a configured pocket in the repository
   - When user navigates to the Pockets tab
   - Then the pocket budget card is displayed with category tag, name, and balance
   - And no top-up button exists inside the pocket budget card

2. Clicking pocket card navigates to pocket details:
   - Given a configured pocket on the Pockets screen
   - When user clicks the pocket budget card
   - Then navigation to Pocket Details screen occurs seamlessly

## E2E Flow Tests

### `pocket/details/PocketDetailsFlowE2eTest`

**Flow**: Verification that top-up remains fully functional inside Pocket Details

**Scenarios**:

- Given a pocket exists and user is on the Pockets tab
    - When user clicks a pocket card in Pockets list
        - [ ] Then active child in RootComponent becomes PocketDetailsComponent
        - And user clicks top-up in PocketDetailsComponent
            - [ ] Then QuickTopUpSheet opens and allows topping up pocket balance
