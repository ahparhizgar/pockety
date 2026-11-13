# Design

## Context

See `proposal.md` for motivation and background.

In `:design` (commit `f098b35`), the designer implemented:
- `DisplayCalendarSystemCard`: A card with rounded corners (`16.dp`), surface container low background, a calendar header with `Icons.Default.CalendarMonth`, and a row with two `CalendarOptionCard`s spaced by `10.dp`.
- `CalendarOptionCard`: An interactive card with rounded corners (`16.dp`), elevated styling when selected, containing a circular symbol badge ("G" or "P"), selection checkmark / radio icon, title, and descriptive subtitle.

In `:app:shared`, `SettingsScreen.kt` currently uses basic `RadioButton` items inside a generic `Card`. The domain model defines `CalendarType` (`GREGORIAN`, `PERSIAN`), and `UserSettingsRepository` persists this selection into DataStore.

## Goals / Non-Goals

**Goals:**
- Port and adapt `DisplayCalendarSystemCard` and `CalendarOptionCard` to `:app:shared` under `com.base.feature.settings`.
- Integrate directly with `SettingsComponent` and domain `CalendarType`.
- Retain test tag identifiers (`calendar-option-gregorian` and `calendar-option-persian`) on the selectable option cards so UI tests and E2E tests remain robust.
- Provide comprehensive Compose Previews (`@ComponentPreviews` and `@ScreenPreviews`).
- Ensure keyboard accessibility and clickable/selectable role semantics.

**Non-Goals:**
- Modifying repository logic or DataStore persistence (already completed and verified in POC-20 / `c4ae290`).
- Adding prototype screen navigation (this belongs strictly to `:design`).
- Modifying other unrelated settings sections (e.g. notifications, currencies).

## Decisions

### 1. Component Placement
- **Decision**: Place `DisplayCalendarSystemCard` and `CalendarOptionCard` in `com.base.feature.settings` in `:app:shared`. They can either reside in a new `SettingsComponents.kt` file or alongside `SettingsScreen.kt`.
- **Rationale**: Keeps settings-specific UI encapsulated within the settings feature package while allowing reuse and clear separation of components and screen.
- **Alternatives Considered**: Putting them into global `com.base.ui.components`—rejected because calendar system options and styling are specific to Settings.

### 2. Domain Model Mapping & String Resources
- **Decision**: Define UI presentation properties (title string resource, subtitle string resource, symbol string) mapped from `CalendarType`.
- **Rationale**: Follows AGENTS.md guidelines: "Use string resources instead of hard-coded strings" and "Prefer enums over strings". Avoids duplicating enum definitions across modules.

### 3. Accessibility and Test Tags
- **Decision**: Apply `Modifier.testTag("calendar-option-gregorian")` and `Modifier.testTag("calendar-option-persian")` directly on the `CalendarOptionCard`s (or their selectable surface) with `role = Role.RadioButton`.
- **Rationale**: Maintains backward compatibility with `SettingsE2eUiTest` and accessibility services while adopting the new card design.

## Risks / Trade-offs

- **[Risk]** Layout overflow on narrow screens due to side-by-side option cards.
  → **Mitigation**: Use `Modifier.weight(1f)` for both cards inside the Row and ensure text styles use `titleMedium` / `labelSmall` with proper max lines or wrapping as defined in `:design`.
- **[Risk]** Regression in existing UI or flow tests.
  → **Mitigation**: Ensure test tags match existing tests and run `SettingsE2eUiTest`, `SettingsFlowE2eTest`, and `SettingsTest`.
