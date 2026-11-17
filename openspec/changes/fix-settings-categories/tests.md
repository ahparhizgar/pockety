# Tests Specification

Use com.base.feature.category.management packages per AGENTS.md (the schema's capability-package example yields to the explicit project feature-package convention). Use fake factories for data, reusable fake classes and root assertion helpers; never assert repository calls or private navigation internals.

## Unit Tests (single component / class / function)

### `category/management/CategoryManagementTest`
**Target:** Browsing component with existing category data fixtures and the real observation path where practical.
- Given a populated collection
  - When browsing starts
    - [ ] Then public state exposes category names, icons and colors.
- Given an empty collection
  - When browsing starts
    - [ ] Then public state is empty and creation remains available in UI coverage.
- Given browsing
  - When Add or Back is activated
    - [ ] Then its explicit callback is invoked (callback parameter assertions are allowed).
Reuse existing AddCategoryTest validation cases; do not duplicate persistence implementation tests.

## E2E Flow Tests

### `category/management/CategoryManagementFlowE2eTest`
**Flow:** Real Root → Main → Settings → Categories → Add → Categories → Settings, using existing test DI/data-layer conventions and real navigation/category components, not a fake Settings callback-only test.
- Given seeded categories and non-default Settings preferences
  - When Categories is activated from Settings
    - [ ] Then the root exposes the management flow with the seeded collection.
  - When Add is opened and a valid named category with chosen icon/color is saved
    - [ ] Then browsing is active and its state includes the saved category.
  - When visible Back is invoked
    - [ ] Then Main still has Settings selected, its category count is incremented and preference values are retained.
- Given no categories
  - When Settings Categories is opened
    - [ ] Then browsing state is empty.
  - When Add is opened and cancelled
    - [ ] Then browsing returns with an empty collection.
  - When platform Back is dispatched
    - [ ] Then Settings is active.
- Given Add opened from management
  - When platform Back is dispatched
    - [ ] Then browsing returns without saving and the flow remains open.
- Given an expense and pocket with assigned category fixtures
  - When the management create/cancel/return journey completes and their existing details/edit flows are reopened
    - [ ] Then the same associations remain visible through component state.
- Given existing expense and pocket category selection flows
  - When an existing category is selected or a category is created through those flows
    - [ ] Then their expected assignment and return behavior remains intact (reuse/extend existing feature tests).

## Repository & Data Tests
No new migration or repository behavior. Run existing CategoryRepositoryTest; validate new journey via public component state rather than inspecting repositories in component tests.

## UI & Accessibility Tests

### `category/management/CategoryManagementE2eUiTest`
- Given the app at Settings with categories
  - When Tab/Shift+Tab and Enter/Space activate Categories and Add, keyboard input fills the name and keyboard actions save
    - [ ] Then the destination displays the new category name and has no Uncategorized or selected/Active control.
  - When keyboard activates visible Back
    - [ ] Then Settings and its updated category badge are displayed.
- Given an empty collection
  - When Categories is opened
    - [ ] Then explanatory empty text and a keyboard-reachable Add action are displayed.
- Given Persian locale and RTL
  - When the keyboard journey is performed
    - [ ] Then visible labels/descriptions use Persian resources and navigation remains usable.
- Given compact and expanded windows
  - When populated and empty previews/screens render
    - [ ] Then bounded centered content and adaptive grid avoid clipped controls; focus is visible and Add/Back remain reachable.

Use @ScreenPreviews/@ComponentPreviews for populated, empty and expanded/RTL states. UI tests operate by keyboard; preview/manual inspection can supplement layout verification. Record actual CI and required gate results, with any failures attributed rather than hidden.
