# Design

## Context

Users can currently view budget pockets on `PocketsScreen` and create new pockets via `PocketSettingsFlowComponent`. However, tapping on a pocket card does not yet navigate anywhere (`onPocketClick` is a no-op in `MainComponent`). Additionally, while the pocket settings screen has a manual top-up section, there is no quick, in-context way to add funds to an existing pocket without entering the full edit flow.

See `proposal.md` for motivation and `specs/pocket-management/spec.md` for functional requirements.

## Goals / Non-Goals

**Goals:**
- Provide a responsive, adaptive `PocketDetailsScreen` matching the Verdant Clarity M3 Expressive design (`design-stitch/pocket_detail_pockety`).
- Wire navigation from `PocketsScreen` pocket items to `PocketDetailsComponent`, and from `PocketDetailsScreen` to `PocketSettingsFlowComponent` in edit mode.
- Implement an accessible Quick Top-up modal bottom sheet with preset quick-amount chips and custom numeric input.
- Persist quick top-up amounts to the database via `PocketRepository.topUpPocket(pocketId, amount)`.
- Render the transaction history feed UI layer with minimalist rows, item metadata, and interactive feedback.
- Provide comprehensive previews and test suites (unit, flow E2E, UI E2E).

**Non-Goals:**
- Querying or linking actual transactions from Room's `transactions` table (linking pocket transactions is a non-goal for this change).
- Automatic recurring monthly allocation refills or scheduling.
- Direct pocket deletion from the details screen (deletion is part of pocket settings lifecycle).

## Decisions

### 1. Navigation Placement: Root Destination Stack

- **Choice**: Register `PocketDetailsComponent.Config(val pocketId: Long) : TopLevelDestinationConfig` on `RootComponent`.
- **Rationale**: In Pockety's architecture, sub-flows and detail screens (`PocketSettingsFlowComponent`, `AddExpenseComponent`) are stacked on the root navigation while `MainComponent` manages bottom-bar tabs. Tapping a pocket from `MainComponent.Pockets` pushes `PocketDetailsComponent.Config` to root stack; tapping "Back" pops back to `MainComponent`.
- **Alternatives Considered**:
  - *Nested stack inside MainChildConfig.Pockets*: Would keep bottom bar visible, but Stitch design (`code.html`) specifies a full-screen mobile stack (`mobile_stack`) with dedicated back navigation and full-height transaction feed.

### 2. Quick Top-up Modal Bottom Sheet

- **Choice**: Material 3 `ModalBottomSheet` launched from the "Top-up" action button on `PocketDetailsScreen`.
- **Rationale**: User explicitly specified: "for quick top-up, we launch a bottom-sheet I think it's not designed." A bottom sheet keeps the user in the context of the pocket, allows instant 1-tap top-ups with preset chips (+50,000 T, +100,000 T, +200,000 T, +500,000 T), or typing a custom amount with full keyboard accessibility.
- **Alternatives Considered**:
  - *Dialog*: Less mobile-ergonomic for quick thumb-reach selections.
  - *In-place inline card*: Clutters the minimalist identity card.

### 3. Top-Up Persistence via PocketDao

- **Choice**: Add `@Query("UPDATE pockets SET budgetAmount = budgetAmount + :amount WHERE id = :id") suspend fun topUpPocket(id: Long, amount: Long)` to `PocketDao` and expose it via `PocketRepository`.
- **Rationale**: Since `budgetAmount` is already a column on `pockets` (Room schema v7), this query updates the balance directly without requiring a Room schema migration. Existing queries observing `pockets` automatically emit the new balance.
- **Alternatives Considered**:
  - *Loading entity and re-inserting with REPLACE*: Unnecessary roundtrips and race-condition prone.

### 4. Transactions Section: UI Layer Presentation

- **Choice**: Model `PocketTransactionUiModel` in `PocketDetailsState` with curated mock/sample transaction items matching the design spec.
- **Rationale**: User explicitly instructed: "showing the transactions data layer is a non goal because we don't have the linking yet, but we can implement the UI layer." Providing UI-level models and actions (such as delete feedback banner) validates the visual layout and interaction behavior without coupling to unlinked database models.
- **Alternatives Considered**:
  - *Leaving expenses section completely empty*: Fails to match design fidelity or verify list rendering.

### 5. Architectural Structure & AGENTS.md Compliance

- **Component**: `PocketDetailsComponent`, `DefaultPocketDetailsComponent`, `FakePocketDetailsComponent` in `com.base.capability.pocket_management`.
- **Screen**: `PocketDetailsScreen`, `PocketDetailsContent`, `QuickTopUpBottomSheet`, `PocketTransactionRow` in `com.base.capability.pocket_management`.
- **Adaptive layout**: Centered container with `Modifier.widthIn(max = 640.dp)`.
- **Previews**: Multiple `@ScreenPreviews` and `@ComponentPreviews` covering normal, quick-top-up open, empty, and confirmation banner states.

## Risks / Trade-offs

- **[Risk]** User navigates back while top-up is persisting.
  → **Mitigation**: `topUpPocket` is launched in `componentScope()`, ensuring completion even if UI transition starts.
- **[Risk]** Custom top-up text contains non-numeric characters or commas.
  → **Mitigation**: Strip commas and parse safely with `toLongOrNull() ?: 0L`, disabling confirm button when amount <= 0.
- **[Risk]** In edit mode, pocket settings modifies name/color/icon while pocket details is on the backstack.
  → **Mitigation**: `PocketDetailsComponent` observes `pocketRepository.observePockets()` or re-fetches pocket data, ensuring it displays updated pocket metadata upon returning from settings.
