# Design

## Context
See proposal.md for motivation. `OptionSelectionCard` currently passes an outer `Modifier.selectable` to a noninteractive Material3 Surface. Surface's internal clipping therefore does not contain that indication. `DisplayCurrencyCard` and `DisplayCalendarSystemCard` share this component; selected cards have 1.dp shadow and unselected cards have none. Existing selected/unselected component previews and settings tests are available.

## Goals / Non-Goals
**Goals:** Put interaction feedback inside the shape boundary while retaining one interaction owner and the caller modifier/test tags.

**Non-Goals:** No redesign or preference/data-layer changes; no unrelated settings cleanup.

## Decisions
Prefer the Material3 selectable Surface overload (`selected`, `onClick`, `shape`) if supported by the pinned Compose version. Remove the outer selectable and retain RadioButton role explicitly if the overload's default role differs. This keeps Surface responsible for shape clipping, elevation and interaction. Confirm semantics in UI tests.

Alternative: keep the noninteractive Surface and place a shape-clipped selectable content container inside it. This preserves the outer shadow while enclosing indication, but adds modifier/layout complexity. Avoid placing clip before the outer Surface because it can crop its selected shadow; avoid adding a second click handler.

No dependency upgrades are needed. Keep shape, colors, padding, symbols, content, caller modifier and tags unchanged. Reuse existing previews to review selected/unselected states. This small design records the clipping-versus-shadow and role decisions before coding.

## Risks / Trade-offs
- Surface overload may change role or minimum touch sizing → explicitly preserve radio semantics and compare rendered dimensions.
- Static previews cannot prove ripple containment → inspect held presses and ripple progression near all corners for both currency and calendar options in both states.
- Selection tests cannot prove visual clipping → record separate visual evidence and any environment limitations.

## Migration Plan
No migration. Ship with normal client build; rollback by reverting the isolated UI fix.
