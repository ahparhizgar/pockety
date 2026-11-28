# Tasks

> DECIDED 2026-11-26 by Amir: middle path (Option B), refined in the
> WhatsApp design review. Design approved in `:design` (Catalog 08c–08g)
> before implementation.

## 0. Decisions (all settled 2026-11-26)

- [x] 0.1 Middle path: pocket creation gains a category-first simple
  mode; the classic form stays as a manual mode. Add Expense keeps its
  pocket picker.
- [x] 0.2 Simple mode: identity is live-linked to the chosen category —
  picking a category sets name/icon/color, picking another re-syncs all
  three. The link breaks only in manual mode.
- [x] 0.3 Manual mode is gated: tapping the quiet "Manual setup" row at
  the bottom opens a confirmation dialog; confirming swaps the whole
  screen to the manual form and the row disappears.
- [x] 0.4 Before saving, Back in manual mode returns to the simple mode
  (category stays selected; identity follows it again, manual tweaks are
  dropped).
- [x] 0.5 Manual mode can link several categories to one pocket.
  `pockets.categoryId` stays as the primary link (drives the card
  badge); all links live in a new `pocket_categories` table.
- [x] 0.6 Category deletion: cross-ref rows cascade-delete; the primary
  `categoryId` keeps its existing `SET_NULL` behaviour.

## 1. Design gate

- [x] 1.1 Category-first creation designed in `:design`
  (`features/addpocket`, Catalog 08c Category / 08d Category Selected /
  08e Manual / 08f Manual Toggle / 08g Manual Confirm) and approved by
  Amir 2026-11-26.

## 2. Data

- [x] 2.1 `PocketCategoryEntity` cross-ref table (PK pocketId+categoryId,
  CASCADE both sides) + Room migration 10 → 11 copying existing
  `categoryId` links.
- [x] 2.2 `PocketRepository.addPocket`/`updatePocket` take
  `categoryIds: List<Long>` (first entry is the primary, written to
  `pockets.categoryId`); links replaced transactionally.
  `PocketItemModel` gains `categoryIds`; new
  `getPocketCategoryIds(pocketId)`.
- [x] 2.3 Add Expense auto-pick matches any linked category
  (`category.id in pocket.categoryIds`).

## 3. Pocket creation UI

- [x] 3.1 `PocketSettingsComponent`: observe categories into state;
  simple mode inherits identity on category pick; manual-mode state,
  confirm-dialog state and multi-link toggling; Back returns to simple
  mode before save; save passes ordered links (category order).
- [x] 3.2 `PocketSettingsScreen`: simple layout (intro, identity +
  inherited note when selected, in-screen category list, allocation,
  manual row), confirm dialog, manual layout (identity, linked
  categories, color/icon pickers, allocation). Edit mode keeps the
  current layout; a multi-linked pocket shows the linked-categories
  section instead of the single category card.
- [x] 3.3 Strings (EN + FA) for intro, manual row, dialog, linked
  categories and the inherited note.

## 4. Tests

- [x] 4.1 Repository tests: links saved/replaced, primary derived,
  migration of call sites to `categoryIds`.
- [x] 4.2 Component tests: inheritance, re-sync on category change, no
  inheritance in edit mode, confirm gate, Back-to-simple, multi-link
  save.
- [x] 4.3 UI tests: simple flow (pick category → name filled → save),
  manual flow (dialog → toggle two categories → save).

## 5. Verification

- [x] 5.1 CI green; screenshot diff reviewed and mobile shots sent.
- [x] 5.2 Specs synced and change archived after Amir's sign-off (approved 2026-11-28).

## 6. Wizard revision (Amir, 2026-11-26, after testing the first build)

- [x] 6.1 Design reworked in :design: step 1 picks zero or more categories (with a
  "No Category" row + divider that proceeds immediately); exactly one pick leads to the
  simple form, zero or several lead to the advanced form; manual-setup entry stays in
  simple mode. Approved by Amir.
- [x] 6.2 App implementation: PocketCreationPhase in PocketSettingsComponent (CategoryPick
  -> Form), picks carry into links, Back returns to the pick step (or to simple when
  manual was entered from it), simple form shows the single followed category read-only.
