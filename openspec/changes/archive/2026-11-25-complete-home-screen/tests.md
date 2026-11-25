# Tests Specification

Tests live under `app/shared/src/commonTest/kotlin/com/base/feature/home/`
(per AGENTS.md), extending the existing home test files where they exist.

## Component Tests — `home/HomeComponentTest` (new)

- Given expenses in the current and previous calendar months
    - When the component state settles,
        - [ ] Then `monthlyOutflow` and `previousMonthOutflow` equal the
          per-month sums and the MoM percent/isSaved reflect the difference
- Given current-month expenses across 6 categories (incl. uncategorized)
    - When the state settles,
        - [ ] Then the breakdown holds the top 4 by amount plus an "Other"
          row aggregating the remaining groups, with shares summing to 1
- Given a previous-month total of zero
    - When the state settles,
        - [ ] Then `monthOverMonthPercent` is null
- Given pockets emitted by the repository
    - When the state settles,
        - [ ] Then `pockets` mirrors them with remaining balances
- Given the calendar setting is Persian
    - When expenses fall in the current Persian month only,
        - [ ] Then they count toward the current month

## UI Tests — `home/HomeE2eUiTest` (new)

- Given a populated fake state
    - When the screen renders,
        - [ ] Then the hero card, MoM card and pockets section exist with
          their test tags, and the pocket grid shows every pocket
- Given a populated fake state
    - When the date pill / All months / Manage All / a pocket card is clicked,
        - [ ] Then the matching fake-component callback is recorded
- Given an empty fake state (no pockets, zero totals)
    - When the screen renders,
        - [ ] Then the pockets section is absent and the MoM badge is absent
