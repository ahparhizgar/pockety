# Design

## Context

In `app/shared`, `PocketBudgetCard` currently defines:
```kotlin
@Composable
fun PocketBudgetCard(
    pocket: PocketItemModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onTopUpClick: () -> Unit = {},
)
```
Inside `PocketBudgetCard`, `PocketCardActions(onTopUpClick = onTopUpClick)` renders a pill button with `Icons.Default.Add` and `stringResource(Res.string.pockets_top_up_action)`. In `PocketsScreen.kt`, it is invoked with `onTopUpClick = {}`.
However, top-up is already fully implemented within `PocketDetailsScreen` via `PocketDetailsComponents` and `QuickTopUpSheet`.

## Goals / Non-Goals

**Goals:**
- Remove `onTopUpClick` parameter from `PocketBudgetCard` in `app/shared/src/commonMain/kotlin/com/base/feature/pocket/PocketBudgetCard.kt`.
- Remove `PocketCardActions` composable and its invocation from `PocketBudgetCard`.
- Clean up unused imports (`Icons.Default.Add`, `pockets_top_up_action`) in `PocketBudgetCard.kt`.
- Remove `onTopUpClick = {}` from `PocketsScreen.kt`.
- Clean up or update any design prototype references if necessary so previews and modules stay consistent.

**Non-Goals:**
- Modifying top-up functionality in `PocketDetailsScreen` or repository logic.
- Altering the click behavior of the card itself (`onClick` navigates to pocket details).

## Decisions

- **Decision: Remove `PocketCardActions` completely rather than hiding or keeping an empty container.**
  - *Rationale*: There are currently no other actions on the pocket card. Keeping an empty row or container adds unnecessary composable layout nesting and padding.
  - *Alternatives considered*: Retaining an empty `PocketCardActions` slot. Rejected because there are no immediate actions planned on the list item cards and card click already opens details.

- **Decision: Keep `pockets_top_up_action` in strings.xml only if used elsewhere, otherwise remove or retain safely.**
  - *Rationale*: Checking string usages shows `pockets_top_up_action` is only referenced in `PocketBudgetCard.kt`. However, keeping or removing it should not break anything; removing cleans up dead resources.

## Risks / Trade-offs

- *[Risk]*: Previews or test cases relying on `onTopUpClick` parameter.
  - *Mitigation*: `PocketListE2eUiTest` and previews only interact with the card surface or FAB; they do not click the top-up action button. Removing `onTopUpClick` will not break tests.
