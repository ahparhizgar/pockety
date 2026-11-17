# Tests Specification

## Unit Tests (single component / class / function)
Reuse existing settings component tests for preference updates. No new business logic; no implementation-detail tests of modifier order.

## E2E Flow Tests
Reuse `com.base.feature.settings.SettingsFlowE2eTest` for navigation and persisted preference behavior. Run existing coverage rather than duplicating data-layer tests.

## Repository & Data Tests
Reuse `UserSettingsRepositoryTest`. No schema or storage changes and no new repository tests required.

## UI & Accessibility Tests

### `com.base.feature.settings.SettingsE2eUiTest`
Use the existing feature package per AGENTS.md. Use reusable fake components/factories and observable state. Extend existing tests where possible.

- Given settings with Tomans selected, When the user focuses Rials using keyboard navigation and activates it with a supported activation key, Then Rials has selected RadioButton semantics, Tomans is unselected, and public currency state is RIALS. Repeat in the reverse direction.
- Given settings with Gregorian selected, When the user focuses Persian using keyboard navigation and activates it, Then Persian has selected RadioButton semantics, Gregorian is unselected, and public calendar state is PERSIAN. Repeat in the reverse direction.
- Given an already selected currency or calendar card, When it is keyboard activated, Then it remains selected and its callback is invoked once. Callback counts are acceptable here; do not assert repository/service calls.
- Given cards rendered through existing test tags, When keyboard focus moves between them, Then each remains focusable and reachable. Preserve existing tags.

### Visual interaction verification (rounded settings option feedback)
- Given currency cards in selected and unselected states, When holding a pointer press near each visible rounded corner and observing the ripple progression, Then feedback never paints outside the 16.dp boundary. Repeat with each currency selected.
- Given calendar cards in selected and unselected states, When repeating the corner presses, Then feedback remains rounded. Repeat with each calendar selected.
- Given existing selected/unselected previews and the rendered settings screen, When comparing before and after, Then shape, dimensions, colors, padding and selected shadow are unchanged.

Record platform, build revision, and visual evidence/results. Static previews or passing semantics tests alone do not prove clipping. Explicitly report any visual verification limitation for handback.
